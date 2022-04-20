/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.TimeHelper
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.util.StatCollector

@ModuleInfo(
    name = "ChestStealer",
    description = "ChestStealer",
    category = ModuleCategory.PLAYER,
    fakeName = "Chest Stealer"
)
class ChestStealer : Module() {
    private val delay = IntegerValue("Delay", 200, 0, 1000)
    val silentValue = BoolValue("Silent", false)
    val silenceTitleValue = BoolValue("SilenceTitle", true)

    private val timer = TimeHelper()
    override val tag: String
        get() = "${delay.get()}.0"

    @EventTarget
    private fun onTick(event: TickEvent) {
        if (mc.thePlayer != null && mc.thePlayer.openContainer is ContainerChest) {
            val container = mc.thePlayer.openContainer as ContainerChest
            if (!StatCollector.translateToLocal("container.chest")
                    .equals(
                        container.lowerChestInventory.displayName.unformattedText,
                        ignoreCase = true
                    ) && !StatCollector.translateToLocal("container.chestDouble")
                    .equals(container.lowerChestInventory.displayName.unformattedText, ignoreCase = true)
            ) {
                StatCollector.translateToLocal("container.chest")
            }
            var i = 0
            while (i < container.lowerChestInventory.sizeInventory) {
                if (container.lowerChestInventory.getStackInSlot(i) != null && timer.hasReached(delay.get().toLong())
                    && (container.lowerChestInventory.getStackInSlot(i).item !is ItemArmor || betterCheck(
                        container,
                        container.lowerChestInventory.getStackInSlot(i)
                    ))
                    && (container.lowerChestInventory.getStackInSlot(i).item !is ItemSword || getDamage(
                        container.lowerChestInventory.getStackInSlot(
                            i
                        )
                    ) >= bestDamage(container, i))
                ) {
                    mc.playerController.windowClick(container.windowId, i, 0, 1, mc.thePlayer)
                    mc.playerController.windowClick(container.windowId, i, 1, 1, mc.thePlayer)
                    timer.reset()
                }
                ++i
            }
            if (isEmpty) {
                mc.thePlayer.closeScreen()
            }
        }
    }

    private val isEmpty: Boolean
        get() {
            if (mc.thePlayer.openContainer is ContainerChest) {
                val container = mc.thePlayer.openContainer as ContainerChest
                var i = 0
                while (i < container.lowerChestInventory.sizeInventory) {
                    val itemStack = container.lowerChestInventory.getStackInSlot(i)
                    if (itemStack != null && itemStack.item != null) {
                        if (itemStack.item !is ItemArmor || betterCheck(
                                container,
                                itemStack
                            )
                        ) if (itemStack.item !is ItemSword || getDamage(itemStack) >= bestDamage(
                                container,
                                i
                            )
                        ) return false
                    }
                    ++i
                }
            }
            return true
        }

