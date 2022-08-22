package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PlayerUtil;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.utils.math.MathUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.timer.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class AntiCheatB extends SpeedMode {
    public Minecraft mc = Minecraft.getMinecraft();

    private double moveSpeed;
    private double lastDist;
    private boolean wasOnGround;

    private boolean wasInitialLowHop;
    public AntiCheatB() {
        super("LowHop");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        wasInitialLowHop = false;
        moveSpeed = 0.221;
        lastDist = 0.0;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        super.onDisable();
        mc.thePlayer.motionZ = 0.0;
        mc.thePlayer.motionX = 0.0;

    }
    private double lowHopYModification(final double baseMotionY,
                                       final double yDistFromGround) {
        if (yDistFromGround == LOW_HOP_Y_POSITIONS[0]) {
            return 0.31;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[1]) {
            return 0.04;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[2]) {
            return -0.2;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[3]) {
            return -0.14;
        } else if (yDistFromGround == LOW_HOP_Y_POSITIONS[4]) {
            return -0.2;
        }

        return baseMotionY;
    }
    private static final double[] LOW_HOP_Y_POSITIONS = {
            MathUtils.round(0.4, 0.001),
            MathUtils.round(0.71, 0.001),
            MathUtils.round(0.75, 0.001),
            MathUtils.round(0.55, 0.001),
            MathUtils.round(0.41, 0.001)
    };

    private boolean simJumpShouldDoLowHop(final double baseMoveSpeedRef) {
        // Calculate the direction moved in
        final float direction = RotationUtils.calculateYawFromSrcToDst(mc.thePlayer.rotationYaw,
                mc.thePlayer.lastReportedPosX, mc.thePlayer.lastReportedPosZ,
                mc.thePlayer.posX, mc.thePlayer.posZ);
        final Vec3 start = new Vec3(mc.thePlayer.posX,
                mc.thePlayer.posY + LOW_HOP_Y_POSITIONS[2],
                mc.thePlayer.posZ);
        // Cast a ray at waist height in the direction moved in for 10 blocks
        final MovingObjectPosition rayTrace = mc.theWorld.rayTraceBlocks(start,
                RotationUtils.getDstVec(start, direction, 0.0F, 8),
                false, true, true);
        // If did not hit anything just continue
        if (rayTrace == null) return true;
        if (rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return true;
        if (rayTrace.hitVec == null) return true;

        // Check if player can fit above
        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox();
        if (mc.theWorld.checkBlockCollision(
                bb.offset(bb.minX - rayTrace.hitVec.xCoord,
                        bb.minY - rayTrace.hitVec.yCoord,
                        bb.minZ - rayTrace.hitVec.zCoord)))
            return false;

        // Distance to the block hit
        final double dist = start.distanceTo(rayTrace.hitVec);
        final double normalJumpDist = 4.0;
        return dist > normalJumpDist;
    }
    @Override
    public void onMove(final MoveEvent event) {
        if(!MovementUtils.isMoving())return;
        final double baseMoveSpeed = MovementUtils.getBaseMoveSpeed(mc.thePlayer);
        boolean doInitialLowHop = !mc.gameSettings.keyBindJump.isKeyDown() &&
                !mc.thePlayer.isPotionActive(Potion.jump) && !mc.thePlayer.isCollidedHorizontally &&
                simJumpShouldDoLowHop(baseMoveSpeed);
        if (!mc.thePlayer.onGround && wasInitialLowHop && mc.thePlayer.fallDistance < 0.54)
            event.setY(mc.thePlayer.motionY = lowHopYModification(mc.thePlayer.motionY, MathUtils.round(mc.thePlayer.posY - (int) mc.thePlayer.posY, 0.001)));

        if (mc.thePlayer.onGround && !wasOnGround) {
            moveSpeed = baseMoveSpeed * 1.7;
            wasInitialLowHop = doInitialLowHop;
            event.setY(mc.thePlayer.motionY = doInitialLowHop ? 0.4F : PlayerUtil.getJumpHeight(mc.thePlayer));
            wasOnGround = true;
        } else if (wasOnGround) {
            wasOnGround = false;
            final double bunnySlope = 0.66 * (lastDist - baseMoveSpeed);
            moveSpeed = lastDist - bunnySlope;
        }
        moveSpeed = Math.max(moveSpeed, baseMoveSpeed);
        MovementUtils.setSpeed(event, moveSpeed);
    }


}
