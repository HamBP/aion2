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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.algosketch.aioninfo.data.enhancement.EnhancementRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancementScreen(
    viewModel: EnhancementViewModel = EnhancementViewModel() // TODO: DI
) {
    var selectedLevel by remember { mutableStateOf(50) }
    val isUnique by remember { derivedStateOf { selectedLevel <= 92 } }

    var expandedLevel by remember { mutableStateOf(false) }

    var enhancementLevels by remember { mutableStateOf(uniqueEnhancementLevels)}

    var currentEnhancement by remember { mutableStateOf(EnhancementLevel(0, 0)) }
    var expandedCurrent by remember { mutableStateOf(false) }

    var targetEnhancement by remember { mutableStateOf(EnhancementLevel(0, 0)) }
    var expandedTarget by remember { mutableStateOf(false) }

    val levels = remember { EnhancementRepository.items.map { it.itemLevel } }

    var stonesData by remember { mutableStateOf(emptyList<Int>()) }
    var kinaData by remember { mutableStateOf(emptyList<Int>()) }
    var breakthroughStonesData by remember { mutableStateOf(emptyList<Int>()) }
    var breakthroughKinaData by remember { mutableStateOf(emptyList<Int>()) }

    LaunchedEffect(levels) {
        selectedLevel = levels.first()
    }

    LaunchedEffect(isUnique) {
        targetEnhancement = if (isUnique) EnhancementLevel(15, 5) else EnhancementLevel(20, 5)
        enhancementLevels = if (isUnique) uniqueEnhancementLevels else heroEnhancementLevels
    }

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "아이온2 강화 시뮬레이터",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        ExposedDropdownMenuBox(
            expanded = expandedLevel,
            onExpandedChange = { expandedLevel = it }
        ) {
            OutlinedTextField(
                value = selectedLevel.toString(),
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
                    value = currentEnhancement.toString(),
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
                    enhancementLevels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.toString()) },
                            onClick = {
                                currentEnhancement = level
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
                    value = targetEnhancement.toString(),
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
                    enhancementLevels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.toString()) },
                            onClick = {
                                targetEnhancement = level
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
                val resultBreakthroughStonesList = mutableListOf<Int>()
                val resultBreakthroughKinaList = mutableListOf<Int>()

                repeat(simulations) {
                    val (enhancement, breakthrough) = viewModel.simulate(
                        itemLevel = selectedLevel,
                        startEnhancement = currentEnhancement,
                        targetEnhancement = targetEnhancement,
                    )

                    resultStonesList.add(enhancement.stones)
                    resultKinaList.add(enhancement.kina)
                    resultBreakthroughStonesList.add(breakthrough.stones)
                    resultBreakthroughKinaList.add(breakthrough.kina)
                }

                stonesData = resultStonesList
                kinaData = resultKinaList
                breakthroughStonesData = resultBreakthroughStonesList
                breakthroughKinaData = resultBreakthroughKinaList
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color(0xFF222222)
            ),
        ) {
            Text("계산하기")
        }

        if (stonesData.isNotEmpty()) {
            val avgStones = stonesData.average().toLong()
            val avgKina = kinaData.average().toLong()
            val avgBreakthroughStones = breakthroughStonesData.average().toLong()
            val avgBreakthroughKina = breakthroughKinaData.average().toLong()

            Text(
                "총 예상 비용",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Column {
                Text("강화석: ${formatNumberWithComma(avgStones)}개")
                Text("돌파석: ${formatNumberWithComma(avgBreakthroughStones)}개")
                Text("키나: ${formatNumberWithComma(avgKina + avgBreakthroughKina)}")
            }

            Text(
                "강화 비용 분포",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "예상 비용",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Column {
                Text("강화석: ${formatNumberWithComma(avgStones)}개")
                Text("키나: ${formatNumberWithComma(avgKina)}")
            }
            DualCdfChart(
                stonesData = stonesData,
                kinaData = kinaData,
            )

            Text(
                "돌파 비용 분포",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "예상 비용",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Column {
                Text("돌파석: ${formatNumberWithComma(avgBreakthroughStones)}개")
                Text("키나: ${formatNumberWithComma(avgBreakthroughKina)}")
            }
            DualCdfChart(
                stonesData = breakthroughStonesData,
                kinaData = breakthroughKinaData,
                stonesLabel = "돌파석",
            )
        }
    }
}

private fun formatNumberWithComma(value: Long): String {
    return value.toString().reversed().chunked(3).joinToString(",").reversed()
}
