package com.sensecode.navigo.navigation_ui.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode

@Composable
fun GraphMapView(
    nodes: List<LocationNode>,
    edges: List<Edge>,
    currentNodeId: String?,
    routeNodeIds: List<String>,
    traversedNodeIds: List<String>,
    startNodeId: String?,
    destinationNodeId: String?,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // Pulsing animation for current node cursor
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Zoom and pan state
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val nodeRadiusDp = 12.dp
    val nodeRadius = with(density) { nodeRadiusDp.toPx() }

    val nodeMap = remember(nodes) { nodes.associateBy { it.id } }
    val routeNodeSet = remember(routeNodeIds) { routeNodeIds.toSet() }
    val traversedNodeSet = remember(traversedNodeIds) { traversedNodeIds.toSet() }

    // Build edge lookup for route
    val routeEdgeSet = remember(routeNodeIds) {
        if (routeNodeIds.size < 2) emptySet()
        else (0 until routeNodeIds.size - 1).map {
            routeNodeIds[it] to routeNodeIds[it + 1]
        }.toSet()
    }

    // Pre-measure a fixed text style to avoid repeated allocations during draw
    val labelFontSize = remember(scale) { (10 * scale).coerceIn(6f, 40f).sp }
    val labelStyle = remember(labelFontSize) {
        TextStyle(fontSize = labelFontSize, color = Color.Gray)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 4f)
                    offset += pan
                }
            }
            .semantics {
                contentDescription = "Graph map view showing ${nodes.size} locations and navigation route"
            }
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Guard against invalid canvas size (can happen during layout transitions)
        if (canvasWidth <= 0f || canvasHeight <= 0f) return@Canvas

        val padding = nodeRadius * 3

        // Visible bounds with generous margin for elements near edges
        val margin = nodeRadius * scale * 4
        val visibleRect = Rect(
            left = -margin,
            top = -margin,
            right = canvasWidth + margin,
            bottom = canvasHeight + margin
        )

        fun nodePosition(node: LocationNode): Offset {
            val x = padding + node.relativeX * (canvasWidth - 2 * padding)
            val y = padding + node.relativeY * (canvasHeight - 2 * padding)
            return Offset(x * scale + offset.x, y * scale + offset.y)
        }

        fun isVisible(pos: Offset): Boolean {
            return visibleRect.contains(pos)
        }

        // 1. Draw all edges as gray lines
        for (edge in edges) {
            val fromNode = nodeMap[edge.fromNodeId] ?: continue
            val toNode = nodeMap[edge.toNodeId] ?: continue
            val from = nodePosition(fromNode)
            val to = nodePosition(toNode)

            // Skip edges completely off-screen
            if (!isVisible(from) && !isVisible(to)) continue

            val isRouteEdge = (edge.fromNodeId to edge.toNodeId) in routeEdgeSet ||
                    (edge.toNodeId to edge.fromNodeId) in routeEdgeSet
            val isTraversed = edge.fromNodeId in traversedNodeSet && edge.toNodeId in traversedNodeSet

            val color = when {
                isRouteEdge && isTraversed -> Color.Gray.copy(alpha = 0.5f)
                isRouteEdge -> Color(0xFF2196F3) // Bright blue
                else -> Color.Gray.copy(alpha = 0.3f)
            }
            val strokeWidth = if (isRouteEdge) 4f * scale else 2f * scale

            drawLine(
                color = color,
                start = from,
                end = to,
                strokeWidth = strokeWidth
            )
        }

        // 2. Draw all nodes
        for (node in nodes) {
            val pos = nodePosition(node)

            // Skip nodes completely off-screen
            if (!isVisible(pos)) continue

            val isStart = node.id == startNodeId
            val isDest = node.id == destinationNodeId
            val isOnRoute = node.id in routeNodeSet
            val isTraversed = node.id in traversedNodeSet
            val isCurrent = node.id == currentNodeId

            val color = when {
                isStart -> Color(0xFF4CAF50) // Green
                isDest -> Color(0xFFF44336) // Red
                isOnRoute && !isTraversed -> Color(0xFF2196F3) // Blue
                isTraversed -> Color.Gray
                else -> Color.LightGray
            }

            val scaledRadius = nodeRadius * scale

            // Draw node circle
            drawCircle(
                color = color,
                radius = scaledRadius,
                center = pos
            )

            // Draw border
            drawCircle(
                color = Color.White,
                radius = scaledRadius,
                center = pos,
                style = Stroke(width = 2f * scale)
            )

            // Draw cursor glow for current node
            if (isCurrent) {
                val glowAlpha1 = pulseAlpha.coerceIn(0f, 1f)
                val glowAlpha2 = (pulseAlpha * 1.5f).coerceIn(0f, 1f)

                drawCircle(
                    color = Color(0xFFFF9800).copy(alpha = glowAlpha1),
                    radius = scaledRadius * pulseScale * 2,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFFFF9800).copy(alpha = glowAlpha2),
                    radius = scaledRadius * pulseScale * 1.3f,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFFFF9800),
                    radius = scaledRadius * 1.1f,
                    center = pos
                )
            }

            // Draw node label — wrapped in try-catch to prevent crash on extreme zoom
            try {
                val label = if (node.name.length > 12) node.name.take(10) + "…" else node.name
                val textTopLeft = Offset(
                    pos.x - 30f * scale,
                    pos.y + scaledRadius + 4f * scale
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = textTopLeft,
                    style = labelStyle
                )
            } catch (_: Exception) {
                // Skip label on error — prevents crash during rapid zoom
            }
        }
    }
}
