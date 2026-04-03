package com.sensecode.navigo.domain.algorithm

import com.sensecode.navigo.domain.model.Edge
import com.sensecode.navigo.domain.model.LocationNode
import com.sensecode.navigo.domain.model.Route
import java.util.PriorityQueue

object Dijkstra {

    fun findShortestPath(
        startNodeId: String,
        destinationNodeId: String,
        nodes: List<LocationNode>,
        edges: List<Edge>,
        accessibleOnly: Boolean = false
    ): Route? {
        if (startNodeId == destinationNodeId) {
            val node = nodes.find { it.id == startNodeId } ?: return null
            return Route(
                nodes = listOf(node),
                edges = emptyList(),
                totalDistanceM = 0f,
                estimatedMinutes = 0,
                isAccessible = true
            )
        }

        val nodeMap = nodes.associateBy { it.id }
        if (!nodeMap.containsKey(startNodeId) || !nodeMap.containsKey(destinationNodeId)) {
            return null
        }

        // Build adjacency list
        val adjacencyList = mutableMapOf<String, MutableList<Edge>>()
        for (edge in edges) {
            if (accessibleOnly && edge.hasStairs) continue
            adjacencyList.getOrPut(edge.fromNodeId) { mutableListOf() }.add(edge)
        }

        // Dijkstra's algorithm
        val distances = mutableMapOf<String, Float>()
        val previousNodes = mutableMapOf<String, String>()
        val previousEdges = mutableMapOf<String, Edge>()
        val visited = mutableSetOf<String>()

        // Priority queue: Pair(distance, nodeId)
        val pq = PriorityQueue<Pair<Float, String>>(compareBy { it.first })

        distances[startNodeId] = 0f
        pq.add(0f to startNodeId)

        while (pq.isNotEmpty()) {
            val (currentDist, currentNodeId) = pq.poll()

            if (currentNodeId in visited) continue
            visited.add(currentNodeId)

            if (currentNodeId == destinationNodeId) break

            val neighbors = adjacencyList[currentNodeId] ?: continue
            for (edge in neighbors) {
                if (edge.toNodeId in visited) continue
                val newDist = currentDist + edge.distanceM
                if (newDist < (distances[edge.toNodeId] ?: Float.MAX_VALUE)) {
                    distances[edge.toNodeId] = newDist
                    previousNodes[edge.toNodeId] = currentNodeId
                    previousEdges[edge.toNodeId] = edge
                    pq.add(newDist to edge.toNodeId)
                }
            }
        }

        // Reconstruct path
        if (destinationNodeId !in previousNodes && startNodeId != destinationNodeId) {
            return null
        }

        val pathNodeIds = mutableListOf<String>()
        var current = destinationNodeId
        while (current != startNodeId) {
            pathNodeIds.add(current)
            current = previousNodes[current] ?: return null
        }
        pathNodeIds.add(startNodeId)
        pathNodeIds.reverse()

        val routeNodes = pathNodeIds.mapNotNull { nodeMap[it] }
        val routeEdges = mutableListOf<Edge>()
        for (i in 0 until pathNodeIds.size - 1) {
            val edge = previousEdges[pathNodeIds[i + 1]] ?: return null
            routeEdges.add(edge)
        }

        val totalDistance = routeEdges.sumOf { it.distanceM.toDouble() }.toFloat()
        val totalSeconds = routeEdges.sumOf { it.estimatedSeconds }
        val isAccessible = routeEdges.none { it.hasStairs }

        return Route(
            nodes = routeNodes,
            edges = routeEdges,
            totalDistanceM = totalDistance,
            estimatedMinutes = (totalSeconds / 60) + if (totalSeconds % 60 > 0) 1 else 0,
            isAccessible = isAccessible
        )
    }
}
