package com.loudbook.dev.listener

import com.loudbook.dev.managers.TimerManager
import com.loudbook.dev.managers.config.Config
import com.loudbook.dev.managers.config.Configurable
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.network.packet.server.play.BlockChangePacket
import java.util.*

@Suppress("UnstableApiUsage")
class BlockPreviewHandler(private val timerManager: TimerManager) : EventListener<PlayerMoveEvent>, Configurable() {

    @Config(key = "place-distance")
    lateinit var placeDistance: Optional<Int>

    companion object {
        private val currentBlockMap = mutableMapOf<Player, Point>()
        private val currentPrePlaceBlockMap = mutableMapOf<Player, Point>()
        fun previewBlock(player: Player, block: Block, point: Point) {
            resetBlock(player)
            val blockChangePacket = BlockChangePacket(point, block)
            player.sendPacket(blockChangePacket)
            currentBlockMap[player] = point
        }

        fun resetBlock(player: Player) {
            if (currentBlockMap[player] != null) {
                val blockChangePacket =
                    BlockChangePacket(currentBlockMap[player]!!, player.instance.getBlock(currentBlockMap[player]!!))
                player.sendPacket(blockChangePacket)
                currentBlockMap.remove(player)
            }
        }

        fun previewPrePlaceBlock(player: Player, block: Block, point: Point) {
            val blockChangePacket = BlockChangePacket(point, block)
            player.sendPacket(blockChangePacket)
            currentPrePlaceBlockMap[player] = point
        }

        fun resetPrePlaceBlock(player: Player) {
            if (currentPrePlaceBlockMap[player] != null) {
                val blockChangePacket = BlockChangePacket(currentPrePlaceBlockMap[player]!!, player.instance.getBlock(currentPrePlaceBlockMap[player]!!))
                player.sendPacket(blockChangePacket)
                currentPrePlaceBlockMap.remove(player)
            }
        }

        fun resetAllPreviews(player: Player) {
            resetBlock(player)
            resetPrePlaceBlock(player)
        }
    }

    override fun eventType(): Class<PlayerMoveEvent> {
        return PlayerMoveEvent::class.java
    }

    override fun run(event: PlayerMoveEvent): EventListener.Result {
        val player = event.player
        if (timerManager.getPlaceTimer(player)) {
            resetBlock(player)
            return EventListener.Result.SUCCESS
        }

        val blocksInSight = player.getLineOfSight(placeDistance.get())
        if (blocksInSight.isEmpty()) {
            resetBlock(player)
            return EventListener.Result.SUCCESS
        }

        blocksInSight.first().let { block ->
            if (currentBlockMap[player] == block) {
                return EventListener.Result.SUCCESS
            }

            val blockInHand = player.itemInMainHand.material().block() ?: return EventListener.Result.SUCCESS

            previewBlock(player, blockInHand, block)
        }
        return EventListener.Result.SUCCESS
    }
}