    private fun betterCheck(c: ContainerChest, item: ItemStack): Boolean {
        val item1 = (item.item as ItemArmor).damageReduceAmount + getProtectionValue(item)
        var item2 = 0.0
        var bestslot = 0
        if (item.unlocalizedName.contains("helmet")) {
            for (i in 0..44) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack && mc.thePlayer.inventoryContainer
                        .getSlot(i).stack.item.unlocalizedName.contains("helmet")
                ) {
                    val temp = ((mc.thePlayer.inventoryContainer.getSlot(i).stack
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).stack))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
            for (i in 0 until c.lowerChestInventory.sizeInventory) {
                if (c.lowerChestInventory.getStackInSlot(i) != null
                    && c.lowerChestInventory.getStackInSlot(i).unlocalizedName.contains("helmet")
                ) {
                    val temp = ((c.lowerChestInventory.getStackInSlot(i)
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(c.lowerChestInventory.getStackInSlot(i)))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
        } // 头盔
        if (item.unlocalizedName.contains("chestplate")) {
            for (i in 0..44) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack && mc.thePlayer.inventoryContainer
                        .getSlot(i).stack.item.unlocalizedName.contains("chestplate")
                ) {
                    val temp = ((mc.thePlayer.inventoryContainer.getSlot(i).stack
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).stack))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
            for (i in 0 until c.lowerChestInventory.sizeInventory) {
                if (c.lowerChestInventory.getStackInSlot(i) != null
                    && c.lowerChestInventory.getStackInSlot(i).unlocalizedName.contains("chestplate")
                ) {
                    val temp = ((c.lowerChestInventory.getStackInSlot(i)
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(c.lowerChestInventory.getStackInSlot(i)))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
        } // 胸甲
        if (item.unlocalizedName.contains("leggings")) {
            for (i in 0..44) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack && mc.thePlayer.inventoryContainer
                        .getSlot(i).stack.item.unlocalizedName.contains("leggings")
                ) {
                    val temp = ((mc.thePlayer.inventoryContainer.getSlot(i).stack
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).stack))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
            for (i in 0 until c.lowerChestInventory.sizeInventory) {
                if (c.lowerChestInventory.getStackInSlot(i) != null
                    && c.lowerChestInventory.getStackInSlot(i).unlocalizedName.contains("leggings")
                ) {
                    val temp = ((c.lowerChestInventory.getStackInSlot(i)
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(c.lowerChestInventory.getStackInSlot(i)))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
        } // 护腿
        if (item.unlocalizedName.contains("boots")) {
            for (i in 0..44) {
                if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack && mc.thePlayer.inventoryContainer
                        .getSlot(i).stack.item.unlocalizedName.contains("boots")
                ) {
                    val temp = ((mc.thePlayer.inventoryContainer.getSlot(i).stack
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(mc.thePlayer.inventoryContainer.getSlot(i).stack))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
            for (i in 0 until c.lowerChestInventory.sizeInventory) {
                if (c.lowerChestInventory.getStackInSlot(i) != null
                    && c.lowerChestInventory.getStackInSlot(i).unlocalizedName.contains("boots")
                ) {
                    val temp = ((c.lowerChestInventory.getStackInSlot(i)
                        .item as ItemArmor).damageReduceAmount
                            + getProtectionValue(c.lowerChestInventory.getStackInSlot(i)))
                    if (temp > item2) {
                        item2 = temp
                        bestslot = i
                    }
                }
            }
        } // 鞋子
        return item1 >= item2 && c.lowerChestInventory.getStackInSlot(bestslot) == item
    }

    private fun getProtectionValue(stack: ItemStack): Double {
        return if (stack.item is ItemArmor) {
            (stack.item as ItemArmor).damageReduceAmount.toDouble() + ((100 - (stack.item as ItemArmor).damageReduceAmount)
                    * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)).toDouble() * 0.0075
        } else {
            0.0
        }
    }

    private fun bestDamage(container: ContainerChest, slot: Int): Double {
        var bestDamage = 0.0
        for (i in 0..44) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).hasStack) {
                val `is` = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (`is`.item is ItemSword && getDamage(`is`) > bestDamage) {
                    bestDamage = getDamage(`is`).toDouble()
                }
            }
        }
        for (i in 0 until container.lowerChestInventory.sizeInventory) {
            if (container.lowerChestInventory.getStackInSlot(i) != null) {
                val `is` = container.lowerChestInventory.getStackInSlot(i)
                if (i != slot && `is`.item is ItemSword && getDamage(`is`) > bestDamage) {
                    bestDamage = getDamage(`is`).toDouble()
                }
            }
        }
        return bestDamage
    }

    private fun getDamage(stack: ItemStack): Float {
        var damage = 0f
        val item = stack.item
        if (item is ItemTool) {
            damage += getSpeed(stack)
        }
        damage += if (item is ItemSword) {
            getAttackDamage(stack)
        } else {
            1f
        }
        return damage
    }

    private fun getAttackDamage(itemStack: ItemStack): Float {
        var damage = (itemStack.item as ItemSword).damageVsEntity
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 1.25f
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 0.01f
        return damage
    }

    private fun getSpeed(stack: ItemStack): Float {
        return (stack.item as ItemTool).toolMaterial.efficiencyOnProperMaterial
    }
}