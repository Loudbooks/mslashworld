package com.loudbook.dev

import com.loudbook.dev.listener.PlaceBlockHandler
import com.loudbook.dev.listener.BlockPreviewHandler
import com.loudbook.dev.listener.PaletteHandler
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType

class MSlashWorld {
    companion object {
        private var fullbright: DimensionType = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
            .ambientLight(2.0f)
            .build()

        @JvmStatic
        fun main(args: Array<String>) {
            val minecraftServer = MinecraftServer.init()
            val instanceManager = MinecraftServer.getInstanceManager()

            val timerManager = TimerManager()

            MojangAuth.init()
            MinecraftServer.getDimensionTypeManager().addDimension(fullbright)

            val worldInstance = instanceManager.createInstanceContainer(fullbright)
            worldInstance.setGenerator { unit ->
                    unit.modifier().fillHeight(0, 1, Block.WHITE_CONCRETE)
            }
            worldInstance.worldBorder.setCenter(0f, 0f)
            worldInstance.worldBorder.setDiameter(200.0)

            val globalEventHandler = MinecraftServer.getGlobalEventHandler()
            globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
                event.setSpawningInstance(worldInstance)
                val skin = PlayerSkin.fromUuid(event.player.uuid.toString())
                event.player.skin = skin
                event.player.gameMode = GameMode.ADVENTURE
                event.player.respawnPoint = Pos(0.0, 1.0, 0.0)
                event.player.isAllowFlying = true
                event.player.isFlying = true

                for (i in 0..8) {
                    event.player.inventory.setItemStack(i, ItemStack.of(Material.RED_CONCRETE))
                }

            }
                .addListener(BlockPreviewHandler(timerManager))
                .addListener(PlaceBlockHandler(timerManager))
                .addListener(PaletteHandler(timerManager))

            if (args.isEmpty()) {
                minecraftServer.start("0.0.0.0", 25565)
            } else {
                minecraftServer.start("0.0.0.0", args[0].toInt())
            }
        }
    }
}