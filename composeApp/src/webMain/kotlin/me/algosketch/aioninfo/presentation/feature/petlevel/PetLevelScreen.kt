package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    val requirements by viewModel.requirements.collectAsState()

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
            text = "펫 이해도작 시뮬레이터",
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

        // 이미 잠긴 슬롯 수
        Text("이미 잠긴 슬롯 수", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "굴리기 전부터 잠겨 있는 슬롯 수입니다. 비용 계산에 반영됩니다.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (0..8).forEach { count ->
                FilterChip(
                    selected = count == viewModel.preLockedSlots,
                    onClick = { viewModel.setPreLockedSlots(count) },
                    label = { Text("${count}개") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF222222),
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }

        // 목표 설정 - 슬롯 타입별 3개 섹션
        Text("목표 설정", style = MaterialTheme.typography.titleMedium)

        RequirementSection(
            title = "일반 슬롯",
            subtitle = "슬롯 1, 2, 4, 5, 7, 8 · 최대 6개",
            slotType = SlotType.NORMAL,
            requirements = requirements.filter { it.slotType == SlotType.NORMAL },
            maxTotalCount = 6,
            onUpdateRequirements = { viewModel.updateRequirementsForType(SlotType.NORMAL, it) },
        )

        RequirementSection(
            title = "특수 슬롯 3, 9",
            subtitle = "슬롯 3, 9 · 최대 2개",
            slotType = SlotType.SPECIAL_39,
            requirements = requirements.filter { it.slotType == SlotType.SPECIAL_39 },
            maxTotalCount = 2,
            onUpdateRequirements = { viewModel.updateRequirementsForType(SlotType.SPECIAL_39, it) },
        )

        RequirementSection(
            title = "특수 슬롯 6",
            subtitle = "슬롯 6 · 최대 1개",
            slotType = SlotType.SPECIAL_6,
            requirements = requirements.filter { it.slotType == SlotType.SPECIAL_6 },
            maxTotalCount = 1,
            onUpdateRequirements = { viewModel.updateRequirementsForType(SlotType.SPECIAL_6, it) },
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.calculate() },
            enabled = requirements.isNotEmpty() && !viewModel.isCalculating,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
        ) {
            if (viewModel.isCalculating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
                Text("계산 중...")
            } else {
                Text("계산하기")
            }
        }

        viewModel.result?.let { result ->
            HorizontalDivider()

            Text("예상 비용", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "1,000회 시뮬레이션 기반",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequirementSection(
    title: String,
    subtitle: String,
    slotType: SlotType,
    requirements: List<OptionRequirement>,
    maxTotalCount: Int,
    onUpdateRequirements: (List<OptionRequirement>) -> Unit,
) {
    val totalCount = requirements.sumOf { it.count }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            if (totalCount > maxTotalCount) {
                Text(
                    text = "목표 슬롯 합계(${totalCount})가 최대(${maxTotalCount})를 초과합니다.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFCC0000),
                )
            }

            requirements.forEachIndexed { i, req ->
                RequirementRow(
                    req = req,
                    availableOptions = slotType.validOptions,
                    showCount = maxTotalCount > 1,
                    onUpdate = { updated ->
                        onUpdateRequirements(requirements.mapIndexed { idx, r -> if (idx == i) updated else r })
                    },
                    onDelete = {
                        onUpdateRequirements(requirements.filterIndexed { idx, _ -> idx != i })
                    },
                )
            }

            if (totalCount < maxTotalCount) {
                OutlinedButton(
                    onClick = {
                        onUpdateRequirements(
                            requirements + OptionRequirement(
                                slotType = slotType,
                                option = slotType.validOptions.first(),
                                minValue = 0.0,
                                count = 1,
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("+ 목표 추가")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequirementRow(
    req: OptionRequirement,
    availableOptions: List<PetOption>,
    onUpdate: (OptionRequirement) -> Unit,
    onDelete: () -> Unit,
    showCount: Boolean = true,
) {
    var expandedOption by remember { mutableStateOf(false) }
    var minValueText by remember(req.minValue) { mutableStateOf(formatDouble(req.minValue)) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.weight(2f),
            expanded = expandedOption,
            onExpandedChange = { expandedOption = it },
        ) {
            OutlinedTextField(
                value = req.option.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("옵션", fontSize = 12.sp) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOption)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            )
            ExposedDropdownMenu(
                expanded = expandedOption,
                onDismissRequest = { expandedOption = false },
            ) {
                availableOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onUpdate(req.copy(option = option, minValue = 0.0))
                            expandedOption = false
                        },
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = minValueText,
            onValueChange = { input ->
                val filtered = filterDoubleInput(input)
                minValueText = filtered
                onUpdate(req.copy(minValue = filtered.toDoubleOrNull() ?: 0.0))
            },
            label = { Text("이상", fontSize = 12.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
        )

        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = req.count.toString(),
            onValueChange = { input ->
                val value = input.filter { it.isDigit() }.toIntOrNull() ?: 1
                onUpdate(req.copy(count = value.coerceAtLeast(1)))
            },
            label = { Text("개", fontSize = 12.sp) },
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            readOnly = !showCount,
        )

        IconButton(onClick = onDelete) {
            Text("✕", color = Color.Gray)
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

// 소수점 불필요 시 정수로 표시 (0.0 → "", 30.0 → "30", 1.5 → "1.5")
private fun formatDouble(value: Double): String = when {
    value == 0.0 -> ""
    value % 1.0 == 0.0 -> value.toInt().toString()
    else -> value.toString()
}

// 숫자와 소수점 하나만 허용
private fun filterDoubleInput(input: String): String {
    var dotSeen = false
    return input.filter { c ->
        when {
            c.isDigit() -> true
            c == '.' && !dotSeen -> { dotSeen = true; true }
            else -> false
        }
    }
}