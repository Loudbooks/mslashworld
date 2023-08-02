package com.loudbook.dev.managers.config

import net.minestom.server.MinecraftServer
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files


class ConfigManager {
    companion object {
        var values = mutableMapOf<String, Any>()

        fun loadConfig() {
            val yaml = Yaml()
            val file = File("./config.yml")

            if (!file.exists()) {
                MinecraftServer.LOGGER.warn("Config file not found! Creating new one...")
                this::class.java.getResourceAsStream("/config.yml").use { `in` ->
                    BufferedReader(InputStreamReader(`in`)).use { reader ->
                        file.writeText(reader.readText())
                        file.createNewFile()
                    }
                }
            }

            values = yaml.load(file.inputStream())
        }
    }
}
