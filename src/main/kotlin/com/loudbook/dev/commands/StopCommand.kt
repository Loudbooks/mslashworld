package com.loudbook.dev.commands

import com.loudbook.dev.world.World
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command

class StopCommand(private val world: World) : Command("stop") {
    init {
        setDefaultExecutor { _, _ ->
            world.saveToFile()
            MinecraftServer.stopCleanly()
        }
    }
}