package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "TargetStrafe", description = "Targetstrafe.", category = ModuleCategory.MOVEMENT)
class TargetStrafe : Module() {

    private val GodmodValue = BoolValue("Godmod", false)
    private val radiusValue = FloatValue("Radius", 2.0F, 0.1F, 5.0F)
    private val modeValue = ListValue("KeyMode", arrayOf("Jump", "None"), "None")
    private val radiusMode = ListValue("radiusMode", arrayOf("TrueRadius", "Simple"), "Simple")
    private val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
    private val speed = LiquidBounce.moduleManager.getModule(Speed::class.java) as Speed
    private val fly = LiquidBounce.moduleManager.getModule(Fly::class.java) as Fly
    var consts = 0

    private var direction = 0

    @EventTarget
    fun movestrafe(event: MoveEvent) {
        if (mc.thePlayer.isCollidedHorizontally || checkVoid()) direction = if (direction == 1) -1 else 1
        if (mc.gameSettings.keyBindLeft.isKeyDown) {
            direction = 1
        }
        if (mc.gameSettings.keyBindRight.isKeyDown) {
            direction = -1
        }
        if (!isVoid(0, 0) && canStrafe) {
            val strafe = RotationUtils.getRotations(killAura.target)
            setSpeed(
                event,
                Math.sqrt(Math.pow(event.x, 2.0) + Math.pow(event.z, 2.0)),
                strafe[0],
                radiusValue.get(),
                1.0
            )
        }

        if (!GodmodValue.get())
            return
        mc.gameSettings.thirdPersonView = if (canStrafe) 2 else 0
    }

    val keyMode: Boolean
        get() = when (modeValue.get().toLowerCase()) {
            "jump" -> mc.gameSettings.keyBindJump.isKeyDown && mc.thePlayer.movementInput.moveStrafe == 0f
            "none" -> mc.thePlayer.movementInput.moveStrafe == 0f || mc.thePlayer.movementInput.moveForward == 0f
            else -> false
        }

    val canStrafe: Boolean
        get() = (killAura.state && (speed.state || fly.state) && killAura.target != null && !mc.thePlayer.isSneaking
                && keyMode)

    val cansize: Float
        get() = when {
            radiusMode.get().toLowerCase() == "simple" ->
                45f / mc.thePlayer!!.getDistance(killAura.target!!.posX, mc.thePlayer!!.posY, killAura.target!!.posZ)
                    .toFloat()
            else -> 45f
        }
    val Enemydistance: Double
        get() = mc.thePlayer!!.getDistance(killAura.target!!.posX, mc.thePlayer!!.posY, killAura.target!!.posZ)

    val algorithm: Float
        get() = Math.max(
            Enemydistance - radiusValue.get(),
            Enemydistance - (Enemydistance - radiusValue.get() / (radiusValue.get() * 2))
        ).toFloat()


    fun setSpeed(
        moveEvent: MoveEvent, moveSpeed: Double, pseudoYaw: Float, pseudoStrafe: Float,
        pseudoForward: Double
    ) {
        var yaw = pseudoYaw
        var forward = pseudoForward
        var strafe = pseudoStrafe
        var strafe1 = 0f
        var strafe2 = 0f

        check()

        when {
            modeValue.get().toLowerCase().equals("jump") ->
                strafe = pseudoStrafe * Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe * consts
            modeValue.get().toLowerCase().equals("none") ->
                strafe = consts.toFloat()
        }

        if (forward != 0.0) {
            if (strafe > 0.0) {
                if (radiusMode.get().toLowerCase() == "trueradius")
                    yaw += (if (forward > 0.0) -cansize else cansize)
                strafe2 += (if (forward > 0.0) -45 / algorithm else 45 / algorithm)
            } else if (strafe < 0.0) {
                if (radiusMode.get().toLowerCase() == "trueradius")
                    yaw += (if (forward > 0.0) cansize else -cansize)
                strafe2 += (if (forward > 0.0) 45 / algorithm else -45 / algorithm)
            }
            strafe = 0.0f
            if (forward > 0.0)
                forward = 1.0
            else if (forward < 0.0)
                forward = -1.0

        }
        if (strafe > 0.0)
            strafe = 1.0f
        else if (strafe < 0.0)
            strafe = -1.0f


        val mx = Math.cos(Math.toRadians(yaw + 90.0 + strafe2))
        val mz = Math.sin(Math.toRadians(yaw + 90.0 + strafe2))
        moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz
        moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx
    }

    private fun check() {
        if (mc.thePlayer!!.isCollidedHorizontally || checkVoid()) {
            if (consts < 2) consts += 1
            else {
                consts = -1
            }
        }
        when (consts) {
            0 -> {
                consts = 1
            }
            2 -> {
                consts = -1
            }
        }
    }

    private fun checkVoid(): Boolean {
        for (x in -1..0) {
            for (z in -1..0) {
                if (isVoid(x, z)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVoid(X: Int, Z: Int): Boolean {
        val fly = LiquidBounce.moduleManager.getModule(Fly::class.java) as Fly
        if (fly.state) {
            return false
        }
        if (mc.thePlayer!!.posY < 0.0) {
            return true
        }
        var off = 0
        while (off < mc.thePlayer!!.posY.toInt() + 2) {
            val bb: AxisAlignedBB =
                mc.thePlayer!!.entityBoundingBox.offset(X.toDouble(), (-off).toDouble(), Z.toDouble())
            if (mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer as Entity, bb).isEmpty()) {
                off += 2
                continue
            }
            return false
            off += 2
        }
        return true
    }

}