package me.algosketch.aioninfo.presentation.feature.enhancement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancementScreen(
    viewModel: EnhancementViewModel = EnhancementViewModel() // TODO: DI
) {
    var selectedLevel by remember { mutableStateOf(50) }
    var expandedLevel by remember { mutableStateOf(false) }

    var currentLevel by remember { mutableStateOf(0) }
    var expandedCurrent by remember { mutableStateOf(false) }

    var targetLevel by remember { mutableStateOf(0) }
    var expandedTarget by remember { mutableStateOf(false) }

    val levels = listOf(67, 84)

    var stonesData by remember { mutableStateOf(emptyList<Int>()) }
    var kinaData by remember { mutableStateOf(emptyList<Int>()) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .safeContentPadding()
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
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
                modifier = Modifier.weight(1f),
                expanded = expandedCurrent,
                onExpandedChange = { expandedCurrent = it }
            ) {
                OutlinedTextField(
                    value = currentLevel.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("current level") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCurrent) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedCurrent,
                    onDismissRequest = { expandedCurrent = false }
                ) {
                    (0..15).forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.toString()) },
                            onClick = {
                                currentLevel = level
                                expandedCurrent = false
                            }
                        )
                    }
                }
            }

            // 목표 단계
            ExposedDropdownMenuBox(
                modifier = Modifier.weight(1f),
                expanded = expandedTarget,
                onExpandedChange = { expandedTarget = it }
            ) {
                OutlinedTextField(
                    value = targetLevel.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("target level") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTarget) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedTarget,
                    onDismissRequest = { expandedTarget = false }
                ) {
                    (0..15).forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.toString()) },
                            onClick = {
                                targetLevel = level
                                expandedTarget = false
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val simulations = 10000
                val resultStonesList = mutableListOf<Int>()
                val resultKinaList = mutableListOf<Int>()

                repeat(simulations) {
                    val (stones, kina) = viewModel.simulate(currentLevel, targetLevel)

                    resultStonesList.add(stones)
                    resultKinaList.add(kina)
                }

                stonesData = resultStonesList
                kinaData = resultKinaList
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Calculate Cost")
        }

        if (stonesData.isNotEmpty()) {
            DualCdfChart(
                stonesData = stonesData,
                kinaData = kinaData
            )
        }
    }
}
