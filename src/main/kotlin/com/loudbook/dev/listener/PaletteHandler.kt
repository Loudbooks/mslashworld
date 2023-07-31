package com.loudbook.dev.listener

import com.loudbook.dev.TimerManager
import net.minestom.server.event.EventListener
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class PaletteHandler(private val timerManager: TimerManager) : EventListener<PlayerHandAnimationEvent> {
    private val blocks = listOf(
        Material.RED_CONCRETE,
        Material.ORANGE_CONCRETE,
        Material.YELLOW_CONCRETE,
        Material.LIME_CONCRETE,
        Material.GREEN_CONCRETE,
        Material.CYAN_CONCRETE,
        Material.LIGHT_BLUE_CONCRETE,
        Material.BLUE_CONCRETE,
        Material.PURPLE_CONCRETE,
        Material.MAGENTA_CONCRETE,
        Material.PINK_CONCRETE,
        Material.BROWN_CONCRETE,
        Material.BLACK_CONCRETE,
        Material.GRAY_CONCRETE,
        Material.LIGHT_GRAY_CONCRETE,
        Material.WHITE_CONCRETE
    )

    override fun eventType(): Class<PlayerHandAnimationEvent> {
        return PlayerHandAnimationEvent::class.java
    }

    override fun run(event: PlayerHandAnimationEvent): EventListener.Result {
        val player = event.player
        val currentIndex = blocks.indexOf(player.itemInMainHand.material())
        var newIndex = currentIndex + 1

        if (newIndex == blocks.size) newIndex = 0

        for (i in 0..8) {
            player.inventory.setItemStack(i, ItemStack.of(blocks[newIndex]))
        }

        if (timerManager.getPlaceTimer(player)) return EventListener.Result.SUCCESS
        if (player.getLineOfSight(100).isEmpty()) return EventListener.Result.SUCCESS

        BlockPreviewHandler.previewBlock(player, blocks[newIndex].block(), player.getLineOfSight(100).first())

        return EventListener.Result.SUCCESS
    }
}