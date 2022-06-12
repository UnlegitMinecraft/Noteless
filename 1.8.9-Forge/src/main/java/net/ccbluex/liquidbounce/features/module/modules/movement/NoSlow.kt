package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.PlayerUtil
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*
import kotlin.math.sqrt

@ModuleInfo(name = "NoSlow", category = ModuleCategory.MOVEMENT, description = "fdp")
class NoSlow : Module() {
    private val modeValue = ListValue("PacketMode", arrayOf("Vanilla", "LiquidBounce", "Watchdog","Custom", "NCP", "AAC4", "AAC5", "Matrix", "Vulcan"), "Vanilla")
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customOnGround = BoolValue("CustomOnGround", false).displayable { modeValue.equals("Custom") }
    private val customDelayValue = IntegerValue("CustomDelay", 60, 10, 200).displayable { modeValue.equals("Custom") }
    // Soulsand
    val soulsandValue = BoolValue("Soulsand", false)
    // Slowdown on teleport
    private val teleportValue = BoolValue("Teleport", false)
    private val teleportModeValue = ListValue("TeleportMode", arrayOf("Vanilla", "VanillaNoSetback", "Custom", "Decrease"), "Vanilla").displayable { teleportValue.get() }
    private val teleportNoApplyValue = BoolValue("TeleportNoApply", false).displayable { teleportValue.get() }
    private val teleportCustomSpeedValue = FloatValue("Teleport-CustomSpeed", 0.13f, 0f, 1f).displayable { teleportValue.get() && teleportModeValue.equals("Custom") }
    private val teleportCustomYValue = BoolValue("Teleport-CustomY", false).displayable { teleportValue.get() && teleportModeValue.equals("Custom") }
    private val teleportDecreasePercentValue = FloatValue("Teleport-DecreasePercent", 0.13f, 0f, 1f).displayable { teleportValue.get() && teleportModeValue.equals("Decrease") }

    private var pendingFlagApplyPacket = false
    private var lastMotionX = 0.0
    private var lastMotionY = 0.0
    private var lastMotionZ = 0.0
    private val msTimer = MSTimer()
    private var sendBuf = false
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false
    private var lastBlockingStat = false

    override fun onDisable() {
        msTimer.reset()
        pendingFlagApplyPacket = false
        sendBuf = false
        packetBuf.clear()
        nextTemp = false
        waitC03 = false
    }

    private fun sendPacket(
        event: MotionEvent,
        sendC07: Boolean,
        sendC08: Boolean,
        delay: Boolean,
        delayValue: Long,
        onGround: Boolean,
        watchDog: Boolean = false
    ) {
        val digging = C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN)
        val blockPlace = C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem())
        val blockMent = C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f)
        if (onGround && !mc.thePlayer.onGround) {
            return
        }
        if (sendC07 && event.eventState == EventState.PRE) {
            if (delay && msTimer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (sendC08 && event.eventState == EventState.POST) {
            if (delay && msTimer.hasTimePassed(delayValue) && !watchDog) {
                mc.netHandler.addToSendQueue(blockPlace)
                msTimer.reset()
            } else if (!delay && !watchDog) {
                mc.netHandler.addToSendQueue(blockPlace)
            } else if (watchDog) {
                mc.netHandler.addToSendQueue(blockMent)
            }
        }
    }
    fun isUsingFood(): Boolean {
        if(mc.thePlayer.itemInUse == null || mc.thePlayer.itemInUse.item == null) return false;
        val usingItem: Item = mc.thePlayer.itemInUse.item
        return mc.thePlayer.isUsingItem && (usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion)
    }
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!MovementUtils.isMoving()) {
            return
        }

