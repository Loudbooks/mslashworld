package com.loudbook.dev.world

import net.minestom.server.item.Material

class World(private val world: Array<Material>) {
    fun serialize(): ByteArray {
        val ser: ByteArray = ByteArray(world.size)
        for (block in world.withIndex()) {
            val blockByte = materialToByte(block.value)
            ser[block.index] = blockByte
        }
        return ser
    }

    fun deserialize(stored: ByteArray): ArrayList<Material> {
        val de: ArrayList<Material> = ArrayList(stored.size)
        for (block in stored.withIndex()) {
            val blockByte = byteToMaterial(block.value)
            de[block.index] = blockByte
        }
        return de
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