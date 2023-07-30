package com.loudbook.dev.listener

import net.kyori.adventure.sound.Sound
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.ParticlePacket
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator

class PlayerInteractEvent : EventListener<PlayerBlockInteractEvent> {
    override fun eventType(): Class<PlayerBlockInteractEvent> {
        return PlayerBlockInteractEvent::class.java
    }

    override fun run(event: PlayerBlockInteractEvent): EventListener.Result {
        val player = event.player
        val blockPosition = event.blockPosition

        player.instance.setBlock(blockPosition, event.player.itemInMainHand.material().block())

        return EventListener.Result.SUCCESS
    }
}