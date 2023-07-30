package com.loudbook.dev.listener

import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.BlockChangePacket

class PlayerMoveEvent : EventListener<PlayerMoveEvent> {
    private val currentBlockMap = mutableMapOf<Player, Point>()
    override fun eventType(): Class<PlayerMoveEvent> {
        return PlayerMoveEvent::class.java
    }

    override fun run(event: PlayerMoveEvent): EventListener.Result {
        val player = event.player
        val blocksInSight = player.getLineOfSight(3)
        blocksInSight ?: return EventListener.Result.SUCCESS
        if (blocksInSight.isEmpty()) return EventListener.Result.SUCCESS

        blocksInSight.first().let { block ->
            if (currentBlockMap[player] == block) {
                return EventListener.Result.SUCCESS
            } else {
                if (currentBlockMap[player] != null) {
                    val blockChangePacket = BlockChangePacket(currentBlockMap[player]!!, player.instance.getBlock(currentBlockMap[player]!!))
                    player.sendPacket(blockChangePacket)
                    currentBlockMap.remove(player)
                }
            }

            val blockChangePacket = BlockChangePacket(block, Block.STONE)
            currentBlockMap[player] = block

            player.sendPacket(blockChangePacket)
        }

        return EventListener.Result.SUCCESS
    }
}