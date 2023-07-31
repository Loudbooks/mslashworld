package com.loudbook.dev.listener

import com.loudbook.dev.Config
import com.loudbook.dev.TimerManager
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerUseItemEvent

class PlaceBlockHandler(private val config: Config, private val timerManager: TimerManager) : EventListener<PlayerUseItemEvent> {
    override fun eventType(): Class<PlayerUseItemEvent> {
        return PlayerUseItemEvent::class.java
    }

    override fun run(event: PlayerUseItemEvent): EventListener.Result {
        val player = event.player

        if (timerManager.getPlaceTimer(player)) return EventListener.Result.SUCCESS

        val blocksInSight = player.getLineOfSight(config.placeDistance)
        blocksInSight ?: return EventListener.Result.SUCCESS
        if (blocksInSight.isEmpty()) return EventListener.Result.SUCCESS

        val blockPosition = blocksInSight.first()
        player.instance.setBlock(blockPosition, event.player.itemInMainHand.material().block())
        player.playSound(Sound.sound(Key.key("block.wool.place"), Sound.Source.PLAYER, 10f, 1f))

        timerManager.startPlaceTimer(player)

        return EventListener.Result.SUCCESS
    }
}