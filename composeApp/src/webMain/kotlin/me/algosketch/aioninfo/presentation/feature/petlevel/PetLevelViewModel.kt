package me.algosketch.aioninfo.presentation.feature.petlevel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.random.Random

val optionValueRanges: Map<PetOption, Map<PetGrade, ValueRange>> = mapOf(
    PetOption.ADDITIONAL_ACCURACY to mapOf(
        PetGrade.HERO   to ValueRange(20.0, 40.0),
        PetGrade.UNIQUE to ValueRange(17.0, 34.0),
    ),
    PetOption.CRITICAL to mapOf(
        PetGrade.HERO   to ValueRange(20.0, 30.0),
        PetGrade.UNIQUE to ValueRange(10.0, 20.0),
    ),
    PetOption.ADDITIONAL_EVASION to mapOf(
        PetGrade.HERO   to ValueRange(20.0, 40.0),
        PetGrade.UNIQUE to ValueRange(15.0, 30.0),
    ),
    PetOption.CRITICAL_RESISTANCE to mapOf(
        PetGrade.HERO   to ValueRange(20.0, 30.0),
        PetGrade.UNIQUE to ValueRange(10.0, 20.0),
    ),
    PetOption.BLOCK to mapOf(
        PetGrade.HERO   to ValueRange(25.0, 50.0),
        PetGrade.UNIQUE to ValueRange(20.0, 40.0),
    ),
    PetOption.DAMAGE_AMPLIFICATION to mapOf(
        PetGrade.HERO   to ValueRange(1.2, 2.4, 0.1),
        PetGrade.UNIQUE to ValueRange(0.9, 1.8, 0.1),
    ),
    PetOption.SMASH to mapOf(
        PetGrade.HERO   to ValueRange(1.2, 2.4, 0.1),
        PetGrade.UNIQUE to ValueRange(0.9, 1.8, 0.1),
    ),
    PetOption.CRITICAL_DAMAGE_AMPLIFICATION to mapOf(
        PetGrade.HERO   to ValueRange(1.5, 3.0, 0.1),
        PetGrade.UNIQUE to ValueRange(1.2, 2.4, 0.1),
    ),
    PetOption.DAMAGE_RESISTANCE to mapOf(
        PetGrade.HERO   to ValueRange(1.2, 2.4, 0.1),
        PetGrade.UNIQUE to ValueRange(0.9, 1.8, 0.1),
    ),
    PetOption.FORTITUDE to mapOf(
        PetGrade.HERO   to ValueRange(1.2, 2.4, 0.1),
        PetGrade.UNIQUE to ValueRange(0.9, 1.8, 0.1),
    ),
    PetOption.CRITICAL_DAMAGE_RESISTANCE to mapOf(
        PetGrade.HERO   to ValueRange(1.3, 2.6, 0.1),
        PetGrade.UNIQUE to ValueRange(1.0, 2.0, 0.1),
    ),
)

// 슬롯 1~9의 타입 (인덱스 0~8)
val SLOT_TYPES = listOf(
    SlotType.NORMAL,     // 슬롯 1
    SlotType.NORMAL,     // 슬롯 2
    SlotType.SPECIAL_39, // 슬롯 3
    SlotType.NORMAL,     // 슬롯 4
    SlotType.NORMAL,     // 슬롯 5
    SlotType.SPECIAL_6,  // 슬롯 6
    SlotType.NORMAL,     // 슬롯 7
    SlotType.NORMAL,     // 슬롯 8
    SlotType.SPECIAL_39, // 슬롯 9
)

object PetConstants {
    // 잠근 슬롯 수 → 필요한 결정 개수 (인덱스 = 잠근 슬롯 수)
    val CRYSTALS_BY_LOCKED = listOf(45, 50, 55, 75, 120, 215, 310, 405, 500)

    // 키나 = 결정 × 200
    const val KINA_PER_CRYSTAL = 200

    fun getCrystals(lockedCount: Int): Int = CRYSTALS_BY_LOCKED[lockedCount]
}

class PetLevelViewModel : ViewModel() {
    var selectedRace by mutableStateOf(PetRace.NORMAL)
        private set

    // 굴리기 전 이미 잠긴 슬롯 수 (0~8)
    var preLockedSlots by mutableStateOf(0)
        private set

    val requirements = MutableStateFlow<List<OptionRequirement>>(emptyList())

    var result by mutableStateOf<CalculationResult?>(null)
        private set

    var isCalculating by mutableStateOf(false)
        private set

    fun selectRace(race: PetRace) {
        selectedRace = race
    }

    fun setPreLockedSlots(count: Int) {
        preLockedSlots = count
    }

    fun updateRequirementsForType(slotType: SlotType, list: List<OptionRequirement>) {
        requirements.value = requirements.value.filter { it.slotType != slotType } + list
    }

    fun calculate() {
        val reqs = requirements.value
        if (reqs.isEmpty()) return

        viewModelScope.launch {
            isCalculating = true
            delay(1) // UI가 로딩 상태를 렌더링할 수 있도록 양보

            val crystalsList = withContext(Dispatchers.Default) {
                val list = mutableListOf<Int>()
                repeat(1000) {
                    list.add(simulate(reqs, preLockedSlots))
                }
                list
            }

            result = CalculationResult(
                avgCrystals = crystalsList.average(),
                avgKina = crystalsList.average() * PetConstants.KINA_PER_CRYSTAL,
                crystalsData = crystalsList,
                kinaData = crystalsList.map { it * PetConstants.KINA_PER_CRYSTAL },
            )
            isCalculating = false
        }
    }

    private fun simulate(requirements: List<OptionRequirement>, preLockedSlots: Int): Int {
        val remaining = requirements.map { it.count }.toMutableList()
        val locked = MutableList(9) { it < preLockedSlots }
        var lockedCount = preLockedSlots
        var totalCrystals = 0

        while (remaining.any { it > 0 }) {
            totalCrystals += PetConstants.getCrystals(lockedCount)

            for (slotIndex in 0..8) {
                if (locked[slotIndex]) continue
                val (option, value) = rollSlot(SLOT_TYPES[slotIndex]) ?: continue
                val idx = requirements.indices.firstOrNull { i ->
                    requirements[i].slotType == SLOT_TYPES[slotIndex] &&
                    requirements[i].option == option &&
                    value >= requirements[i].minValue &&
                    remaining[i] > 0
                } ?: continue
                remaining[idx]--
                locked[slotIndex] = true
                lockedCount++
            }
        }

        return totalCrystals
    }

    private fun rollSlot(slotType: SlotType): Pair<PetOption, Double>? {
        val grade = rollGrade()
        if (grade != PetGrade.HERO && grade != PetGrade.UNIQUE) return null

        val optionIndex = Random.nextInt(slotType.totalOptions)
        if (optionIndex >= slotType.validOptions.size) return null

        val option = slotType.validOptions[optionIndex]
        val range = optionValueRanges[option]!![grade]!!
        return option to range.roll()
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