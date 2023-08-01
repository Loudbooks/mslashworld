package com.loudbook.dev.commands

import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.Command

class StopCommand : Command("stop") {
    init {
        setDefaultExecutor { _, _ ->
            MinecraftServer.stopCleanly()
        }
    }
}