package com.loudbook.dev.managers.config

import net.minestom.server.MinecraftServer

open class Configurable {
    init {
        setConfigVariables()
    }

    private fun setConfigVariables() {
        val clazz = this.javaClass
        for (field in clazz.declaredFields) {
            if (field.isAnnotationPresent(Config::class.java)) {
                val key = field.getAnnotation(Config::class.java).key
                val value = ConfigManager.values[key] ?: {
                    MinecraftServer.LOGGER.error("Config value $key not found! Using default value of ${field.get(this)}")
                }
                MinecraftServer.LOGGER.info("Overriding ${field.name} to $value in ${this.javaClass.simpleName}")
                field.isAccessible = true
                field.set(this, value)
            }
        }
    }
}