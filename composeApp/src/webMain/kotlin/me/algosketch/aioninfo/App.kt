package me.algosketch.aioninfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme {
        // Dropdown menu states
        var selectedGrade by remember { mutableStateOf("Heroic") }
        var expandedGrade by remember { mutableStateOf(false) }

        var selectedType by remember { mutableStateOf("Weapon") }
        var expandedType by remember { mutableStateOf(false) }

        var selectedLevel by remember { mutableStateOf("50") }
        var expandedLevel by remember { mutableStateOf(false) }

        // TextField states
        var currentEnhancement by remember { mutableStateOf("") }
        var targetEnhancement by remember { mutableStateOf("") }

        // Grade, type, and level options
        val grades = listOf("Normal", "Advanced", "Rare", "Heroic", "Legendary", "Mythic")
        val types = listOf("Weapon", "Armor", "Accessory")
        val levels = listOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100")

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

            // Dropdown 1: Item Grade
            ExposedDropdownMenuBox(
                expanded = expandedGrade,
                onExpandedChange = { expandedGrade = it }
            ) {
                OutlinedTextField(
                    value = selectedGrade,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Item Grade") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrade) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedGrade,
                    onDismissRequest = { expandedGrade = false }
                ) {
                    grades.forEach { grade ->
                        DropdownMenuItem(
                            text = { Text(grade) },
                            onClick = {
                                selectedGrade = grade
                                expandedGrade = false
                            }
                        )
                    }
                }
            }

            // Dropdown 2: Item Type
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = it }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Item Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                expandedType = false
                            }
                        )
                    }
                }
            }

            // Dropdown 3: Item Level
            ExposedDropdownMenuBox(
                expanded = expandedLevel,
                onExpandedChange = { expandedLevel = it }
            ) {
                OutlinedTextField(
                    value = selectedLevel,
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
                            text = { Text(level) },
                            onClick = {
                                selectedLevel = level
                                expandedLevel = false
                            }
                        )
                    }
                }
            }

            // TextField 1: Current Enhancement
            OutlinedTextField(
                value = currentEnhancement,
                onValueChange = { currentEnhancement = it },
                label = { Text("Current Enhancement") },
                placeholder = { Text("0~14") },
                modifier = Modifier.fillMaxWidth()
            )

            // TextField 2: Target Enhancement
            OutlinedTextField(
                value = targetEnhancement,
                onValueChange = { targetEnhancement = it },
                label = { Text("Target Enhancement") },
                placeholder = { Text("1~15") },
                modifier = Modifier.fillMaxWidth()
            )

            // Button: Calculate
            Button(
                onClick = {
                    // Calculation logic goes here
                    println("Grade: $selectedGrade")
                    println("Type: $selectedType")
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
