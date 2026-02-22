package me.algosketch.aioninfo.data.enhancement

import kotlin.collections.listOf

object EnhancementRepository {
    // 일반 강화
    val uniqueSuccessPercent = listOf(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 65, 50, 35, 25, 20)
    val heroSuccessPercent = listOf(100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 65, 50, 35, 25, 20, 20, 15, 15, 10, 10)

    val items = listOf(
        ItemEnhancementInfo(
            itemLevel = 84,
            enhancementCosts = listOf(
                EnhancementCost(
                    stones = 420,
                    kina = 10500,
                ),
                EnhancementCost(
                    stones = 600,
                    kina = 15000,
                ),
                EnhancementCost(
                    stones = 1100,
                    kina = 27500,
                ),
                EnhancementCost(
                    stones = 1680,
                    kina = 42000,
                ),
                EnhancementCost(
                    stones = 2350,
                    kina = 59000,
                ),
                EnhancementCost(
                    stones = 3090,
                    kina = 87000,
                ),
                EnhancementCost(
                    stones = 3890,
                    kina = 98000,
                ),
                EnhancementCost(
                    stones = 4760,
                    kina = 119000,
                ),
                EnhancementCost(
                    stones = 5670,
                    kina = 142000,
                ),
                EnhancementCost(
                    stones = 6650,
                    kina = 167000,
                ),
                EnhancementCost(
                    stones = 7670,
                    kina = 300000,
                ),
                EnhancementCost(
                    stones = 8730,
                    kina = 440000,
                ),
                EnhancementCost(
                    stones = 9850,
                    kina = 710000,
                ),
                EnhancementCost(
                    stones = 11010,
                    kina = 1110000,
                ),
                EnhancementCost(
                    stones = 12200,
                    kina = 1530000,
                ),
            ),
        ),
        // TODO: 2~10 강 DB 만들기
        ItemEnhancementInfo(
            itemLevel = 108,
            enhancementCosts = listOf(
                EnhancementCost(
                    stones = 540,
                    kina = 13500,
                ),
                EnhancementCost(
                    stones = 770,
                    kina = 19300,
                ),
                EnhancementCost(
                    stones = 1410,
                    kina = 35300,
                ),
                EnhancementCost(
                    stones = 0,
                    kina = 0,
                ),
                EnhancementCost(
                    stones = 0,
                    kina = 0,
                ),
                EnhancementCost(
                    stones = 0,
                    kina = 0,
                ),
                EnhancementCost(
                    stones = 3890,
                    kina = 98000,
                ),
                EnhancementCost(
                    stones = 4760,
                    kina = 119000,
                ),
                EnhancementCost(
                    stones = 5670,
                    kina = 142000,
                ),
                EnhancementCost(
                    stones = 6650,
                    kina = 167000,
                ),
                EnhancementCost(
                    stones = 9860,
                    kina = 380000,
                ),
                EnhancementCost(
                    stones = 11230,
                    kina = 570000,
                ),
                EnhancementCost(
                    stones = 12660,
                    kina = 910000,
                ),
                EnhancementCost(
                    stones = 14150,
                    kina = 1420000,
                ),
                EnhancementCost(
                    stones = 15690,
                    kina = 1970000,
                ),
                EnhancementCost(
                    stones = 17280,
                    kina = 2160000,
                ),
                EnhancementCost(
                    stones = 18930,
                    kina = 3200000,
                ),
                EnhancementCost(
                    stones = 20620,
                    kina = 3500000,
                ),
                EnhancementCost(
                    stones = 22370,
                    kina = 5600000,
                ),
                EnhancementCost(
                    stones = 24150,
                    kina = 6100000,
                ),
            ),
        )
    )

    // 돌파 강화
    val uniqueBreakthroughPercent = listOf(66, 50, 33, 25, 20)
    val heroBreakthroughPercent = listOf(33.0, 25.0, 16.5, 12.5, 10.0)

    val uniqueEnhancementCosts = listOf(
        BreakthroughCost(
            stones = 6,
            kina = 600_000,
        ),
        BreakthroughCost(
            stones = 9,
            kina = 900_000,
        ),
        BreakthroughCost(
            stones = 12,
            kina = 1_400_000,
        ),
        BreakthroughCost(
            stones = 15,
            kina = 2_000_000,
        ),
        BreakthroughCost(
            stones = 20,
            kina = 3_000_000,
        ),
    )

    val heroEnhancementCosts = listOf(
        BreakthroughCost(
            stones = 6,
            kina = 1_000_000,
        ),
        BreakthroughCost(
            stones = 9,
            kina = 1_500_000,
        ),
        BreakthroughCost(
            stones = 12,
            kina = 2_400_000,
        ),
        BreakthroughCost(
            stones = 15,
            kina = 3_100_000,
        ),
        BreakthroughCost(
            stones = 20,
            kina = 7_300_000,
        ),
    )
}
