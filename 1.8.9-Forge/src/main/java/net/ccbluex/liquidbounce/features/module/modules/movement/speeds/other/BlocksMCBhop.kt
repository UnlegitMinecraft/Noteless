/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class BlocksMcBHop : SpeedMode("BlocksMcBHop") {

    override fun onMotion() {
        if (MovementUtils.isMoving()) {
            val speed = LiquidBounce.moduleManager.getModule(Speed::class.java) as Speed? ?: return
            mc.timer.timerSpeed = 1F
            if (mc.thePlayer.onGround) {
                MovementUtils.strafe(0.65F)
                mc.thePlayer.motionY = 0.2
            } else if (speed.customStrafeValue.get()) MovementUtils.strafe(0.65F) else MovementUtils.strafe()
        } else {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = mc.thePlayer.motionZ
        }

    }
    override fun onDisable() {
        MovementUtils.strafe()
        mc.timer.timerSpeed = 1f
    }
}
