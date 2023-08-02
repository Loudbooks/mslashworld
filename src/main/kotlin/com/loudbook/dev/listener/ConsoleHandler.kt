package com.loudbook.dev.listener

import net.minestom.server.MinecraftServer

class ConsoleHandler {
    init {
        Thread {
            while (true) {
                val input = readln()
                MinecraftServer.getCommandManager().executeServerCommand(input)
                println(input)
            }
        }
    }
}