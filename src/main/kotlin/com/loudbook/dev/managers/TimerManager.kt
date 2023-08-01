package com.loudbook.dev.managers

import com.loudbook.dev.listener.PreviewBlockHandler
import com.loudbook.dev.world.World
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.network.packet.server.play.ActionBarPacket
import net.minestom.server.timer.TaskSchedule
import java.util.*

@Suppress("UnstableApiUsage")
class TimerManager(private val world: World) {
    private val placeTimerMap = mutableMapOf<UUID, Int>()
    private val currentPrePlaceTimers = mutableListOf<UUID>()

    fun startPlaceTimer(player: Player) {
        val uuid = player.uuid
        placeTimerMap[uuid] = 10

        MinecraftServer.getSchedulerManager().submitTask {
            if (placeTimerMap[uuid]!! > 0) {
                val actionBarPacket = ActionBarPacket(
                    Component.text(secondsToTimerString(placeTimerMap[uuid]!!))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                )

                player.sendPacket(actionBarPacket)
                placeTimerMap[uuid] = placeTimerMap[uuid]!! - 1
                TaskSchedule.seconds(1)
            } else {
                placeTimerMap.remove(uuid)

                player.sendMessage(
                    Component.text("Your timer has expired!")
                        .color(NamedTextColor.GREEN)
                )

                player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 1f, 1f))

                val actionBarPacket = ActionBarPacket(
                    Component.text("Your timer has expired!")
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)
                )

                player.sendPacket(actionBarPacket)
                TaskSchedule.stop()
            }
        }
    }
    fun getPlaceTimer(player: Player): Boolean {
        return placeTimerMap.contains(player.uuid)
    }

    private fun secondsToTimerString(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        if (remainingSeconds < 10) return "$minutes:0$remainingSeconds"

        return "$minutes:$remainingSeconds"
    }

    fun startPrePlaceTimer(player: Player, point: Point, material: Material) {
        currentPrePlaceTimers.add(player.uuid)

        PreviewBlockHandler.resetAllPreviews(player)

        player.sendMessage(
            Component.textOfChildren(
                Component.text("Your block will be placed in 5 seconds! Type ")
                    .color(NamedTextColor.GREEN),
                Component.text("/cancel")
                    .color(NamedTextColor.GREEN)
                    .decorate(TextDecoration.BOLD),
                Component.text(" to cancel.")
                    .color(NamedTextColor.GREEN)
            )
        )
        var isPreviewed = false
        var timesRun = 0
        MinecraftServer.getSchedulerManager().submitTask {
            if (!currentPrePlaceTimers.contains(player.uuid)) {
                PreviewBlockHandler.resetPrePlaceBlock(player)
                return@submitTask TaskSchedule.stop()
            }

            if (timesRun == 20) {
                player.instance.setBlock(point, material.block())
                startPlaceTimer(player)
                currentPrePlaceTimers.remove(player.uuid)
                world.blocks[Pos(point)] = material
                return@submitTask TaskSchedule.stop()
            }

            if (isPreviewed) {
                PreviewBlockHandler.resetPrePlaceBlock(player)
            } else {
                PreviewBlockHandler.previewPrePlaceBlock(player, material.block()!!, point)
            }

            isPreviewed = !isPreviewed
            timesRun++

            return@submitTask TaskSchedule.millis(250)
        }
    }

    fun cancelPrePlaceTimer(player: Player) {
        currentPrePlaceTimers.remove(player.uuid)
        player.sendMessage(
            Component.text("Your block placement has been cancelled!")
                .color(NamedTextColor.RED)
        )
    }

    fun getPrePlaceTimer(player: Player): Boolean {
        return currentPrePlaceTimers.contains(player.uuid)
    }
}