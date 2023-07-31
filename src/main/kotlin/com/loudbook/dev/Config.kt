package com.loudbook.dev

import kotlinx.serialization.Serializable


@Serializable
data class Config(
    val port: Int,
    val admins: Array<String>,
    val placeDistance: Int,
    val placeCooldown: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Config

        if (port != other.port) return false
        if (!admins.contentEquals(other.admins)) return false
        if (placeDistance != other.placeDistance) return false
        if (placeCooldown != other.placeCooldown) return false

        return true
    }

    override fun hashCode(): Int {
        var result = port
        result = 31 * result + admins.contentHashCode()
        result = 31 * result + placeDistance
        result = 31 * result + placeCooldown
        return result
    }
}
