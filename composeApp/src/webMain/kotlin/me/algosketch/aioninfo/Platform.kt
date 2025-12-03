package me.algosketch.aioninfo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform