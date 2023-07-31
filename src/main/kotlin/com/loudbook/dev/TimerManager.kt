package com.loudbook.dev

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.network.packet.server.play.ActionBarPacket
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import kotlin.time.Duration.Companion.seconds

class TimerManager {
    private val timerMap = mutableMapOf<UUID, Int>()

    fun startTimer(player: Player, ticks: Int) {
        val uuid = player.uuid
        timerMap[uuid] = ticks

        MinecraftServer.getSchedulerManager().submitTask {
            if (timerMap[uuid]!! > 0) {
                val actionBarPacket = ActionBarPacket(
                    Component.text(secondsToTimerString(timerMap[uuid]!!))
                        .color(NamedTextColor.RED)
                        .decorate(TextDecoration.BOLD)
                )

                player.sendPacket(actionBarPacket)
                timerMap[uuid] = timerMap[uuid]!! - 1
                TaskSchedule.seconds(1)
            } else {
                timerMap.remove(uuid)

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
    fun getTimer(player: Player): Int? {
        return timerMap[player.uuid]
    }

    fun removeTimer(player: Player) {
        timerMap.remove(player.uuid)
    }

    private fun secondsToTimerString(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        if (remainingSeconds < 10) return "$minutes:0$remainingSeconds"

        return "$minutes:$remainingSeconds"
    }
}