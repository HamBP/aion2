package me.algosketch.aioninfo.presentation.feature.enhancement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.algosketch.aioninfo.data.enhancement.EnhancementRepository

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

    val levels = remember { EnhancementRepository.items.map { it.itemLevel } }

    var stonesData by remember { mutableStateOf(emptyList<Int>()) }
    var kinaData by remember { mutableStateOf(emptyList<Int>()) }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "아이온2 강화 시뮬레이터",
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
                label = { Text("아이템 레벨") },
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
                            selectedLevel = level
                            expandedLevel = false
                        }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    label = { Text("현재 강화 단계") },
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
                    label = { Text("목표 강화 단계") },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = {
                val simulations = 10000
                val resultStonesList = mutableListOf<Int>()
                val resultKinaList = mutableListOf<Int>()

                repeat(simulations) {
                    val (enhancement, breakthrough) = viewModel.simulate(
                        itemLevel = selectedLevel,
                        startEnhancement = EnhancementLevel(enhancementLevel = currentLevel, breakthroughLevel = 0),
                        targetEnhancement = EnhancementLevel(enhancementLevel = targetLevel, breakthroughLevel = 0),
                    )

                    resultStonesList.add(enhancement.stones)
                    resultKinaList.add(enhancement.kina)
                }

                stonesData = resultStonesList
                kinaData = resultKinaList
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color(0xFF222222)
            ),
        ) {
            Text("계산하기")
        }

        if (stonesData.isNotEmpty()) {
            DualCdfChart(
                stonesData = stonesData,
                kinaData = kinaData
            )
        }
    }
}
