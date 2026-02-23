package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

enum class PetRace(val displayName: String) {
    INTELLECT("지성"),
    WILD("야성"),
    NATURE("자연"),
    TRANSFORM("변형"),
    SPECIAL("특수"),
}

enum class PetGrade(val displayName: String, val probability: Double) {
    HERO("영웅", 0.05),
    UNIQUE("유일", 0.15),
    LEGEND("전승", 0.30),
    RARE("희귀", 0.30),
    COMMON("일반", 0.20),
}

// 유효 옵션 5가지. 전체 14가지 옵션 중 이 5가지만 유효.
enum class PetOption(val displayName: String) {
    ADDITIONAL_ACCURACY("추가 명중"),
    CRITICAL("치명타"),
    ADDITIONAL_EVASION("추가 회피"),
    CRITICAL_RESISTANCE("치명타 저항"),
    BLOCK("막기"),
}

data class ValueRange(val min: Int, val max: Int) {
    fun probabilityAtLeast(minValue: Int): Double = when {
        minValue <= min -> 1.0
        minValue > max -> 0.0
        else -> (max - minValue + 1).toDouble() / (max - min + 1)
    }
}

// 유효 옵션별 등급별 수치 범위
val optionValueRanges: Map<PetOption, Map<PetGrade, ValueRange>> = mapOf(
    PetOption.ADDITIONAL_ACCURACY to mapOf(
        PetGrade.HERO   to ValueRange(20, 40),
        PetGrade.UNIQUE to ValueRange(17, 34),
    ),
    PetOption.CRITICAL to mapOf(
        PetGrade.HERO   to ValueRange(20, 30),
        PetGrade.UNIQUE to ValueRange(10, 20),
    ),
    PetOption.ADDITIONAL_EVASION to mapOf(
        PetGrade.HERO   to ValueRange(20, 40),
        PetGrade.UNIQUE to ValueRange(15, 30),
    ),
    PetOption.CRITICAL_RESISTANCE to mapOf(
        PetGrade.HERO   to ValueRange(20, 30),
        PetGrade.UNIQUE to ValueRange(10, 20),
    ),
    PetOption.BLOCK to mapOf(
        PetGrade.HERO   to ValueRange(25, 50),
        PetGrade.UNIQUE to ValueRange(20, 40),
    ),
)

object PetConstants {
    const val TOTAL_OPTIONS = 14
    val VALID_GRADE_PROBABILITY = PetGrade.HERO.probability + PetGrade.UNIQUE.probability // 0.20

    // 잠근 슬롯 수 → 필요한 결정 개수 (인덱스 = 잠근 슬롯 수)
    val CRYSTALS_BY_LOCKED = listOf(45, 50, 55, 75, 120, 215, 310, 405, 500)

    // 키나 = 결정 × 200
    const val KINA_PER_CRYSTAL = 200

    fun getCost(lockedCount: Int): Pair<Int, Int> {
        val crystals = CRYSTALS_BY_LOCKED[lockedCount]
        return crystals to crystals * KINA_PER_CRYSTAL
    }
}

data class OptionCandidate(
    val option: PetOption,
    val minValue: Int,
)

data class SlotConfig(
    val candidates: List<OptionCandidate> = emptyList(),
)

data class CalculationResult(
    val avgCrystals: Double,
    val avgKina: Double,
    val crystalsData: List<Int>,
    val kinaData: List<Int>,
)

class PetLevelViewModel {
    var selectedRace by mutableStateOf(PetRace.INTELLECT)
        private set

    val slots = MutableStateFlow(listOf(*Array(9) { SlotConfig() }))

    var result by mutableStateOf<CalculationResult?>(null)
        private set

    fun selectRace(race: PetRace) {
        selectedRace = race
    }

    fun updateSlot(index: Int, config: SlotConfig) {
        slots.update {
            val slotsToUpdate = it.toMutableList()
            slotsToUpdate[index] = config
            slotsToUpdate
        }
    }

    fun calculate() {
        val targetIndices = slots.value.indices.filter { slots.value[it].candidates.isNotEmpty() }
        if (targetIndices.isEmpty()) return

        val simulations = 10000
        val crystalsList = mutableListOf<Int>()
        val kinaList = mutableListOf<Int>()

        repeat(simulations) {
            val (crystals, kina) = simulate(slots.value, targetIndices)
            crystalsList.add(crystals)
            kinaList.add(kina)
        }

        result = CalculationResult(
            avgCrystals = crystalsList.average(),
            avgKina = kinaList.average(),
            crystalsData = crystalsList,
            kinaData = kinaList,
        )
    }

    private fun simulate(slots: List<SlotConfig>, targetIndices: List<Int>): Pair<Int, Int> {
        val locked = MutableList(9) { false }
        var totalCrystals = 0
        var totalKina = 0

        while (targetIndices.any { !locked[it] }) {
            val lockedCount = locked.count { it }
            val (crystals, kina) = PetConstants.getCost(lockedCount)
            totalCrystals += crystals
            totalKina += kina

            for (i in targetIndices) {
                if (locked[i]) continue
                if (rollSlot(slots[i])) locked[i] = true
            }
        }

        return totalCrystals to totalKina
    }

    private fun rollSlot(config: SlotConfig): Boolean {
        val grade = rollGrade()
        if (grade != PetGrade.HERO && grade != PetGrade.UNIQUE) return false

        val optionIndex = Random.nextInt(PetConstants.TOTAL_OPTIONS)
        if (optionIndex >= PetOption.entries.size) return false // 무효 옵션

        val option = PetOption.entries[optionIndex]
        val candidate = config.candidates.find { it.option == option } ?: return false

        val range = optionValueRanges[option]!![grade]!!
        val value = Random.nextInt(range.min, range.max + 1)

        return value >= candidate.minValue
    }

    private fun rollGrade(): PetGrade {
        val dice = Random.nextDouble()
        var cumulative = 0.0
        for (grade in PetGrade.entries) {
            cumulative += grade.probability
            if (dice < cumulative) return grade
        }
        return PetGrade.COMMON
    }
}