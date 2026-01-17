package me.algosketch.aioninfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TODO: 영웅 등급은 따로 작성해야 함.
private val successPercent = listOf(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 65, 50, 35, 25, 20)
private val stones = listOf(420, 600, 1100, 1680, 2350, 3090, 3890, 4760, 5670, 6650, 7670, 8730, 9850, 11010, 12200)
private val kina = listOf(10500, 15000, 27500, 42000, 59000, 87000, 98000, 119000, 142000, 167000, 300000, 440000, 710000, 1110000, 1530000)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        var selectedLevel by remember { mutableStateOf(50) }
        var expandedLevel by remember { mutableStateOf(false) }

        var currentLevel by remember { mutableStateOf(0) }
        var expandedCurrent by remember { mutableStateOf(false) }

        var targetLevel by remember { mutableStateOf(0) }
        var expandedTarget by remember { mutableStateOf(false) }

        // TextField states
        var currentEnhancement by remember { mutableStateOf("") }
        var targetEnhancement by remember { mutableStateOf("") }

        val levels = listOf(55, 57, 67, 84)

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Aion 2 Enhancement Calculator",
                style = MaterialTheme.typography.headlineMedium
            )

            ExposedDropdownMenuBox(
                expanded = expandedLevel,
                onExpandedChange = { expandedLevel = it }
            ) {
                OutlinedTextField(
                    value = currentLevel.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Item Level") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLevel) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedLevel,
                    onDismissRequest = { expandedLevel = false }
                ) {
                    levels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.toString()) },
                            onClick = {
                                currentLevel = level
                                expandedLevel = false
                            }
                        )
                    }
                }
            }

            Row {
                // 현재 단계
                ExposedDropdownMenuBox(
                    modifier = Modifier.width(500.dp),
                    expanded = expandedCurrent,
                    onExpandedChange = { expandedCurrent = it }
                ) {
                    OutlinedTextField(
                        value = targetLevel.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("123") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCurrent) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCurrent,
                        onDismissRequest = { expandedCurrent = false }
                    ) {
                        levels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.toString()) },
                                onClick = {
                                    targetLevel = level
                                    expandedCurrent = false
                                }
                            )
                        }
                    }
                }

                // 목표 단계
                ExposedDropdownMenuBox(
                    modifier = Modifier.width(500.dp),
                    expanded = expandedTarget,
                    onExpandedChange = { expandedLevel = it }
                ) {
                    OutlinedTextField(
                        value = selectedLevel.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("4") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTarget) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTarget,
                        onDismissRequest = { expandedTarget = false }
                    ) {
                        levels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.toString()) },
                                onClick = {
                                    selectedLevel = level
                                    expandedTarget = false
                                }
                            )
                        }
                    }
                }
            }



            Button(
                onClick = {
                    println("Level: $selectedLevel")
                    println("Current: $currentEnhancement")
                    println("Target: $targetEnhancement")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Calculate Cost")
            }
        }
    }
}
