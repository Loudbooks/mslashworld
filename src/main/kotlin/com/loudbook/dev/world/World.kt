package com.loudbook.dev.world

import com.loudbook.dev.MSlashWorld
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.time.measureTime

class World {
    private val instanceManager = MinecraftServer.getInstanceManager()
    var instance: InstanceContainer = instanceManager.createInstanceContainer(MSlashWorld.fullbright)
    var blocks = mutableMapOf<Pos, Material>()

    init {
        instance.setGenerator { unit ->
            unit.modifier().fillHeight(0, 1, Block.WHITE_CONCRETE)
        }
        instance.worldBorder.setCenter(0f, 0f)
        instance.worldBorder.setDiameter(200.0)
    }

    fun load() {
        loadFromFile()
    }

    private fun serialize(): MutableMap<Triple<Double, Double, Double>, Byte> {
        val blockMap = mutableMapOf<Triple<Double, Double, Double>, Byte>()
        for (pos in blocks.keys.withIndex()) {
            blockMap[Triple(pos.value.x, pos.value.y, pos.value.z)] = materialToByte(blocks[pos.value]!!)
        }
        return blockMap
    }

    private fun deserialize(file: File): MutableMap<Pos, Material> {
        val inputStream = ObjectInputStream(file.inputStream())
        val stored = inputStream.readObject() as MutableMap<Triple<Double, Double, Double>, Byte>

        val blocks = mutableMapOf<Pos, Material>()
        for (pos in stored.keys.withIndex()) {
            blocks[Pos(pos.value.first, pos.value.second, pos.value.third)] = byteToMaterial(stored[pos.value]!!)
        }

        return blocks
    }

    fun save() {
        val file = File("./world.dat")

        if (file.exists()) {
            file.delete()
        }

        val timeTaken = measureTime {
            val objectOutputStream = ObjectOutputStream(FileOutputStream(file))
            objectOutputStream.writeObject(serialize())
            file.createNewFile()
        }

        MinecraftServer.LOGGER.info("Saved ${this.blocks.size} blocks in ${timeTaken.inWholeMilliseconds}ms!")
    }

    private fun loadFromFile() {
        val file = File("./world.dat")

        if (!file.exists()) {
            return
        }

        this.blocks = deserialize(file)

        MinecraftServer.LOGGER.info("Loaded valid world file! Placing...")

        val timeTaken = measureTime {
            for (block in this.blocks) {
                instance.setBlock(block.key, block.value.block())
            }
        }

        MinecraftServer.LOGGER.info("Placed ${this.blocks.size} blocks in ${timeTaken.inWholeMilliseconds}ms!")
    }

    private fun materialToByte(block: Material): Byte {
        return when (block) {
            Material.RED_CONCRETE -> 1
            Material.ORANGE_CONCRETE -> 2
            Material.YELLOW_CONCRETE -> 3
            Material.LIME_CONCRETE -> 4
            Material.GREEN_CONCRETE -> 5
            Material.CYAN_CONCRETE -> 6
            Material.LIGHT_BLUE_CONCRETE -> 7
            Material.BLUE_CONCRETE -> 8
            Material.PURPLE_CONCRETE -> 9
            Material.MAGENTA_CONCRETE -> 10
            Material.PINK_CONCRETE -> 11
            Material.BROWN_CONCRETE -> 12
            Material.BLACK_CONCRETE -> 13
            Material.GRAY_CONCRETE -> 14
            Material.LIGHT_GRAY_CONCRETE -> 15
            Material.WHITE_CONCRETE -> 16
            else -> {
                0
            }
        }
    }

    private fun byteToMaterial(byte: Byte): Material {
        return when (byte) {
            1.toByte() -> Material.RED_CONCRETE
            2.toByte() -> Material.ORANGE_CONCRETE
            3.toByte() -> Material.YELLOW_CONCRETE
            4.toByte() -> Material.LIME_CONCRETE
            5.toByte() -> Material.GREEN_CONCRETE
            6.toByte() -> Material.CYAN_CONCRETE
            7.toByte() -> Material.LIGHT_BLUE_CONCRETE
            8.toByte() -> Material.BLUE_CONCRETE
            9.toByte() -> Material.PURPLE_CONCRETE
            10.toByte() -> Material.MAGENTA_CONCRETE
            11.toByte() -> Material.PINK_CONCRETE
            12.toByte() -> Material.BROWN_CONCRETE
            13.toByte() -> Material.BLACK_CONCRETE
            14.toByte() -> Material.GRAY_CONCRETE
            15.toByte() -> Material.LIGHT_GRAY_CONCRETE
            16.toByte() -> Material.WHITE_CONCRETE
            else -> {
                Material.AIR
            }
        }
    }
}