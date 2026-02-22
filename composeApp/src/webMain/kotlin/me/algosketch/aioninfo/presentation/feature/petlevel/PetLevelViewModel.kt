package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

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

class PetLevelViewModel {
    var selectedRace by mutableStateOf(PetRace.INTELLECT)
        private set

    var slots by mutableStateOf(List(9) { SlotConfig() })
        private set

    fun selectRace(race: PetRace) {
        selectedRace = race
    }

    fun updateSlot(index: Int, config: SlotConfig) {
        slots = slots.mapIndexed { i, s -> if (i == index) config else s }
    }
}