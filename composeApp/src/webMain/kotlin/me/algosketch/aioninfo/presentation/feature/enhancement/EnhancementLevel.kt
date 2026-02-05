package me.algosketch.aioninfo.presentation.feature.enhancement

data class EnhancementLevel(
    val enhancementLevel: Int,
    val breakthroughLevel: Int,
) {
    override fun toString(): String {
        return if (breakthroughLevel == 0) "${enhancementLevel}강" else "${breakthroughLevel}돌파"
    }
}
