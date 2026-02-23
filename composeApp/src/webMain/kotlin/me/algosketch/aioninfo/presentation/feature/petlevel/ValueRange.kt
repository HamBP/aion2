package me.algosketch.aioninfo.presentation.feature.petlevel

import kotlin.math.roundToInt
import kotlin.random.Random

data class ValueRange(val min: Double, val max: Double, val step: Double = 1.0) {
    fun roll(): Double {
        return if (step == 1.0) {
            Random.nextInt(min.toInt(), max.toInt() + 1).toDouble()
        } else {
            // 소수점 오차를 피하기 위해 정수 연산 사용
            val minInt = (min * 10).roundToInt()
            val maxInt = (max * 10).roundToInt()
            val stepInt = (step * 10).roundToInt()
            val count = (maxInt - minInt) / stepInt + 1
            (minInt + Random.nextInt(0, count) * stepInt) / 10.0
        }
    }
}