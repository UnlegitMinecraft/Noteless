package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.timer.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;

public class AntiCheatB extends SpeedMode {
    private int stage;
    public Minecraft mc = Minecraft.getMinecraft();

    private int hops;
    private double moveSpeed;
    private double lastDist;

    public AntiCheatB() {
        super("AntiCheatB");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lastDist = 0.0D;
        this.hops = 1;
        this.stage = 0;
        this.moveSpeed = MovementUtils.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.0D : 1.34D);

    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;

        super.onDisable();
        mc.thePlayer.motionZ = 0.0;
        mc.thePlayer.motionX = 0.0;


    }

    @Override
    public void onMove(final MoveEvent event) {
        double motionY, lastDist, n, lastDist2;
        EntityPlayerSP thePlayer;
        switch (this.stage) {
            case 0:
                this.stage++;
                this.lastDist = 0.0D;
                break;
            case 2:
                this.lastDist = 0.0D;
                motionY = 0.41199998688697814D;
                if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
                    if (mc.thePlayer.isPotionActive(Potion.jump))
                        motionY += ((mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
                    event.setY(mc.thePlayer.motionY = motionY);
                    this.hops++;
                    this.moveSpeed *= 1.9D;
                }
                break;
            case 3:
                lastDist = this.lastDist;
                n = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? (mc.thePlayer.isPotionActive(Potion.jump) ? 0.54D : 0.655D) : 0.7025D;
                lastDist2 = this.lastDist;
                this.moveSpeed = lastDist - n * (lastDist2 - MovementUtils.getBaseMoveSpeed());
                break;
            default:
                if ((mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, mc.thePlayer.motionY, 0.0D)).size() > 0 || mc.thePlayer.isCollidedVertically) && this.stage > 0)
                    this.stage = MovementUtils.isMoving() ? 1 : 0;
                this.moveSpeed = this.lastDist - this.lastDist / 90D;
                break;
        }
        MovementUtils.setMotion(event, this.moveSpeed = Math.max(this.moveSpeed, MovementUtils.defaultSpeed() + 0.011219999998D));
        this.stage++;


    }


}
