package com.loudbook.dev.listener

import com.loudbook.dev.managers.config.Config
import com.loudbook.dev.managers.config.Configurable
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerBlockBreakEvent

class BreakBlockHandler : EventListener<PlayerBlockBreakEvent>, Configurable() {
    @Config(key = "admins")
    lateinit var admins: List<String>

    override fun eventType(): Class<PlayerBlockBreakEvent> {
        return PlayerBlockBreakEvent::class.java
    }

    override fun run(event: PlayerBlockBreakEvent): EventListener.Result {
        val player = event.player
        if (player.username !in admins) {
            event.isCancelled = true
        }
        return EventListener.Result.SUCCESS
    }

}