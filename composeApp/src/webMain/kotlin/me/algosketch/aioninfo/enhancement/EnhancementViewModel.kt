package me.algosketch.aioninfo.enhancement

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
        // TODO: 영웅 등급은 따로 작성해야 함.
        val successPercent = listOf(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 65, 50, 35, 25, 20)
        val stones = listOf(420, 600, 1100, 1680, 2350, 3090, 3890, 4760, 5670, 6650, 7670, 8730, 9850, 11010, 12200)
        val kina = listOf(
            10500,
            15000,
            27500,
            42000,
            59000,
            87000,
            98000,
            119000,
            142000,
            167000,
            300000,
            440000,
            710000,
            1110000,
            1530000
        )

        var totalStones = 0
        var totalKina = 0
        for (level in currentLevel until targetLevel) {
            var percent = successPercent[level]
            while (true) {
                totalStones += stones[level]
                totalKina += kina[level]

                val dice = Random.nextInt(100) + 1
                if (dice <= percent) break

                percent += 5
            }
        }

        return totalStones to totalKina
    }
}