/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "NoWeb", description = "Prevents you from getting slowed down in webs.", category = ModuleCategory.MOVEMENT)
class NoWeb : Module() {

    private val modeValue = ListValue("Mode", arrayOf("None", "AAC", "LAAC", "Rewi", "AAC4", "Cardinal", "Horizon", "Spartan", "Negativity"), "None")
    private val horizonSpeed = FloatValue("HorizonSpeed", 0.1f, 0.01f, 0.8f)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "none" -> mc.thePlayer.isInWeb = false
            "aac" -> {
                mc.thePlayer.jumpMovementFactor = 0.59f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0
            }
            "laac" -> {
                mc.thePlayer.jumpMovementFactor = if (mc.thePlayer.movementInput.moveStrafe != 0f) 1.0f else 1.21f

                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }
            "rewi" -> {
                mc.thePlayer.jumpMovementFactor = 0.42f

                if (mc.thePlayer.onGround)
                    mc.thePlayer.jump()
            }
            "spartan" -> {
                MovementUtils.strafe(0.27f)
                mc.timer.timerSpeed = 3.7f
                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    mc.thePlayer.motionY = 0.0
                }
                if (mc.gameSettings.keyBindSneak.keyCode % 2 == 0) {
                    mc.timer.timerSpeed = 1.7f
                }
                if (mc.gameSettings.keyBindSneak.keyCode % 40 == 0) {
                    mc.timer.timerSpeed = 3.0f
                }
            }
            "aac4" ->{
                mc.timer.timerSpeed = 0.99F
                mc.thePlayer.jumpMovementFactor = 0.02958f
                mc.thePlayer.motionY -= 0.00775
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    mc.thePlayer.motionY = 0.4050
                    mc.timer.timerSpeed = 1.35F
                }
            }
            "cardinal" -> {
                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    MovementUtils.strafe(0.262f)
                } else {
                    MovementUtils.strafe(0.366f)
                }
            }
            "horizon" -> {
                if (!mc.gameSettings.keyBindSneak.isKeyDown) {
                    MovementUtils.strafe(this.horizonSpeed.get())
                }
            }
            "negativity" -> {
                mc.thePlayer.jumpMovementFactor = 0.4f
                if (mc.gameSettings.keyBindSneak.keyCode % 2 === 0) {
                    mc.thePlayer.jumpMovementFactor = 0.53f
                }
                if (!mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY = 0.0
            }
        }
    }
    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }
    override val tag: String?
        get() = modeValue.get()
}
