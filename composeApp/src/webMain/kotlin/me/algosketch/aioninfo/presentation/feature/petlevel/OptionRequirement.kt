package me.algosketch.aioninfo.presentation.feature.petlevel

data class OptionRequirement(
    val option: PetOption,
    val minValue: Double,
    val count: Int,
)