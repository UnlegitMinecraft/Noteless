package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.JumpEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import kotlin.math.*

@ModuleInfo(name = "Strafe", description = "Allows you to freely move in mid air.", category = ModuleCategory.MOVEMENT)
class Strafe : Module() {
    private var strengthValue = FloatValue("Strength", 0.67F, 0F, 1F)
    private val rotate = BoolValue("Rotate", true)
    private val onlySpeed = BoolValue("OnlySpeed", true)
    private var noMoveStopValue = BoolValue("NoMoveStop", false)
    private var onGroundValue = BoolValue("OnGround", true)

    private var wasDown: Boolean = false
    private var jump: Boolean = false

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (jump) {
            event.cancelEvent()
        }
    }

    override fun onEnable() {
        wasDown = false
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val thePlayer = mc.thePlayer ?: return
        val shotSpeed = sqrt((thePlayer.motionX * thePlayer.motionX) + (thePlayer.motionZ * thePlayer.motionZ))
        val speed = (shotSpeed * strengthValue.get())
        val motionX = (thePlayer.motionX * (1 - strengthValue.get()))
        val motionZ = (thePlayer.motionZ * (1 - strengthValue.get()))

        if (MovementUtils.isMoving() && rotate.get()) {
            RotationUtils.setTargetRotation(Rotation((MovementUtils.getDirectiond() * 180f / Math.PI).toFloat(), thePlayer.rotationPitch), 1)
        }

        if (onlySpeed.get() && !LiquidBounce.moduleManager.getModule(Speed::class.java)!!.state)
            return

        if (!(thePlayer.movementInput.moveForward != 0F || thePlayer.movementInput.moveStrafe != 0F)) {
            if (noMoveStopValue.get()) {
                thePlayer.motionX = 0.0
                thePlayer.motionZ = 0.0
            }
            return
        }

        if (!thePlayer.onGround || onGroundValue.get()) {
            val yaw = MovementUtils.getDirectionf()
            thePlayer.motionX = (-sin(Math.toRadians(yaw.toDouble())) * speed) + motionX
            thePlayer.motionZ = (cos(Math.toRadians(yaw.toDouble())) * speed) + motionZ
        }
    }
}