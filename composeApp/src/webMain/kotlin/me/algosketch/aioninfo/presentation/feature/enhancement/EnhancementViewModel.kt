package me.algosketch.aioninfo.presentation.feature.enhancement

import me.algosketch.aioninfo.data.enhancement.BreakthroughCost
import me.algosketch.aioninfo.data.enhancement.EnhancementCost
import me.algosketch.aioninfo.data.enhancement.EnhancementRepository
import kotlin.random.Random

class EnhancementViewModel {
    // TODO: 각 단계별 트라이 횟수도 출력해 주기
    /**
     * 해당 레벨까지의 시뮬레이션 된 강화 비용 반환
     *
     * @return (강화석, 키나)
     */
    fun simulate(
        itemLevel: Int,
        startEnhancement: EnhancementLevel,
        targetEnhancement: EnhancementLevel,
    ): Pair<EnhancementCost, BreakthroughCost> {
        val isUnique = itemLevel <= 92

        return if (isUnique) {
            simulateUnique(
                itemLevel = itemLevel,
                startEnhancement = startEnhancement,
                targetEnhancement = targetEnhancement
            )
        } else {
            simulateHero(
                itemLevel = itemLevel,
                startEnhancement = startEnhancement,
                targetEnhancement = targetEnhancement
            )
        }
    }

    fun simulateUnique(
        itemLevel: Int,
        startEnhancement: EnhancementLevel,
        targetEnhancement: EnhancementLevel,
    ): Pair<EnhancementCost, BreakthroughCost> {
        val enhancementCosts = EnhancementRepository.items.find { it.itemLevel == itemLevel }
            ?: throw IllegalStateException("${itemLevel}에 해당하는 강화 정보가 존재하지 않습니다.")

        // 강화 계산
        val enhancementCost =
            simulateEnhancement(
                costs = enhancementCosts.enhancementCosts,
                startEnhancement = startEnhancement.enhancementLevel,
                targetEnhancement = targetEnhancement.enhancementLevel
            )

        // 돌파 계산
        var breakthroughStones = 0
        var breakthroughKina = 0
        val breakthroughCosts = EnhancementRepository.uniqueEnhancementCosts
        for (currentLevel in startEnhancement.breakthroughLevel until targetEnhancement.breakthroughLevel) {
            var percent = EnhancementRepository.uniqueBreakthroughPercent[currentLevel]
            while (true) {
                breakthroughStones += breakthroughCosts[currentLevel].stones
                breakthroughKina += breakthroughCosts[currentLevel].kina

                val dice = Random.nextInt(100) + 1
                if (dice <= percent) break

                percent += 5 - currentLevel
            }
        }

        return enhancementCost to BreakthroughCost(
            stones = breakthroughStones,
            kina = breakthroughKina
        )
    }

    fun simulateHero(
        itemLevel: Int,
        startEnhancement: EnhancementLevel,
        targetEnhancement: EnhancementLevel,
    ): Pair<EnhancementCost, BreakthroughCost> {
        val enhancementCosts = EnhancementRepository.items.find { it.itemLevel == itemLevel }
            ?: throw IllegalStateException("${itemLevel}에 해당하는 강화 정보가 존재하지 않습니다.")

        // 강화 계산
        val enhancementCost =
            simulateEnhancement(
                costs = enhancementCosts.enhancementCosts,
                startEnhancement = startEnhancement.enhancementLevel,
                targetEnhancement = targetEnhancement.enhancementLevel
            )

        // 돌파 계산
        var breakthroughStones = 0
        var breakthroughKina = 0
        val breakthroughCosts = EnhancementRepository.heroEnhancementCosts
        for (currentLevel in startEnhancement.breakthroughLevel until targetEnhancement.breakthroughLevel) {
            var percent = EnhancementRepository.heroBreakthroughPercent[currentLevel]
            while (true) {
                breakthroughStones += breakthroughCosts[currentLevel].stones
                breakthroughKina += breakthroughCosts[currentLevel].kina

                val dice = Random.nextDouble(100.0)
                if (dice <= percent) break

                percent += 5 - currentLevel
            }
        }

        return enhancementCost to BreakthroughCost(
            stones = breakthroughStones,
            kina = breakthroughKina
        )
    }

    fun simulateEnhancement(
        costs: List<EnhancementCost>,
        startEnhancement: Int,
        targetEnhancement: Int,
    ): EnhancementCost {
        var totalStones = 0
        var totalKina = 0
        for (currentLevel in startEnhancement until targetEnhancement) {
            var percent = EnhancementRepository.heroSuccessPercent[currentLevel]
            while (true) {
                totalStones += costs[currentLevel].stones
                totalKina += costs[currentLevel].kina

                val dice = Random.nextInt(100) + 1
                if (dice <= percent) break

                percent += 5
            }
        }

        return EnhancementCost(stones = totalStones, kina = totalKina)
    }
}