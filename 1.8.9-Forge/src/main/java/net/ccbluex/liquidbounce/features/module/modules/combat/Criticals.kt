package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.PlayerUtil
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C07PacketPlayerDigging

@ModuleInfo(name = "Criticals", description = "Automatically deals critical hits.", category = ModuleCategory.COMBAT)
class Criticals : Module() {

    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "Packet",
            "NCPPacket",
            "NoGround",
            "Redesky",
            "NewPacket",
            "Hop",
            "TPHop",
            "Jump",
            "Visual",
                "Hypixel",
            "VerusSmart"
        ),
        "Packet"
    )
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val jumpHeightValue = FloatValue("JumpHeight", 0.42F, 0.1F, 0.42F)
    private val downYValue = FloatValue("DownY", 0f, 0f, 0.1F)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val onlyAuraValue = BoolValue("OnlyAura", false)
    private val debugValue = BoolValue("DebugMessage", false)
    var ss = BoolValue("StopSpeed",true)
    val msTimer = MSTimer()
    private var readyCrits = false
    private var canCrits = true
    private var counter = 0
    private var target = 0
    override fun onEnable() {
        if (modeValue.get().equals("NoGround", ignoreCase = true))
            mc.thePlayer.jump()
        canCrits = true
        counter = 0;
        groundTicks = 0;
    }
    private var groundTicks = 0
    fun 天安门大屠杀(xOffset: Double = 0.0, yOffset: Double = 0.0, zOffset: Double = 0.0, ground: Boolean) {
        val x = mc.thePlayer.posX + xOffset
        val y = mc.thePlayer.posY + yOffset
        val z = mc.thePlayer.posZ + zOffset
        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, ground))
    }


    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (onlyAuraValue.get() && !LiquidBounce.moduleManager[KillAura::class.java]!!.state && !LiquidBounce.moduleManager[TPAura::class.java]!!.state) return

        if (event.targetEntity is EntityLivingBase) {
            val entity = event.targetEntity
            target = entity.entityId
            if (!mc.thePlayer.onGround || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb || mc.thePlayer.isInWater ||
                mc.thePlayer.isInLava || mc.thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                LiquidBounce.moduleManager[Fly::class.java]!!.state || !msTimer.hasTimePassed(delayValue.get().toLong())
            )
                return

            val x = mc.thePlayer.posX
            val y = mc.thePlayer.posY
            val z = mc.thePlayer.posZ

            when (modeValue.get().toLowerCase()) {
                "packet" -> {
                    天安门大屠杀(yOffset = 0.0625, ground = true)
                    天安门大屠杀(ground = false)
                    天安门大屠杀(yOffset = 1.1E-5, ground = false)
                    天安门大屠杀(ground = false)
                }
                "hypixel" -> {
                    val posy = doubleArrayOf(0.05,0.0016,0.0018,0.0016,0.002,0.04,0.0011)
                    for (i in posy.indices) {
                        PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(x, y + posy[i], z, false))
                    }
                }
                "ncppacket" -> {
                    天安门大屠杀(yOffset = 0.11, ground = false)
                    天安门大屠杀(yOffset = 0.1100013579, ground = false)
                    天安门大屠杀(yOffset = 0.0000013579, ground = false)
                }

                "newpacket" -> {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.05250000001304, z, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.01400000001304, z, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00150000001304, z, false))
                }

                "tphop" -> {
                    天安门大屠杀(yOffset = 0.02, ground = false)
                    天安门大屠杀(yOffset = 0.01, ground = false)
                    mc.thePlayer.setPosition(x, y + 0.01, z)
                }

                "jump" -> {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = jumpHeightValue.get().toDouble()
                    } else {
                        mc.thePlayer.motionY -= downYValue.get()
                    }
                }

                "verussmart" -> {
                    counter++
                    if (counter == 1) {
                        天安门大屠杀(yOffset = 0.0114514, ground = true)
                        天安门大屠杀(ground = false)
                    }
                    if (counter >= 5)
                        counter = 0
                }

                "visual" -> mc.thePlayer.onCriticalHit(entity)
            }
            if (debugValue.get()) {
                PlayerUtil.tellPlayer("\u00a7oCrit: " + "\u00a7c\u00a7o" + randomNumber(-9999, 9999))
            }
            readyCrits = true
            msTimer.reset()
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        groundTicks = if (MovementUtils.isOnGround(0.0)) groundTicks + 1 else 0
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (onlyAuraValue.get() && !LiquidBounce.moduleManager[KillAura::class.java]!!.state) return
        val killaure = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        val packet = event.packet
        if (packet is C03PacketPlayer && killaure.target != null && groundTicks > 1 && modeValue.get().equals("Hypixel", ignoreCase = true)) {
            if(!mc.thePlayer.onGround || mc.thePlayer.isOnLadder || mc.thePlayer.isInWeb || mc.thePlayer.isInWater ||
                    mc.thePlayer.isInLava || mc.thePlayer.ridingEntity != null || killaure.target!!.hurtTime > hurtTimeValue.get() ||
                    LiquidBounce.moduleManager[Fly::class.java]!!.state || !msTimer.hasTimePassed(delayValue.get().toLong()) || (ss.get() && LiquidBounce.moduleManager[Speed::class.java]!!.state))
                return
            packet.onGround = false
        }
        when (modeValue.get().toLowerCase()) {
            "redesky" -> {
                if (packet is C03PacketPlayer) {
                    val packetPlayer: C03PacketPlayer = packet as C03PacketPlayer
                    if (mc.thePlayer.onGround && canCrits) {
                        packetPlayer.y += 0.000001
                        packetPlayer.onGround = false
                    }
                    if (mc.theWorld.getCollidingBoundingBoxes(
                            mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(
                                0.0, (mc.thePlayer.motionY - 0.08) * 0.98, 0.0
                            ).expand(0.0, 0.0, 0.0)
                        ).isEmpty()
                    ) {
                        packetPlayer.onGround = true;
                    }
                }
                if (packet is C07PacketPlayerDigging) {
                    if (packet.status == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                        canCrits = false;
                    } else if (packet.status == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK || packet.status == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                        canCrits = true;
                    }
                }
            }

            "noground" -> {
                if (packet is C03PacketPlayer) {
                    packet.onGround = false
                }
            }
        }

    }

    private fun randomNumber(max: Int, min: Int): Int {
        return (Math.random() * (max - min).toDouble()).toInt() + min
    }


    override val tag: String?
        get() = modeValue.get()
}