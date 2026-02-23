package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

data class ValueRange(val min: Int, val max: Int)

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

    // 잠근 슬롯 수 → 필요한 결정 개수 (인덱스 = 잠근 슬롯 수)
    val CRYSTALS_BY_LOCKED = listOf(45, 50, 55, 75, 120, 215, 310, 405, 500)

    // 키나 = 결정 × 200
    const val KINA_PER_CRYSTAL = 200

    fun getCrystals(lockedCount: Int): Int = CRYSTALS_BY_LOCKED[lockedCount]
}

// 옵션 N개 이상 M슬롯 목표
data class OptionRequirement(
    val option: PetOption,
    val minValue: Int,
    val count: Int,
)

data class CalculationResult(
    val avgCrystals: Double,
    val avgKina: Double,
    val crystalsData: List<Int>,
    val kinaData: List<Int>,
)

class PetLevelViewModel : ViewModel() {
    var selectedRace by mutableStateOf(PetRace.INTELLECT)
        private set

    // 굴릴 일반 슬롯 수 (1~6)
    var activeSlots by mutableStateOf(6)
        private set

    val requirements = MutableStateFlow<List<OptionRequirement>>(emptyList())

    var result by mutableStateOf<CalculationResult?>(null)
        private set

    fun selectRace(race: PetRace) {
        selectedRace = race
    }

    fun setActiveSlots(count: Int) {
        activeSlots = count
    }

    fun updateRequirements(list: List<OptionRequirement>) {
        requirements.value = list
    }

    fun calculate() {
        val reqs = requirements.value
        val totalRequired = reqs.sumOf { it.count }
        if (reqs.isEmpty() || totalRequired > activeSlots) return

        val simulations = 10000
        val crystalsList = mutableListOf<Int>()

        repeat(simulations) {
            crystalsList.add(simulate(reqs, activeSlots))
        }

        result = CalculationResult(
            avgCrystals = crystalsList.average(),
            avgKina = crystalsList.average() * PetConstants.KINA_PER_CRYSTAL,
            crystalsData = crystalsList,
            kinaData = crystalsList.map { it * PetConstants.KINA_PER_CRYSTAL },
        )
    }

    private fun simulate(requirements: List<OptionRequirement>, activeSlots: Int): Int {
        val remaining = requirements.map { it.count }.toMutableList()
        var lockedCount = 0
        var totalCrystals = 0

        while (remaining.any { it > 0 }) {
            totalCrystals += PetConstants.getCrystals(lockedCount)

            val unlocked = activeSlots - lockedCount
            repeat(unlocked) {
                val (option, value) = rollSlot() ?: return@repeat
                val idx = requirements.indices.firstOrNull { i ->
                    requirements[i].option == option &&
                    value >= requirements[i].minValue &&
                    remaining[i] > 0
                } ?: return@repeat
                remaining[idx]--
                lockedCount++
            }
        }

        return totalCrystals
    }

    private fun rollSlot(): Pair<PetOption, Int>? {
        val grade = rollGrade()
        if (grade != PetGrade.HERO && grade != PetGrade.UNIQUE) return null

        val optionIndex = Random.nextInt(PetConstants.TOTAL_OPTIONS)
        if (optionIndex >= PetOption.entries.size) return null

        val option = PetOption.entries[optionIndex]
        val range = optionValueRanges[option]!![grade]!!
        val value = Random.nextInt(range.min, range.max + 1)
        return option to value
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