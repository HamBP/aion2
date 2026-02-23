package me.algosketch.aioninfo.presentation.feature.petlevel

enum class PetOption(val displayName: String) {
    // 일반 슬롯 + 특수 슬롯 3, 9 공통
    ADDITIONAL_ACCURACY("추가 명중"),
    CRITICAL("치명타"),
    ADDITIONAL_EVASION("추가 회피"),
    CRITICAL_RESISTANCE("치명타 저항"),
    BLOCK("막기"),
    // 슬롯 3, 9 전용
    DAMAGE_AMPLIFICATION("피해 증폭"),
    SMASH("강타"),
    CRITICAL_DAMAGE_AMPLIFICATION("치명타 피해 증폭"),
    // 슬롯 6 전용
    DAMAGE_RESISTANCE("피해 내성"),
    FORTITUDE("철벽"),
    CRITICAL_DAMAGE_RESISTANCE("치명타 피해 내성"),
}