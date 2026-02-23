package me.algosketch.aioninfo.presentation.feature.petlevel

data class CalculationResult(
    val avgCrystals: Double,
    val avgKina: Double,
    val crystalsData: List<Int>,
    val kinaData: List<Int>,
)