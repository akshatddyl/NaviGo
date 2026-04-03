package com.sensecode.navigo.setup.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeDropDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, accessible: Boolean, hasStairs: Boolean, hasElevator: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("room") }
    var accessible by remember { mutableStateOf(true) }
    var hasStairs by remember { mutableStateOf(false) }
    var hasElevator by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val nodeTypes = listOf("room", "junction", "entrance", "exit", "staircase", "elevator", "toilet", "canteen")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Drop Node",
                fontSize = 22.sp,
                modifier = Modifier.semantics { contentDescription = "Drop node dialog. Fill in the location details." }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .semantics { contentDescription = "Node details form" }
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Location Name", fontSize = 16.sp) },
                    placeholder = { Text("e.g., Room 101, Main Lobby") },
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Enter location name, for example Room 101 or Main Lobby" },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type", fontSize = 16.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .semantics { contentDescription = "Select node type. Currently selected: $selectedType" }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        nodeTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.replaceFirstChar { it.uppercase() }, fontSize = 18.sp) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                    hasStairs = type == "staircase"
                                    hasElevator = type == "elevator"
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .semantics { contentDescription = "Select node type: $type" }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("Wheelchair Accessible", fontSize = 18.sp)
                    Switch(
                        checked = accessible,
                        onCheckedChange = { accessible = it },
                        modifier = Modifier.semantics {
                            contentDescription = "Wheelchair accessible: ${if (accessible) "enabled" else "disabled"}. Toggle to change."
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("Has Stairs", fontSize = 18.sp)
                    Switch(
                        checked = hasStairs,
                        onCheckedChange = { hasStairs = it },
                        modifier = Modifier.semantics {
                            contentDescription = "Has stairs: ${if (hasStairs) "enabled" else "disabled"}. Toggle to change."
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("Has Elevator", fontSize = 18.sp)
                    Switch(
                        checked = hasElevator,
                        onCheckedChange = { hasElevator = it },
                        modifier = Modifier.semantics {
                            contentDescription = "Has elevator: ${if (hasElevator) "enabled" else "disabled"}. Toggle to change."
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, selectedType, accessible, hasStairs, hasElevator) },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .height(48.dp)
                    .semantics { contentDescription = "Confirm and drop node named $name" }
            ) {
                Text("Drop", fontSize = 18.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .height(48.dp)
                    .semantics { contentDescription = "Cancel and close dialog" }
            ) {
                Text("Cancel", fontSize = 18.sp)
            }
        }
    )
}
