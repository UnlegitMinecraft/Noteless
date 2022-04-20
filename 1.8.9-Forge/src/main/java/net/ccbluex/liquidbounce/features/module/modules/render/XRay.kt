/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import com.google.common.collect.Lists
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockOre
import net.minecraft.block.BlockRedstoneOre
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import java.awt.Color
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.roundToInt

@ModuleInfo(
    name = "XRay",
    description = "Allows you to see ores through walls.",
    category = ModuleCategory.RENDER
)
class XRay : Module() {
    val bypassValue = BoolValue("Bypass", true)
    private val drawBlockESP = BoolValue("DrawBlockESP", true)
    private val cave = BoolValue("Cave", true)
    var blocks: List<Int> = ArrayList()
    var list: MutableList<XrayBlock> = CopyOnWriteArrayList()
    override fun onEnable() {
        mc.renderGlobal.loadRenderers()
    }

    override fun onDisable() {
        mc.renderGlobal.loadRenderers()
        list.clear()
    }

    @EventTarget
    fun onWorld(event : WorldEvent) {
        mc.renderGlobal.loadRenderers()
    }
    fun getColor(red: Int, green: Int, blue: Int): Int {
        return getColor(red, green, blue, 255)
    }
    fun getColor(red: Int, green: Int, blue: Int, alpha: Int): Int {
        var color = 0
        color = color or (alpha shl 24)
        color = color or (red shl 16)
        color = color or (green shl 8)
        color = color or blue
        return color
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        for (xrayBlock in list) {
            if (mc.theWorld.getBlockState(BlockPos(xrayBlock.x, xrayBlock.y, xrayBlock.z)).block is BlockAir) {
                list.remove(xrayBlock)
            }
            if (drawBlockESP.get()) {
                if (xrayBlock.type.contains("Diamond") || xrayBlock.type.contains("钻石"))
                    RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY,
                        xrayBlock.z - mc.renderManager.viewerPosZ, getColor(55, 155, 255),
                        Color(55, 155, 255).rgb, 2f) else if (xrayBlock.type.contains("Iron") || xrayBlock.type.contains("铁")) RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY,
                    xrayBlock.z - mc.renderManager.viewerPosZ, getColor(180, 180, 180),
                    Color(180, 180, 180).rgb, 2f) else if (xrayBlock.type.contains("Gold") || xrayBlock.type.contains("金")) RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY,
                    xrayBlock.z - mc.renderManager.viewerPosZ, getColor(255, 255, 120), Color(255, 255, 120).rgb,
                    2f) else if (xrayBlock.type.contains("Red") || xrayBlock.type.contains("红")) {
                    RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY,
                        xrayBlock.z - mc.renderManager.viewerPosZ, getColor(255, 50, 50), Color(255, 50, 50).rgb,
                        2f)
                } else if(xrayBlock.type.contains("Lapis") || xrayBlock.type.contains("青")) {
                    RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY, xrayBlock.z - mc.renderManager.viewerPosZ, getColor(12, 26, 255), Color(12, 26, 255).rgb, 2F)
                } else if(xrayBlock.type.contains("绿") || xrayBlock.type.contains("Emerald")) {
                    RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY, xrayBlock.z - mc.renderManager.viewerPosZ, getColor(50, 255, 50), Color(50, 255, 50).rgb, 2F)
                } else {
                    RenderUtils.drawblock2(xrayBlock.x - mc.renderManager.viewerPosX, xrayBlock.y - mc.renderManager.viewerPosY,
                        xrayBlock.z - mc.renderManager.viewerPosZ, getColor(50, 50, 50), Color(50, 50, 50).rgb, 2F)
                }
            }
        }
    }
    private fun containsID(id: Int): Boolean {
        return Lists.newArrayList(10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62,
            73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154).contains(id)
    }
    @EventTarget
    fun onBlockRenderSide(e: BlockRenderSideEvent) {
        if(!drawBlockESP.get())
            return
        if (!cave.value && containsID(Block.getIdFromBlock(e.state?.block))
            && !if (e.side == EnumFacing.DOWN && e.minY > 0.0) true else if (e.side === EnumFacing.UP && e.maxY < 1.0) true else if (e.side == EnumFacing.NORTH && e.minZ > 0.0) true else if (e.side == EnumFacing.SOUTH && e.maxZ < 1.0) true else if (e.side === EnumFacing.WEST && e.minX > 0.0) true else if (e.side == EnumFacing.EAST && e.maxX < 1.0) true else !e.world.getBlockState(e.pos).block.isOpaqueCube) {
            e.isToRender = true
        } else {
            if (!cave.value) {
                e.cancelEvent()
            }
        }
        if (e.side == EnumFacing.DOWN && e.minY > 0.0 || e.side == EnumFacing.UP && e.maxY < 1.0 || e.side == EnumFacing.NORTH && e.minZ > 0.0 || e.side == EnumFacing.SOUTH && e.maxZ < 1.0 || e.side == EnumFacing.WEST && e.minX > 0.0 || e.side == EnumFacing.EAST && e.maxX < 1.0 || !e.world.getBlockState(e.pos).block.isOpaqueCube) {
            if (mc.theWorld.getBlockState(e.pos.offset(e.side, -1)).block is BlockOre
                || mc.theWorld.getBlockState(e.pos.offset(e.side, -1))
                    .block is BlockRedstoneOre) {
                val xDiff = (mc.thePlayer.posX - e.pos.x).toFloat()
                val yDiff = 0f
                val zDiff = (mc.thePlayer.posZ - e.pos.z).toFloat()
                val dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff)
                if (dis > 50) return
                val x = XrayBlock(
                    e.pos.offset(e.side, -1).x.toFloat().roundToInt(),
                    e.pos.offset(e.side, -1).y.toFloat().roundToInt(),
                    e.pos.offset(e.side, -1).z.toFloat().roundToInt(),
                    mc.theWorld.getBlockState(e.pos.offset(e.side, -1)).block.unlocalizedName)
                if (!list.contains(x)) list.add(x)
                val iBlockState : IBlockState = mc.theWorld.getBlockState(BlockPos(
                    e.pos.offset(e.side, -1).x.toDouble().roundToInt().toDouble(), e.pos.offset(e.side, -1).y.toDouble()
                        .roundToInt().toDouble(), e.pos.offset(
                        e.side,
                        -1
                    ).z.toDouble().roundToInt().toDouble()))
                if(iBlockState.block is BlockAir) {
                    if(list.contains(x)) {
                        list.remove(x)
                    }
                }
            }
        }
    }
    val xrayBlocks = mutableListOf<Block>(
        Blocks.coal_ore,
        Blocks.iron_ore,
        Blocks.gold_ore,
        Blocks.redstone_ore,
        Blocks.lapis_ore,
        Blocks.diamond_ore,
        Blocks.emerald_ore,
        Blocks.quartz_ore,
        Blocks.clay,
        Blocks.glowstone,
        Blocks.crafting_table,
        Blocks.torch,
        Blocks.ladder,
        Blocks.tnt,
        Blocks.coal_block,
        Blocks.iron_block,
        Blocks.gold_block,
        Blocks.diamond_block,
        Blocks.emerald_block,
        Blocks.redstone_block,
        Blocks.lapis_block,
        Blocks.fire,
        Blocks.mossy_cobblestone,
        Blocks.mob_spawner,
        Blocks.end_portal_frame,
        Blocks.enchanting_table,
        Blocks.bookshelf,
        Blocks.command_block,
        Blocks.lava,
        Blocks.flowing_lava,
        Blocks.water,
        Blocks.flowing_water,
        Blocks.furnace,
        Blocks.lit_furnace
    )

    init {
        LiquidBounce.commandManager.registerCommand(object : Command("xray", emptyArray()) {

            override fun execute(args: Array<String>) {
                if (args.size > 1) {
                    if (args[1].equals("add", ignoreCase = true)) {
                        if (args.size > 2) {
                            try {
                                val block = try {
                                    Block.getBlockById(args[2].toInt())
                                } catch (exception: NumberFormatException) {
                                    val tmpBlock = Block.getBlockFromName(args[2])

                                    if (Block.getIdFromBlock(tmpBlock) <= 0 || tmpBlock == null) {
                                        chat("§7Block §8${args[2]}§7 does not exist!")
                                        return
                                    }

                                    tmpBlock
                                }

                                if (xrayBlocks.contains(block)) {
                                    chat("This block is already on the list.")
                                    return
                                }

                                xrayBlocks.add(block)
                                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.xrayConfig)
                                chat("§7Added block §8${block.localizedName}§7.")
                                playEdit()
                            } catch (exception: NumberFormatException) {
                                chatSyntaxError()
                            }

                            return
                        }

                        chatSyntax("xray add <block_id>")
                        return
                    }

                    if (args[1].equals("remove", ignoreCase = true)) {
                        if (args.size > 2) {
                            try {
                                var block: Block

                                try {
                                    block = Block.getBlockById(args[2].toInt())
                                } catch (exception: NumberFormatException) {
                                    block = Block.getBlockFromName(args[2])

                                    if (Block.getIdFromBlock(block) <= 0) {
                                        chat("§7Block §8${args[2]}§7 does not exist!")
                                        return
                                    }
                                }

                                if (!xrayBlocks.contains(block)) {
                                    chat("This block is not on the list.")
                                    return
                                }

                                xrayBlocks.remove(block)
                                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.xrayConfig)
                                chat("§7Removed block §8${block.localizedName}§7.")
                                playEdit()
                            } catch (exception: NumberFormatException) {
                                chatSyntaxError()
                            }

                            return
                        }
                        chatSyntax("xray remove <block_id>")
                        return
                    }

                    if (args[1].equals("list", ignoreCase = true)) {
                        chat("§8Xray blocks:")
                        xrayBlocks.forEach { chat("§8${it.localizedName} §7-§c ${Block.getIdFromBlock(it)}") }
                        return
                    }
                }

                chatSyntax("xray <add, remove, list>")
            }
        })
    }

    override fun onToggle(state: Boolean) {
        mc.renderGlobal.loadRenderers()
    }
}
data class XrayBlock(var x: Int, var y: Int, var z: Int, var type: String)
