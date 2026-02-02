package me.algosketch.aioninfo.presentation.feature.enhancement

import me.algosketch.aioninfo.data.enhancement.Enhancement
import kotlin.random.Random

class EnhancementViewModel {
    // TODO: 각 단계별 트라이 횟수도 출력해 주기
    /**
     * 가격 반환
     *
     * @return (강화석, 키나)
     */
    fun simulate(
        currentLevel: Int,
        targetLevel: Int,
    ): Pair<Int, Int> {
        val cost = Enhancement.items.find { it.itemLevel == 84 }!!
        val successPercent = listOf(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 65, 50, 35, 25, 20)

        var totalStones = 0
        var totalKina = 0
        for (level in currentLevel until targetLevel) {
            var percent = successPercent[level]
            while (true) {
                totalStones += cost.enhancementCosts[level].stones
                totalKina += cost.enhancementCosts[level].kina

                val dice = Random.nextInt(100) + 1
                if (dice <= percent) break

                percent += 5
            }
        }

        return totalStones to totalKina
    }
}