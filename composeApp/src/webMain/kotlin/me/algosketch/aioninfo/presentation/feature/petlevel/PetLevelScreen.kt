package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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

enum class PetRace(val displayName: String) {
    INTELLECT("지성"),
    WILD("야성"),
    NATURE("자연"),
    TRANSFORM("변형"),
    SPECIAL("특수"),
}

// 더미 옵션 목록 - 추후 실제 데이터로 교체
enum class PetOption(val displayName: String) {
    ACCURACY("명중"),
    EVASION("회피"),
    CRITICAL("치명타"),
    ATTACK("공격력"),
    DEFENSE("방어력"),
    HP("체력"),
    MAGIC_ATTACK("마법 공격력"),
    MAGIC_DEFENSE("마법 방어력"),
}

data class OptionCandidate(
    val option: PetOption,
    val minValue: Int,
)

data class SlotConfig(
    val candidates: List<OptionCandidate> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetLevelScreen(petLevelViewModel: PetLevelViewModel = PetLevelViewModel()) {
    var selectedRace by remember { mutableStateOf(PetRace.INTELLECT) }
    var slots by remember { mutableStateOf(List(9) { SlotConfig() }) }
    var editingSlotIndex by remember { mutableStateOf<Int?>(null) }

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
                    selected = race == selectedRace,
                    onClick = { selectedRace = race },
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
            onClick = { /* TODO: 기댓값 계산 */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF222222)),
        ) {
            Text("계산하기")
        }
    }

    editingSlotIndex?.let { index ->
        SlotEditDialog(
            slotNumber = index + 1,
            isSpecial = (index + 1) % 3 == 0,
            slotConfig = slots[index],
            onDismiss = { editingSlotIndex = null },
            onConfirm = { newConfig ->
                slots = slots.mapIndexed { i, s -> if (i == index) newConfig else s }
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
            if (isSpecial) {
                Text(
                    text = "★특수",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9B59B6),
                )
            }
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
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
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
                        candidates = candidates + OptionCandidate(PetOption.ACCURACY, 0)
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