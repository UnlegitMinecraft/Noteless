/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.DebugUtil
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.util.BlockPos
import java.util.concurrent.ThreadLocalRandom

@ModuleInfo(name = "Velocity", description = "Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Vertical", 0F, 0F, 1F)
    private val alerts = BoolValue("alerts", true)
    private val velocityTickValue = IntegerValue("VelocityTick", 1, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("Cancel","PacketPhase","Spoof","Tick","Vanilla","Clean","AAC4"), "Cancel")
    private val phaseHeightValue = FloatValue("PhaseHeight", 0.5F, 0F, 1F)
    private val phaseOnlyGround = BoolValue("PhaseOnlyGround", true)
    private val onlyCombatValue = BoolValue("OnlyCombat", false)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val noFireValue = BoolValue("noFire", false)
    // Revers
    // Legit
    private var pos:BlockPos?=null
    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityInput = false

    // SmoothReverse
    private var velocityTick = 0
    // AACPush
    private var var0 = false;//hurt
    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(velocityInput) {
            velocityTick++
        }else velocityTick = 0

        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb)
            return
        if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }
        if (noFireValue.get() && mc.thePlayer.isBurning) return
        when (modeValue.get().toLowerCase()) {
            "tick" -> {
                if(velocityTick > velocityTickValue.get()) {
                    if(mc.thePlayer.motionY > 0) mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.jumpMovementFactor = -0.00001f
                    velocityInput = false
                }
                if(mc.thePlayer.onGround && velocityTick > 1) {
                    velocityInput = false
                }
            }
            "aac4" -> {
                if (!mc.thePlayer.onGround) {
                    if (mc.thePlayer.hurtTime != 0 && var0) {
                        if (alerts.get()) {
                            DebugUtil.log(
                                "Velocity",
                                ThreadLocalRandom.current().nextInt(1000, 10000).toString()
                            )
                        }
                        mc.thePlayer.motionX *= 0.6;
                        mc.thePlayer.motionZ *= 0.6;
                    }
                } else if (velocityTimer.hasTimePassed(80)) {
                    var0 = false
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S12PacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer)
                return

            velocityTimer.reset()
            if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
                return
            }
            if (noFireValue.get() && mc.thePlayer.isBurning) return
            velocityTimer.reset()
            velocityTick = 0
            when (modeValue.get().toLowerCase()) {
                "tick" -> {
                    velocityInput = true
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F) {
                        event.cancelEvent()
                    }

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }
                "vanilla" -> {
                    event.cancelEvent()
                    if (alerts.get()) {
                        DebugUtil.log(
                            "Velocity",
                            ThreadLocalRandom.current().nextInt(1000, 10000).toString()
                        )
                    }
                }
                "spoof" -> {
                    event.cancelEvent()
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + packet.motionX / 8000.0, mc.thePlayer.posY + packet.motionY / 8000.0, mc.thePlayer.posZ + packet.motionZ / 8000.0, false))
                }
                "packetphase" -> {
                    if (!mc.thePlayer.onGround && phaseOnlyGround.get()) {
                        return
                    }

//                    chat("MOTX=${packet.motionX}, MOTZ=${packet.motionZ}")
                    if (packet.motionX <500 && packet.motionY <500) {
                        return
                    }

                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - phaseHeightValue.get(), mc.thePlayer.posZ, false))
                    event.cancelEvent()
                    packet.motionX = 0
                    packet.motionY = 0
                    packet.motionZ = 0
                }
                "aac4" -> {
                    velocityTimer.reset();
                    var0 = true;
                }
                "clean"->{
                    if (alerts.get()) {
                        DebugUtil.log(
                            "Velocity",
                            ThreadLocalRandom.current().nextInt(1000, 10000).toString()
                        )
                    }
                     if (0 < mc.thePlayer.hurtTime) {
                        if (packet is S12PacketEntityVelocity) {
                            if(packet is C03PacketPlayer) {
                                packet.y += 0.11451419198
                            }
                        }
                        if (packet is S12PacketEntityVelocity) {
                            packet.motionX = 0 * packet.getMotionX();
                            packet.motionY = 0 * packet.getMotionY();
                            packet.motionZ = 0 * packet.getMotionZ()
                        }
                    } else {
                        if (packet is S12PacketEntityVelocity) {
                            packet.motionX = 0 * packet.getMotionX();
                            packet.motionY = 0 * packet.getMotionY();
                            packet.motionZ = 0 * packet.getMotionZ()
                        }
                        if (packet is S12PacketEntityVelocity) {
                            if(packet is C03PacketPlayer) {
                                packet.y += 0.11451419198
                            }
                        }
                    }
                }
                "cancel" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()
                    if (alerts.get()) {
                        DebugUtil.log(
                            "Velocity",
                            ThreadLocalRandom.current().nextInt(1000, 10000).toString()
                        )
                    }
                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()
                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }
            }
        }

        if (packet is S27PacketExplosion) {
            // TODO: Support velocity for explosions
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mc.thePlayer == null || mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb)
            return
        if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }
    }
}
