package me.algosketch.aioninfo.presentation.feature.petlevel

enum class SlotType(val totalOptions: Int, val validOptions: List<PetOption>) {
    NORMAL(14, listOf(
        PetOption.ADDITIONAL_ACCURACY,
        PetOption.CRITICAL,
        PetOption.ADDITIONAL_EVASION,
        PetOption.CRITICAL_RESISTANCE,
        PetOption.BLOCK,
    )),
    SPECIAL_39(18, listOf(
        PetOption.DAMAGE_AMPLIFICATION,
        PetOption.SMASH,
        PetOption.CRITICAL_DAMAGE_AMPLIFICATION,
        PetOption.ADDITIONAL_ACCURACY,
        PetOption.CRITICAL,
        PetOption.CRITICAL_RESISTANCE,
        PetOption.ADDITIONAL_EVASION,
    )),
    SPECIAL_6(18, listOf(
        PetOption.DAMAGE_RESISTANCE,
        PetOption.FORTITUDE,
        PetOption.CRITICAL_DAMAGE_RESISTANCE,
        PetOption.ADDITIONAL_ACCURACY,
        PetOption.CRITICAL,
        PetOption.CRITICAL_RESISTANCE,
        PetOption.ADDITIONAL_EVASION,
    )),
}