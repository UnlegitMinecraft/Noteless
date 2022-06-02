package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.Vec3
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@ModuleInfo(name = "SlientMove", description = "SlientMove.", category = ModuleCategory.PLAYER)
class SlientMove : Module() {

    private var attacp = false

    private var posx = 0.0
    private var posz = 0.0

    @EventTarget
    fun onUpdate(evnet: MoveEvent) {

        val ismove = mc.gameSettings.keyBindForward.isKeyDown || mc.gameSettings.keyBindBack.isKeyDown
                || mc.gameSettings.keyBindLeft.isKeyDown || mc.gameSettings.keyBindRight.isKeyDown

        attacp = mc.gameSettings.keyBindJump.isKeyDown && !ismove

        if(!attacp) {
            posx = mc.thePlayer.posX.toInt() + 0.5
            posz = mc.thePlayer.posZ.toInt() + 0.5
        }

        val enemyDist = mc.thePlayer!!.getDistance(posx , mc.thePlayer.posY , posz) + 0.1f
        val algorithm = max((enemyDist - 1.0f), enemyDist - (enemyDist - 1.0f / (1.0f * 2))).toFloat() * 4

        var strafe2 = 0f
        val speed = 0.25
        var forward = 1.0

        val yaw = RotationUtils.toRotation(Vec3(posx , mc.thePlayer.posY , posz) , false )

        RotationUtils.setTargetRotation(yaw)

        var strafe = 1.0

        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw.yaw += (if (forward > 0.0) -45 / enemyDist else 45 / enemyDist).toFloat()
                    strafe2 += (if (forward > 0.0) -45 / algorithm else 45 / algorithm).toFloat()
                } else if (strafe < 0.0) {
                    yaw.yaw += (if (forward > 0.0) 45 / enemyDist else -45 / enemyDist).toFloat()
                    strafe2 += (if (forward > 0.0) -45 / algorithm else 45 / algorithm).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }

            val mx = cos(Math.toRadians(yaw.yaw + 90 + strafe2.toDouble()))
            val mz = sin(Math.toRadians(yaw.yaw + 90 + strafe2.toDouble()))

            val x = forward * speed * -mz + strafe * speed * mx
            val z = forward * speed * mx - strafe * speed * -mz

            if(attacp) {
                mc.thePlayer.motionX =  x * 0.5
                mc.thePlayer.motionZ = z * 0.5
            }
        }
    }
}
