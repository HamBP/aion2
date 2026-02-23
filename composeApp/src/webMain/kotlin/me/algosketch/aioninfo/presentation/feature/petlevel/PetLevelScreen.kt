package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetLevelScreen(viewModel: PetLevelViewModel = viewModel { PetLevelViewModel() }) {
    var editingSlotIndex by remember { mutableStateOf<Int?>(null) }
    val slots by viewModel.slots.collectAsState()

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "펫 이해도작 기댓값 계산기",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )

        // 종족 선택
        Text("펫 종족", style = MaterialTheme.typography.titleMedium)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            PetRace.entries.forEach { race ->
                FilterChip(
                    selected = race == viewModel.selectedRace,
                    onClick = { viewModel.selectRace(race) },
                    label = { Text(race.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF222222),
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }

        // 슬롯 그리드
        Text("이해도 슬롯", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0 until 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        val slotNumber = index + 1
                        SlotCard(
                            modifier = Modifier.weight(1f),
                            slotNumber = slotNumber,
                            isSpecial = slotNumber % 3 == 0,
                            slotConfig = slots[index],
                            onClick = { editingSlotIndex = index },
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            onClick = { viewModel.calculate() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
        ) {
            Text("계산하기")
        }

        viewModel.result?.let { result ->
            HorizontalDivider()

            Text("예상 비용", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ResultCard(
                    modifier = Modifier.weight(1f),
                    label = "평균 결정",
                    value = "${formatNumber(result.avgCrystals.toLong())}개",
                )
                ResultCard(
                    modifier = Modifier.weight(1f),
                    label = "평균 키나",
                    value = formatNumber(result.avgKina.toLong()),
                )
            }

            Text("비용 분포 (결정 기준)", style = MaterialTheme.typography.titleMedium)
            PercentileTable(data = result.crystalsData, unit = "개")
        }
    }

    editingSlotIndex?.let { index ->
        SlotEditDialog(
            slotNumber = index + 1,
            isSpecial = (index + 1) % 3 == 0,
            slotConfig = slots[index],
            onDismiss = { editingSlotIndex = null },
            onConfirm = { newConfig ->
                viewModel.updateSlot(index, newConfig)
                editingSlotIndex = null
            },
        )
    }
}

@Composable
private fun SlotCard(
    modifier: Modifier = Modifier,
    slotNumber: Int,
    isSpecial: Boolean,
    slotConfig: SlotConfig,
    onClick: () -> Unit,
) {
    val borderColor = if (isSpecial) Color(0xFF9B59B6) else Color(0xFFCCCCCC)
    val bgColor = if (isSpecial) Color(0xFFF5EEFF) else Color(0xFFF9F9F9)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = bgColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "슬롯 $slotNumber",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            if (slotConfig.candidates.isEmpty()) {
                Text(
                    text = "미설정",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
            } else {
                slotConfig.candidates.forEach { candidate ->
                    Text(
                        text = "${candidate.option.displayName} ${candidate.minValue}+",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PercentileTable(data: List<Int>, unit: String) {
    val sorted = remember(data) { data.sorted() }
    val percentiles = listOf(25, 50, 75, 90, 99)

    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            percentiles.forEach { p ->
                val index = ((p / 100.0) * sorted.size).toInt().coerceAtMost(sorted.size - 1)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${p}% 이하", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(
                        "${formatNumber(sorted[index].toLong())}$unit",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun formatNumber(value: Long): String =
    value.toString().reversed().chunked(3).joinToString(",").reversed()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlotEditDialog(
    slotNumber: Int,
    isSpecial: Boolean,
    slotConfig: SlotConfig,
    onDismiss: () -> Unit,
    onConfirm: (SlotConfig) -> Unit,
) {
    var candidates by remember { mutableStateOf(slotConfig.candidates) }
    var expandedOptionIndex by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "슬롯 $slotNumber 설정${if (isSpecial) " ★특수" else ""}",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "후보 옵션 중 하나라도 조건을 만족하면 유효 슬롯으로 인정합니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                candidates.forEachIndexed { i, candidate ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ExposedDropdownMenuBox(
                            modifier = Modifier.weight(1f),
                            expanded = expandedOptionIndex == i,
                            onExpandedChange = { expanded ->
                                expandedOptionIndex = if (expanded) i else null
                            },
                        ) {
                            OutlinedTextField(
                                value = candidate.option.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("옵션", fontSize = 12.sp) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOptionIndex == i)
                                },
                                modifier = Modifier.fillMaxWidth()
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                            )
                            ExposedDropdownMenu(
                                expanded = expandedOptionIndex == i,
                                onDismissRequest = { expandedOptionIndex = null },
                            ) {
                                PetOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.displayName) },
                                        onClick = {
                                            candidates = candidates.mapIndexed { idx, c ->
                                                if (idx == i) c.copy(option = option) else c
                                            }
                                            expandedOptionIndex = null
                                        },
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            modifier = Modifier.width(72.dp),
                            value = if (candidate.minValue == 0) "" else candidate.minValue.toString(),
                            onValueChange = { input ->
                                val value = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                                candidates = candidates.mapIndexed { idx, c ->
                                    if (idx == i) c.copy(minValue = value) else c
                                }
                            },
                            label = { Text("이상", fontSize = 12.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                        )

                        IconButton(
                            onClick = {
                                candidates = candidates.filterIndexed { idx, _ -> idx != i }
                            },
                        ) {
                            Text("✕", color = Color.Gray)
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        candidates = candidates + OptionCandidate(PetOption.ADDITIONAL_ACCURACY, 0)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("+ 후보 옵션 추가")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(SlotConfig(candidates = candidates)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
    )
}