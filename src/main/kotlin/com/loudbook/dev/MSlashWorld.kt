package com.loudbook.dev

import com.akuleshov7.ktoml.Toml
import com.loudbook.dev.commands.PlaceCommand
import com.loudbook.dev.listener.PlaceBlockHandler
import com.loudbook.dev.listener.BlockPreviewHandler
import kotlinx.serialization.decodeFromString
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType
import java.io.File
import java.nio.charset.Charset

class MSlashWorld {
    companion object {
        private var fullbright: DimensionType = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
            .ambientLight(2.0f)
            .build()

        @JvmStatic
        fun main(args: Array<String>) {
            val configString = File("config.toml").readText(charset = Charset.defaultCharset())
            val config = Toml.decodeFromString<Config>(configString)
            val minecraftServer = MinecraftServer.init()
            val instanceManager = MinecraftServer.getInstanceManager()

            val timerManager = TimerManager(config)

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
                event.player.gameMode = GameMode.CREATIVE
                event.player.respawnPoint = Pos(0.0, 1.0, 0.0)
                event.player.isAllowFlying = true
                event.player.isFlying = true
                event.player.gameMode = GameMode.CREATIVE
            }
                .addListener(BlockPreviewHandler(config, timerManager))
                .addListener(PlaceBlockHandler(config, timerManager))

            MinecraftServer.getCommandManager().register(PlaceCommand(config, timerManager))

            minecraftServer.start("0.0.0.0", config.port)
        }
    }
}