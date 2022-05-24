package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.VecRotation
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.misc.NewFall
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemBucket
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.ceil
import kotlin.math.sqrt

@ModuleInfo(name = "NoFall", spacedName = "No Fall", description = "Prevents you from taking fall damage.", category = ModuleCategory.PLAYER)
class NoFall : Module() {
    val typeValue = ListValue("Type", arrayOf("Edit", "Packet", "MLG", "AAC", "Spartan", "CubeCraft", "Hypixel", "Phase", "Verus", "Medusa", "Motion", "Matrix"), "Edit")

    val editMode = ListValue("Edit-Mode", arrayOf("Always", "Default", "Smart", "NoGround", "Damage"), "Always")
    private val packetMode = ListValue("Packet-Mode", arrayOf("Default", "Smart"), "Default")
    private val aacMode = ListValue("AAC-Mode", arrayOf("Default", "LAAC", "3.3.11", "3.3.15", "4.x", "4.4.x", "Loyisa4.4.2", "5.0.4", "5.0.14"), "Default")
    private val hypixelMode = ListValue("Hypixel-Mode", arrayOf("Default", "Packet", "New"), "Default")
    private val matrixMode = ListValue("Matrix-Mode", arrayOf("Old", "6.2.x", "6.6.3"), "Old")

    private val phaseOffsetValue = IntegerValue("PhaseOffset", 1, 0, 5)
    private val minFallDistanceValue = FloatValue("MinMLGHeight", 5F, 2F, 50F)
    private val flySpeedValue = FloatValue("MotionSpeed", -0.01F, -5F, 5F)

    private val voidCheckValue = BoolValue("Void-Check", true)

    private val aac4FlagCooldown = MSTimer()
    private val spartanTimer = TickTimer()
    private val mlgTimer = TickTimer()

    private var oldaacState = 0

    private var aac4Fakelag = false
    private var aac4FlagCount = 0

    private var aac5doFlag = false
    private var aac5Check = false
    private var aac5Timer = 0
    private val aac4Packets = mutableListOf<C03PacketPlayer>()

    private var currentMlgRotation: VecRotation? = null
    private var currentMlgItemIndex = 0
    private var currentMlgBlock: BlockPos? = null

    private var matrixFalling = false
    private var matrixCanSpoof = false
    private var matrixFallTicks = 0
    private var matrixLastMotionY = 0.0
    private var matrixFlagWait = 0
    private var matrixSend = false

    private var isDmgFalling = false
    private var jumped = false
    private var modifiedTimer = false
    private var packetModify = false
    private var needSpoof = false
    private var lastFallDistRounded = 0

    override fun onEnable() {
        aac4FlagCount = 0
        aac4Fakelag = false
        aac5Check = false
        packetModify = false
        aac4Packets.clear()
        needSpoof = false
        aac5doFlag = false
        aac5Timer = 0
        lastFallDistRounded = 0
        oldaacState = 0
        matrixFalling = false
        matrixCanSpoof = false
        matrixFallTicks = 0
        matrixLastMotionY = 0.0
        isDmgFalling = false
        matrixFlagWait = 0
        aac4FlagCooldown.reset()
    }

