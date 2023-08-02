package com.loudbook.dev

import com.loudbook.dev.commands.PlaceCommand
import com.loudbook.dev.commands.StopCommand
import com.loudbook.dev.listener.PreviewBlockHandler
import com.loudbook.dev.listener.BreakBlockHandler
import com.loudbook.dev.listener.ConsoleHandler
import com.loudbook.dev.listener.PlaceBlockHandler
import com.loudbook.dev.managers.TimerManager
import com.loudbook.dev.managers.config.ConfigManager
import com.loudbook.dev.managers.config.Configurable
import com.loudbook.dev.world.World
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.extras.MojangAuth
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.utils.NamespaceID
import net.minestom.server.world.DimensionType

class MSlashWorld : Configurable() {
    companion object {
        var fullbright: DimensionType = DimensionType.builder(NamespaceID.from("minestom:full_bright"))
            .ambientLight(2.0f)
            .build()

        @JvmStatic
        fun main(args: Array<String>) {
            ConfigManager.loadConfig()

            ConsoleHandler()

            val minecraftServer = MinecraftServer.init()
            MinecraftServer.setBrandName("m/world")

            MojangAuth.init()
            MinecraftServer.getDimensionTypeManager().addDimension(fullbright)

            val world = World()

            val timerManager = TimerManager(world)

            val globalEventHandler = MinecraftServer.getGlobalEventHandler()
            globalEventHandler.addListener(PlayerLoginEvent::class.java) { event ->
                event.setSpawningInstance(world.instance)
                val skin = PlayerSkin.fromUuid(event.player.uuid.toString())
                event.player.skin = skin
                event.player.respawnPoint = Pos(0.0, 1.0, 0.0)
                event.player.isAllowFlying = true
                event.player.isFlying = true
                event.player.gameMode = GameMode.ADVENTURE
                event.player.inventory.addItemStack(ItemStack.of(Material.GRAY_CONCRETE))
            }
                .addListener(PreviewBlockHandler(timerManager))
                .addListener(PlaceBlockHandler(timerManager, world))
                .addListener(BreakBlockHandler())

            MinecraftServer.getCommandManager().register(PlaceCommand(timerManager))
            MinecraftServer.getCommandManager().register(StopCommand(world))

            world.load()

            if (args.isEmpty()) {
                minecraftServer.start("0.0.0.0", 25565)
            } else {
                minecraftServer.start("0.0.0.0", args[0].toInt())
            }

            Runtime.getRuntime().addShutdownHook(Thread {
                world.save()
            })
        }
    }
}