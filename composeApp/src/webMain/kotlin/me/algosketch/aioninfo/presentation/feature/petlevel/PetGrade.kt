package me.algosketch.aioninfo.presentation.feature.petlevel

enum class PetGrade(val displayName: String, val probability: Double) {
    HERO("영웅", 0.05),
    UNIQUE("유일", 0.15),
    LEGEND("전승", 0.30),
    RARE("희귀", 0.30),
    COMMON("일반", 0.20),
}