package com.loudbook.dev.listener

import com.loudbook.dev.managers.TimerManager
import com.loudbook.dev.managers.config.Config
import com.loudbook.dev.managers.config.Configurable
import com.loudbook.dev.world.World
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.event.trait.PlayerEvent
import java.util.*

class PlaceBlockHandler(private val timerManager: TimerManager, private val world: World) : EventListener<PlayerEvent>, Configurable() {
    @Config(key = "place-distance")
    lateinit var placeDistance: Optional<Int>

    override fun eventType(): Class<PlayerEvent> {
        return PlayerEvent::class.java
    }

    override fun run(event: PlayerEvent): EventListener.Result {
        println("susler")
        if (event !is PlayerUseItemEvent && event !is PlayerBlockPlaceEvent) return EventListener.Result.SUCCESS

        println("sussiest")
        if (event is PlayerBlockPlaceEvent) {
            event.isCancelled = true
            println("SUS")
        }

        val player = event.player

        if (timerManager.getPlaceTimer(player)) return EventListener.Result.SUCCESS

        val blocksInSight = player.getLineOfSight(placeDistance.get())
        blocksInSight ?: return EventListener.Result.SUCCESS
        if (blocksInSight.isEmpty()) return EventListener.Result.SUCCESS

        val blockPosition = blocksInSight.first()
        player.instance.setBlock(blockPosition, event.player.itemInMainHand.material().block())
        player.playSound(Sound.sound(Key.key("block.wool.place"), Sound.Source.PLAYER, 10f, 1f))

        timerManager.startPlaceTimer(player)
        world.blocks[Pos(blockPosition)] = player.itemInMainHand.material()

        return EventListener.Result.SUCCESS
    }
}