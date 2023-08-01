package com.loudbook.dev.commands

import com.loudbook.dev.managers.TimerManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player
import net.minestom.server.item.Material

class PlaceCommand(private val timerManager: TimerManager) : Command("place") {
    init {
        setDefaultExecutor { sender, _ ->
            if (sender !is Player) return@setDefaultExecutor

            if (timerManager.getPrePlaceTimer(sender)) {
                timerManager.cancelPrePlaceTimer(sender)
                return@setDefaultExecutor
            }

            if (timerManager.getPlaceTimer(sender)) {
                sender.sendMessage(
                    Component.text("You can't place blocks right now!")
                        .color(NamedTextColor.RED)
                )
                return@setDefaultExecutor
            }

            val material = sender.itemInMainHand.material()

            if (material == Material.AIR) {
                sender.sendMessage(
                    Component.text("You aren't holding any blocks!")
                        .color(NamedTextColor.RED)
                )
                return@setDefaultExecutor
            }

            timerManager.startPrePlaceTimer(sender, sender.position, material)
        }
    }
}