package me.algosketch.aioninfo.enhancement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

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

    var top5 by remember { mutableStateOf(0 to 0) }
    var middle by remember { mutableStateOf(0 to 0) }
    var bottom5 by remember { mutableStateOf(0 to 0) }

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

                val resultStones = resultStonesList.sorted()
                val resultKina = resultKinaList.sorted()

                val index5 = simulations * 5 / 100 - 1
                val index50 = simulations / 2 - 1
                val index95 = simulations * 95 / 100 - 1

                top5 = resultStones[index5] to resultKina[index5]
                middle = resultStones[index50] to resultKina[index50]
                bottom5 = resultStones[index95] to resultKina[index95]
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Calculate Cost")
        }

        Text(text = "result")
        Text(text = "5 : ${top5.first}, ${top5.second}")
        Text(text = "50 : ${middle.first}, ${middle.second}")
        Text(text = "95 : ${bottom5.first}, ${bottom5.second}")
    }
}