//        val heldItem = mc.thePlayer.heldItem
            when (modeValue.get().toLowerCase()) {
                "aac5"-> {
                    if (event.eventState == EventState.POST && (mc.thePlayer.isUsingItem || mc.thePlayer.isBlocking || killAura.blockingStatus)) {
                        mc.netHandler.addToSendQueue(
                            C08PacketPlayerBlockPlacement(
                                BlockPos(-1, -1, -1),
                                255,
                                mc.thePlayer.inventory.getCurrentItem(),
                                0f,
                                0f,
                                0f
                            )
                        )
                    }
                    if (!mc.thePlayer.isBlocking && !killAura.blockingStatus) {
                        return
                    }
                }
                "liquidbounce" -> {
                    sendPacket(event, true, true, false, 0, false)
                }
                "watchdog" -> {
                    if(event.eventState == EventState.PRE){
                        if(isUsingFood()){
                            if(mc.thePlayer.itemInUseDuration >= 1){
                                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                            }
                        }
                    }
                    if(LiquidBounce.combatManager.target != null || !mc.thePlayer.isBlocking || !PlayerUtil.isMoving() || !MovementUtils.isOnGround(0.42))
                        return
                    if(event.eventState == EventState.POST){
                        if(LiquidBounce.combatManager.target != null && !mc.thePlayer.isBlocking || !PlayerUtil.isMoving() || !MovementUtils.isOnGround(0.42)){
                            mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(BlockPos(-1,-1,-1),255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                        }
                    }
                }
                "aac4" -> {
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        sendPacket(event, true, false, false, 0, false)
                    } else if (mc.thePlayer.ticksExisted % 3 == 1) {
                        sendPacket(event, false, true, false, 0, false)
                    }
                }

                "custom" -> {
                    sendPacket(event, true, true, true, customDelayValue.get().toLong(), customOnGround.get())
                }

                "ncp" -> {
                    sendPacket(event, true, true, false, 0, false)
                }
            }
        }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: Item?, isForward: Boolean) = when (item) {
        is ItemFood, is ItemPotion, is ItemBucketMilk -> {
            if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
        }
        is ItemSword -> {
            if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
        }
        is ItemBow -> {
            if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
        }
        else -> 0.2F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        if((modeValue.equals("Matrix") || modeValue.equals("Vulcan")) && (lastBlockingStat || isBlocking)) {
            if(msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                PacketUtils.sendPacketNoEvent(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos(-1, -1, -1), EnumFacing.DOWN))
                if(packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for(packet in packetBuf) {
                        if(packet is C03PacketPlayer) {
                            canAttack = true
                        }
                        if(!((packet is C02PacketUseEntity || packet is C0APacketAnimation) && !canAttack)) {
                            PacketUtils.sendPacketNoEvent(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
            if(!nextTemp) {
                lastBlockingStat = isBlocking
                if (!isBlocking) {
                    return
                }
                PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f))
                nextTemp = true
                waitC03 = modeValue.equals("Vulcan")
                msTimer.reset()
            }
        }
    }
    val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
    private val isBlocking: Boolean
        get() = (mc.thePlayer.isUsingItem || killAura.blockingStatus) && mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if((modeValue.equals("Matrix") || modeValue.equals("Vulcan")) && nextTemp) {
            if((packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) && isBlocking) {
                event.cancelEvent()
            }else if (packet is C03PacketPlayer || packet is C0APacketAnimation || packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement) {
                if (modeValue.equals("Vulcan") && waitC03 && packet is C03PacketPlayer) {
                    waitC03 = false
                    return
                }
                packetBuf.add(packet as Packet<INetHandlerPlayServer>)
                event.cancelEvent()
            }
        } else if (teleportValue.get() && packet is S08PacketPlayerPosLook) {
            pendingFlagApplyPacket = true
            lastMotionX = mc.thePlayer.motionX
            lastMotionY = mc.thePlayer.motionY
            lastMotionZ = mc.thePlayer.motionZ
            when (teleportModeValue.get().toLowerCase()) {
                "vanillanosetback" -> {
                    val x = packet.x - mc.thePlayer.posX
                    val y = packet.y - mc.thePlayer.posY
                    val z = packet.z - mc.thePlayer.posZ
                    val diff = sqrt(x * x + y * y + z * z)
                    if (diff <= 8) {
                        event.cancelEvent()
                        pendingFlagApplyPacket = false
                        PacketUtils.sendPacketNoEvent(C06PacketPlayerPosLook(packet.x, packet.y, packet.z, packet.getYaw(), packet.getPitch(), mc.thePlayer.onGround))
                    }
                }
            }
        } else if (pendingFlagApplyPacket && packet is C06PacketPlayerPosLook) {
            pendingFlagApplyPacket = false
            if (teleportNoApplyValue.get()) {
                event.cancelEvent()
            }
            when (teleportModeValue.get().toLowerCase()) {
                "vanilla", "vanillanosetback" -> {
                    mc.thePlayer.motionX = lastMotionX
                    mc.thePlayer.motionY = lastMotionY
                    mc.thePlayer.motionZ = lastMotionZ
                }
                "custom" -> {
                    if (MovementUtils.isMoving()) {
                        MovementUtils.strafe(teleportCustomSpeedValue.get())
                    }

                    if (teleportCustomYValue.get()) {
                        if (lastMotionY> 0) {
                            mc.thePlayer.motionY = teleportCustomSpeedValue.get().toDouble()
                        } else {
                            mc.thePlayer.motionY = -teleportCustomSpeedValue.get().toDouble()
                        }
                    }
                }
                "decrease" -> {
                    mc.thePlayer.motionX = lastMotionX * teleportDecreasePercentValue.get()
                    mc.thePlayer.motionY = lastMotionY * teleportDecreasePercentValue.get()
                    mc.thePlayer.motionZ = lastMotionZ * teleportDecreasePercentValue.get()
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}