package com.loudbook.dev.managers.config

import net.minestom.server.MinecraftServer
import java.util.*

open class Configurable {
    init {
        setConfigVariables()
    }

    private fun setConfigVariables() {
        val clazz = this.javaClass
        for (field in clazz.fields) {
            if (!field.isAnnotationPresent(Config::class.java)) continue
            val key = field.getAnnotation(Config::class.java).key
            val value = ConfigManager.values[key] ?: {
                MinecraftServer.LOGGER.error("Config value $key not found! Using default value of ${field.get(this)}")
            }
            MinecraftServer.LOGGER.info("Overriding ${field.name} to $value in ${this.javaClass.simpleName}")
            field.isAccessible = true

            when (field.type) {
                Optional::class.java -> {
                    field.set(this, Optional.of(value))
                }
                else -> {
                    field.set(this, value)
                }
            }
        }
    }
}