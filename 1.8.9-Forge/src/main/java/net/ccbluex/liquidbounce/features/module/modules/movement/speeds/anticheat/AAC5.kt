package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class AAC5 : SpeedMode("AAC5") {
    override fun onUpdate() {
        if (!MovementUtils.isMoving())
            return;
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump()
            mc.thePlayer.speedInAir = 0.0201F
        }
        if (mc.thePlayer.fallDistance > 0.7 && mc.thePlayer.fallDistance < 1.3) {
            mc.thePlayer.speedInAir = 0.02F
            mc.timer.timerSpeed = 1.8F
        }
    }

    override fun onDisable() {
        mc.thePlayer!!.speedInAir = 0.02f
        mc.timer.timerSpeed = 1f
    }
}