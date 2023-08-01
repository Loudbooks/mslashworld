package com.loudbook.dev.managers.config

import net.minestom.server.MinecraftServer
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files


class ConfigManager {
    companion object {
        var values = mutableMapOf<String, Any>()

        fun loadConfig() {
            val yaml = Yaml()
            var file = File("./config.yml")

            if (!file.exists()) {
                MinecraftServer.LOGGER.warn("Config file not found! Creating new one...")
                file = Files.copy(File(this::class.java.classLoader.getResource("config.yml")!!.file).toPath(), File("./config.yml").toPath()).toFile()
            }

            values = yaml.load(file.inputStream())
        }
    }
}