    override fun onDisable() {
        matrixSend = false
        aac4FlagCount = 0
        aac4Fakelag = false
        aac5Check = false
        packetModify = false
        aac4Packets.clear()
        needSpoof = false
        aac5doFlag = false
        aac5Timer = 0
        lastFallDistRounded = 0
        oldaacState = 0
        matrixFalling = false
        matrixCanSpoof = false
        matrixFallTicks = 0
        matrixLastMotionY = 0.0
        isDmgFalling = false
        matrixFlagWait = 0
        aac4FlagCooldown.reset()
        mc.timer.timerSpeed = 1.0f
    }

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent) {
        if (modifiedTimer) {
            mc.timer.timerSpeed = 1.0F
            modifiedTimer = false
        }

        if (mc.thePlayer.onGround)
            jumped = false

        if (mc.thePlayer.motionY > 0)
            jumped = true

        if (!state || LiquidBounce.moduleManager[FreeCam::class.java]!!.state
                || mc.thePlayer.isSpectator || mc.thePlayer.capabilities.allowFlying || mc.thePlayer.capabilities.disableDamage)
            return

        if (!LiquidBounce.moduleManager[Fly::class.java]!!.state && voidCheckValue.get() && !MovementUtils.isBlockUnder()) return

        if (BlockUtils.collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } || BlockUtils.collideBlock(AxisAlignedBB(mc.thePlayer.entityBoundingBox.maxX, mc.thePlayer.entityBoundingBox.maxY, mc.thePlayer.entityBoundingBox.maxZ, mc.thePlayer.entityBoundingBox.minX, mc.thePlayer.entityBoundingBox.minY - 0.01, mc.thePlayer.entityBoundingBox.minZ)) { it is BlockLiquid })
            return

        if (matrixFlagWait > 0) {
            if (matrixFlagWait-- == 0)
                mc.timer.timerSpeed = 1F
        }

        when (typeValue.get().toLowerCase()) {
            "packet" -> when (packetMode.get().toLowerCase()) {
                "default" -> {
                    if (mc.thePlayer.fallDistance > 2F)
                        mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }
                "smart" -> {
                    if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3f) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                        mc.thePlayer.fallDistance = 0f
                    }
                }
            }
            "cubecraft" -> {
                if (mc.thePlayer.fallDistance > 2F) {
                    mc.thePlayer.onGround = true
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }
            }
            "spartan" -> {
                spartanTimer.update()

                if (mc.thePlayer.fallDistance > 1.5F && spartanTimer.hasTimePassed(10)) {
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10, mc.thePlayer.posZ, true))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 10, mc.thePlayer.posZ, true))
                    spartanTimer.reset()
                }
            }
            "verus" -> {
                if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3F) {
                    mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX *= 0.5
                    mc.thePlayer.motionX *= 0.5
                    mc.thePlayer.fallDistance = 0F
                    needSpoof = true
                }
            }
            "matrix" -> when (matrixMode.get().toLowerCase()) {
                "old" -> {
                    if (mc.thePlayer.fallDistance > 3)
                        isDmgFalling = true
                }
                "6.6.3" -> {
                    if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3F) {
                        mc.thePlayer.fallDistance = 0.0f
                        matrixSend = true
                        mc.timer.timerSpeed = 0.45f
                        modifiedTimer = true
                    }
                }
                "6.2.x" -> {
                    if (matrixFalling) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.jumpMovementFactor = 0f
                        if (mc.thePlayer.onGround)
                            matrixFalling = false
                    }
                    if (mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3F) {
                        matrixFalling = true
                        if (matrixFallTicks == 0)
                            matrixLastMotionY = mc.thePlayer.motionY
                        mc.thePlayer.motionY = 0.0
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        mc.thePlayer.jumpMovementFactor = 0f
                        mc.thePlayer.fallDistance = 3.2f
                        if (matrixFallTicks in 8..9)
                            matrixCanSpoof = true
                        matrixFallTicks++
                    }
                    if (matrixFallTicks > 12 && !mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = matrixLastMotionY
                        mc.thePlayer.fallDistance = 0f
                        matrixFallTicks = 0
                        matrixCanSpoof = false
                    }
                }
            }
            "aac" -> when (aacMode.get().toLowerCase()) {
                "default" -> {
                    if (mc.thePlayer.fallDistance > 2f) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                        oldaacState = 2
                    } else if (oldaacState == 2 && mc.thePlayer.fallDistance < 2) {
                        mc.thePlayer.motionY = 0.1
                        oldaacState = 3
                        return
                    }
                    if (oldaacState in 3..5) {
                        mc.thePlayer.motionY = 0.1
                        if (oldaacState == 5) oldaacState = 1
                    }
                }
                "laac" -> if (!jumped && mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInWeb) {
                    mc.thePlayer.motionY = -6.0
                }
                "3.3.11" -> if (mc.thePlayer.fallDistance > 2F) {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 10E-4, mc.thePlayer.posZ, mc.thePlayer.onGround))
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }
                "3.3.15" -> if (mc.thePlayer.fallDistance > 2) {
                    if (!mc.isIntegratedServerRunning) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.NaN, mc.thePlayer.posZ, false))
                    }
                    mc.thePlayer.fallDistance = -9999f
                }
                "5.0.4", "loyisa4.4.2" -> {
                    if (mc.thePlayer.fallDistance > 3)
                        isDmgFalling = true

                    if (aacMode.get().equals("loyisa4.4.2", true)) {
                        if (aac4FlagCount >= 3 || aac4FlagCooldown.hasTimePassed(1500L)) {
                            return
                        }
                        if (!aac4FlagCooldown.hasTimePassed(1500L) && (mc.thePlayer.onGround || mc.thePlayer.fallDistance < 0.5)) {
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                            mc.thePlayer.onGround = false
                            mc.thePlayer.jumpMovementFactor = 0.0f
                        }
                    }
                }
                "5.0.14" -> {
                    var offsetYs = 0.0
                    aac5Check = false

                    while (mc.thePlayer.motionY - 1.5 < offsetYs) {
                        val blockPos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + offsetYs, mc.thePlayer.posZ)
                        val block = BlockUtils.getBlock(blockPos)
                        val axisAlignedBB = block!!.getCollisionBoundingBox(mc.theWorld, blockPos, BlockUtils.getState(blockPos))
                        if (axisAlignedBB != null) {
                            offsetYs = -999.9
                            aac5Check = true
                        }
                        offsetYs -= 0.5
                    }

                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.fallDistance = -2f
                        aac5Check = false
                    }

                    if (aac5Timer > 0)
                        aac5Timer--

                    if (aac5Check && mc.thePlayer.fallDistance > 2.5 && !mc.thePlayer.onGround) {
                        aac5doFlag = true
                        aac5Timer = 18
                    } else if (aac5Timer < 2)
                        aac5doFlag = false

                    if (aac5doFlag)
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + if (mc.thePlayer.onGround) 0.5 else 0.42, mc.thePlayer.posZ, true))
                }
            }
            "motion" -> if (mc.thePlayer.fallDistance > 3F) {
                mc.thePlayer.motionY = flySpeedValue.get().toDouble()
            }
            "phase" -> {
                if (mc.thePlayer.fallDistance > 3 + phaseOffsetValue.get()) {
                    val fallPos = NewFall(mc.thePlayer)
                            .findCollision(5) ?: return
                    if (fallPos.y - mc.thePlayer.motionY / 20.0 < mc.thePlayer.posY) {
                        mc.timer.timerSpeed = 0.05f
                        Timer().schedule(100L) {
                            try {
                                mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(fallPos.x.toDouble(), fallPos.y.toDouble(), fallPos.z.toDouble(), true))
                            } catch (e: Exception) {
                                // ignore, may leave unexpectedly.
                            }
                            mc.timer.timerSpeed = 1f
                        }
                    }
                }
            }
            "edit" -> if (editMode.get().equals("smart", true)) {
                if (mc.thePlayer.fallDistance.toInt() / 3 > lastFallDistRounded) {
                    lastFallDistRounded = mc.thePlayer.fallDistance.toInt() / 3
                    packetModify = true
                }
                if (mc.thePlayer.onGround)
                    lastFallDistRounded = 0
            }
            "hypixel" -> if (hypixelMode.get().equals("packet", true)) {
                val offset = 2.5
                if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance - matrixFallTicks * offset >= 0.0) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                    matrixFallTicks++
                } else if (mc.thePlayer.onGround)
                    matrixFallTicks = 1
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (!LiquidBounce.moduleManager[Fly::class.java]!!.state && voidCheckValue.get() && !MovementUtils.isBlockUnder()) return

        if (typeValue.get().equals("aac", true) && aacMode.get().equals("4.x", true) && event.eventState == EventState.PRE) {
            if (!inVoid()) {
                if (aac4Fakelag) {
                    aac4Fakelag = false
                    if (aac4Packets.size > 0) {
                        for (packet in aac4Packets) {
                            mc.thePlayer.sendQueue.addToSendQueue(packet)
                        }
                        aac4Packets.clear()
                    }
                }
                return
            }
            if (mc.thePlayer.onGround && aac4Fakelag) {
                aac4Fakelag = false
                if (aac4Packets.size > 0) {
                    for (packet in aac4Packets) {
                        mc.thePlayer.sendQueue.addToSendQueue(packet)
                    }
                    aac4Packets.clear()
                }
                return
            }
            if (mc.thePlayer.fallDistance > 2.5 && aac4Fakelag) {
                packetModify = true
                mc.thePlayer.fallDistance = 0f
            }
            if (inAir(4.0, 1.0)) {
                return
            }
            if (!aac4Fakelag)
                aac4Fakelag = true
        } else if (typeValue.get().equals("mlg", true)) {
            if (event.eventState == EventState.PRE) {
                currentMlgRotation = null
                mlgTimer.update()

                if (!mlgTimer.hasTimePassed(10))
                    return

                if (mc.thePlayer.fallDistance > minFallDistanceValue.get()) {
                    val NewFallingPlayer = NewFall(mc.thePlayer)
                    val maxDist = mc.playerController.blockReachDistance + 1.5
                    val collision = NewFallingPlayer.findCollision(ceil(1.0 / mc.thePlayer.motionY * -maxDist).toInt()) ?: return
                    var ok = Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.eyeHeight, mc.thePlayer.posZ).distanceTo(Vec3(collision).addVector(0.5, 0.5, 0.5)) < mc.playerController.blockReachDistance + sqrt(0.75)

                    if (mc.thePlayer.motionY < collision.y + 1 - mc.thePlayer.posY)
                        ok = true

                    if (!ok)
                        return

                    var index = -1

                    for (i in 36..44) {
                        val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack

                        if (itemStack != null && (itemStack.item == Items.water_bucket || itemStack.item is ItemBlock && (itemStack.item as ItemBlock).block == Blocks.web)) {
                            index = i - 36

                            if (mc.thePlayer.inventory.currentItem == index)
                                break
                        }
                    }

                    if (index == -1)
                        return

                    currentMlgItemIndex = index
                    currentMlgBlock = collision

                    if (mc.thePlayer.inventory.currentItem != index) {
                        mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(index))
                    }

                    currentMlgRotation = RotationUtils.faceBlock(collision)
                    currentMlgRotation!!.rotation.toPlayer(mc.thePlayer)
                }
            } else if (currentMlgRotation != null) {
                val stack = mc.thePlayer.inventory.mainInventory[currentMlgItemIndex]

                if (stack.item is ItemBucket)
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, stack)
                else if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, currentMlgBlock, EnumFacing.UP, Vec3(0.0, 0.5, 0.0).add(Vec3(currentMlgBlock ?: return))))
                    mlgTimer.reset()

                if (mc.thePlayer.inventory.currentItem != currentMlgItemIndex)
                    mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mc.thePlayer ?: return

        if (!LiquidBounce.moduleManager[Fly::class.java]!!.state && voidCheckValue.get() && !MovementUtils.isBlockUnder()) return

        val packet = event.packet
        if (packet is S12PacketEntityVelocity && typeValue.get().equals("aac", true) && aacMode.get().equals("4.4.x", true) && mc.thePlayer.fallDistance > 1.8)
            packet.motionY = (packet.motionY * -0.1).toInt()

        if (packet is S08PacketPlayerPosLook) {
            if (typeValue.get().equals("aac", true) && aacMode.get().equals("loyisa4.4.2", true)) {
                aac4FlagCount++
                if(matrixFlagWait > 0) {
                    aac4FlagCooldown.reset()
                    aac4FlagCount = 1
                    event.cancelEvent()
                }
            }
            if (typeValue.get().equals("matrix", true) && matrixMode.get().equals("old", true) && matrixFlagWait > 0) {
                matrixFlagWait = 0
                mc.timer.timerSpeed = 1.0F
                event.cancelEvent()
            }
        }

        if (packet is C03PacketPlayer) {
            if (matrixSend) {
                matrixSend = false
                PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y, packet.z, true))
            }

            if (typeValue.get().equals("edit", true)) {
                val edits = editMode.get()

                if (edits.equals("always", true)
                        || (edits.equals("default", true) && mc.thePlayer.fallDistance > 2.5F)
                        || (edits.equals("damage", true) && mc.thePlayer.fallDistance > 3.5F)
                        || (edits.equals("smart", true) && packetModify)) {
                    packet.onGround = true
                    packetModify = false
                }

                if (edits.equals("noground", true))
                    packet.onGround = false
            }

            if (typeValue.get().equals("medusa", true) && mc.thePlayer.fallDistance > 2.3F) {
                event.cancelEvent()
                PacketUtils.sendPacketNoEvent(C03PacketPlayer(true))
                mc.thePlayer.fallDistance = 0F
            }

            if (typeValue.get().equals("hypixel", true)) {
                when (hypixelMode.get().toLowerCase()) {
                    "default" -> if (mc.thePlayer.fallDistance > 1.5) {
                        packet.onGround = mc.thePlayer.ticksExisted % 2 == 0
                    }
                    "new" -> if (mc.thePlayer.fallDistance > 2.5F && mc.thePlayer.ticksExisted % 2 == 0) {
                        packet.onGround = true
                        packet.setMoving(false)
                    }
                }
            }

            if (typeValue.get().equals("verus", true) && needSpoof) {
                packet.onGround = true
                needSpoof = false
            }

            if (typeValue.get().equals("aac", true)) {
                when (aacMode.get().toLowerCase()) {
                    "4.x" -> if (aac4Fakelag) {
                        event.cancelEvent()
                        if (packetModify) {
                            packet.onGround = true
                            packetModify = false
                        }
                        aac4Packets.add(packet)
                    }
                    "4.4.x" -> if (mc.thePlayer.fallDistance > 1.6) {
                        packet.onGround = true
                    }
                    "5.0.4" -> if (isDmgFalling) {
                        if (packet.onGround && mc.thePlayer.onGround) {
                            isDmgFalling = false
                            packet.onGround = true
                            mc.thePlayer.onGround = false
                            packet.y += 1.0
                            mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y - 1.0784, packet.z, false))
                            mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y - 0.5, packet.z, true))
                        }
                    }
                }
            }

            if (typeValue.get().equals("matrix", true) && matrixMode.get().equals("6.2.x", true) && matrixCanSpoof) {
                packet.onGround = true
                matrixCanSpoof = false
            }

            if (isDmgFalling && ((typeValue.get().equals("matrix", true) && matrixMode.get().equals("old", true))
                            || (typeValue.get().equals("aac", true) && aacMode.get().equals("loyisa4.4.2", true)))) {
                if (packet.onGround && mc.thePlayer.onGround) {
                    matrixFlagWait = 2
                    isDmgFalling = false
                    event.cancelEvent()
                    mc.thePlayer.onGround = false
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(packet.x, packet.y - 256, packet.z, false))
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(packet.x, (-10).toDouble() , packet.z, true))
                    mc.timer.timerSpeed = 0.18f
                    modifiedTimer = true
                }
            }
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!LiquidBounce.moduleManager[Fly::class.java]!!.state && voidCheckValue.get() && !MovementUtils.isBlockUnder()) return

        if (BlockUtils.collideBlock(mc.thePlayer.entityBoundingBox) { it is BlockLiquid } || BlockUtils.collideBlock(AxisAlignedBB(mc.thePlayer.entityBoundingBox.maxX, mc.thePlayer.entityBoundingBox.maxY, mc.thePlayer.entityBoundingBox.maxZ, mc.thePlayer.entityBoundingBox.minX, mc.thePlayer.entityBoundingBox.minY - 0.01, mc.thePlayer.entityBoundingBox.minZ)) { it is BlockLiquid })
            return

        if (typeValue.get().equals("aac", true) && aacMode.get().equals("laac", true)) {
            if (!jumped && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isInWeb && mc.thePlayer.motionY < 0.0) {
                event.x = 0.0
                event.z = 0.0
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
    }

    private fun inVoid(): Boolean {
        if (mc.thePlayer.posY < 0) return false
        var off = 0
        while (off < mc.thePlayer.posY + 2) {
            val bb = AxisAlignedBB(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    mc.thePlayer.posX,
                    off.toDouble(),
                    mc.thePlayer.posZ
            )
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true
            }
            off += 2
        }
        return false
    }

    private fun inAir(height: Double, plus: Double): Boolean {
        if (mc.thePlayer.posY < 0) return false
        var off = 0
        while (off < height) {
            val bb = AxisAlignedBB(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - off,
                    mc.thePlayer.posZ
            )
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty())
                return true

            off += plus.toInt()
        }
        return false
    }

    override val tag: String
        get() = typeValue.get()
}