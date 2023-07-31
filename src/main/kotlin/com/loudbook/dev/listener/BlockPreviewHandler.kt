package com.loudbook.dev.listener

import com.loudbook.dev.TimerManager
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.server.ServerTickMonitorEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.BlockChangePacket

class BlockPreviewHandler(private val timerManager: TimerManager) : EventListener<PlayerMoveEvent> {

    companion object {
        private val currentBlockMap = mutableMapOf<Player, Point>()
        fun previewBlock(player: Player, block: Block, point: Point) {
            resetBlock(player)
            val blockChangePacket = BlockChangePacket(point, block)
            player.sendPacket(blockChangePacket)
            currentBlockMap[player] = point
        }

        private fun resetBlock(player: Player) {
            if (currentBlockMap[player] != null) {
                val blockChangePacket = BlockChangePacket(currentBlockMap[player]!!, player.instance.getBlock(currentBlockMap[player]!!))
                player.sendPacket(blockChangePacket)
                currentBlockMap.remove(player)
            }
        }
    }
    override fun eventType(): Class<PlayerMoveEvent> {
        return PlayerMoveEvent::class.java
    }

    override fun run(event: PlayerMoveEvent): EventListener.Result {
        val player = event.player
        if (timerManager.getTimer(player) != null) {
            resetBlock(player)
            return EventListener.Result.SUCCESS
        }

        val blocksInSight = player.getLineOfSight(100)
        if (blocksInSight.isEmpty()) return EventListener.Result.SUCCESS

        blocksInSight.first().let { block ->
            if (currentBlockMap[player] == block) {
                return EventListener.Result.SUCCESS
            }

            player.itemInMainHand.material().block() ?: return EventListener.Result.SUCCESS

            previewBlock(player, player.itemInMainHand.material().block()!!, block)
        }
        return EventListener.Result.SUCCESS
    }
}