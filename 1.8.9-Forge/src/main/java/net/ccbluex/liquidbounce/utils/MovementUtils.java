/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils;

import antiskidderobfuscator.NativeMethod;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.math.BigDecimal;

public final class MovementUtils extends MinecraftInstance {

    public static float getSpeed() {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }
    public static void setSpeed(MoveEvent moveEvent, double moveSpeed) {
        setSpeed(moveEvent, moveSpeed, mc.thePlayer.rotationYaw, (double)mc.thePlayer.movementInput.moveStrafe, (double)mc.thePlayer.movementInput.moveForward);
    }
    public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
        return mc.theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
    }
    public static boolean isBlockUnder() {
        if (mc.thePlayer == null) return false;

        if (mc.thePlayer.posY < 0.0) {
            return false;
        }
        for (int off = 0; off < (int)mc.thePlayer.posY + 2; off += 2) {
            final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0.0, (double)(-off), 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
    @NativeMethod
    public static float getDirectionf() {
        float yaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward != 0.0f && mc.thePlayer.moveStrafing == 0.0f) {
            yaw += mc.thePlayer.moveForward > 0.0f? 0.0f: 180.0f;
        } else if (mc.thePlayer.moveForward != 0.0f) {
            if (mc.thePlayer.moveForward > 0.0f) {
                yaw += mc.thePlayer.moveStrafing > 0.0f? -45.0f: 45.0f;
            } else {
                yaw -= mc.thePlayer.moveStrafing > 0.0f? -45.0f: 45.0f;
            }
            yaw += mc.thePlayer.moveForward > 0.0f? 0.0f: 180.0f;
        } else if (mc.thePlayer.moveStrafing != 0.0f) {
            yaw += mc.thePlayer.moveStrafing > 0.0f? -90.0f: 90.0f;
        }

        return yaw;
    }
    @NativeMethod
    public static double getDirectiond() {
        double yaw = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward < 0.0F)
            yaw += 180.0;

        float forward = 1F;

        if (mc.thePlayer.moveForward < 0F) {
            forward = -0.5F;
        } else if(mc.thePlayer.moveForward > 0F) {
            forward = 0.5F;
        }

        if(mc.thePlayer.moveStrafing > 0F)
            yaw -= 90F * forward;

        if(mc.thePlayer.moveStrafing < 0F)
            yaw += 90F * forward;

        return Math.toRadians(yaw);
    }
    public static float getScaffoldRotation(float yaw, float strafe) {
        float rotationYaw = yaw;

        rotationYaw += 180F;

        float forward = -0.5F;

        if(strafe < 0F)
            rotationYaw -= 90F * forward;

        if(strafe > 0F)
            rotationYaw += 90F * forward;

        return rotationYaw;
    }
    public static void setMoveSpeed(double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                forward = forward > 0.0 ? 1.0 : -1.0;
            }
            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            mc.thePlayer.motionX = (forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
        }
    }
    public static void setSpeed(final MoveEvent moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward == 0.0 && strafe == 0.0) {
            moveEvent.setZ(0);
            moveEvent.setX(0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            if (strafe > 0.0D) {
                strafe = 1.0D;
            } else if (strafe < 0.0D) {
                strafe = -1.0D;
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));

            moveEvent.setX((forward * moveSpeed * cos + strafe * moveSpeed * sin));
            moveEvent.setZ((forward * moveSpeed * sin - strafe * moveSpeed * cos));
        }
    }
    public static void setPosition(MotionData posAndMotion) {
        mc.thePlayer.setPosition(posAndMotion.x, posAndMotion.y, posAndMotion.z);
        mc.thePlayer.motionX = posAndMotion.motionX;
        mc.thePlayer.motionY = posAndMotion.motionY;
        mc.thePlayer.motionZ = posAndMotion.motionZ;
    }
    public static double getBlockSpeed(final EntityLivingBase entityIn) {
        return BigDecimal.valueOf(Math.sqrt(Math.pow(entityIn.posX - entityIn.prevPosX, 2) + Math.pow(entityIn.posZ - entityIn.prevPosZ, 2)) * 20).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    public static boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }
    public static final double WALK_SPEED = 0.221;
    public static final double SNEAK_MOD = 0.3F;

    public static final double SPRINTING_MOD = 1.0 / 1.3F;
    private static final double SWIM_MOD = 0.115F / WALK_SPEED;
    private static final double[] DEPTH_STRIDER_VALUES = {
            1.0F,
            0.1645F / SWIM_MOD / WALK_SPEED,
            0.1995F / SWIM_MOD / WALK_SPEED,
            1.0F / SWIM_MOD,
    };
    public static double getBaseMoveSpeed(final EntityPlayerSP player) {
        double base = player.isSneaking() ? WALK_SPEED * SNEAK_MOD : mc.thePlayer.isSprinting() ? WALK_SPEED / SPRINTING_MOD : WALK_SPEED;

        final PotionEffect speed = player.getActivePotionEffect(Potion.moveSpeed);
        final int moveSpeedAmp = speed == null || speed.getDuration() < 3 ? 0 : speed.getAmplifier() + 1;

        if (moveSpeedAmp > 0)
            base *= 1.0 + 0.2 * moveSpeedAmp;

        if (player.isInWater()) {
            base *= SWIM_MOD;
            final int depthStriderLevel = EnchantmentHelper.getDepthStriderModifier(player);
            if (depthStriderLevel > 0) {
                base *= DEPTH_STRIDER_VALUES[depthStriderLevel];
            }

            return base * SWIM_MOD;
        } else if (player.isInLava()) {
            return base * SWIM_MOD;
        } else {
            return base;
        }
    }
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (MovementUtils.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }
    public static double defaultSpeed() {
        double baseSpeed = 0.2873;
        Minecraft.getMinecraft();
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            Minecraft.getMinecraft();
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return baseSpeed;
    }
    public static void setMotion(MoveEvent e, double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        if (mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if (mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        double yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -44 : 44);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 44 : -44);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            e.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            e.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    public static void setMotion(double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0F));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionX = forward * speed * cos
                    + strafe * speed * sin;
            mc.thePlayer.motionZ = forward * speed * sin
                    - strafe * speed * cos;
        }
    }

    public static boolean hasMotion() {
        return mc.thePlayer.motionX != 0D && mc.thePlayer.motionZ != 0D && mc.thePlayer.motionY != 0D;
    }
    private static double bps=0.0,lastX=0.0,lastY=0.0,lastZ=0.0;

    public static void updateBlocksPerSecond() {
        if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 1) {
            bps=0.0;
        }
        final double distance = mc.thePlayer.getDistance(lastX, lastY, lastZ);
        lastX=mc.thePlayer.posX;
        lastY=mc.thePlayer.posY;
        lastZ=mc.thePlayer.posZ;
        bps = distance * (20 * mc.timer.timerSpeed);
    }
    public void strafe(final double speed) {
        if (!isMoving()) return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
    }
    public static void strafe(final float speed) {
        if(!isMoving())
            return;

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * length));
    }
    // TODO: Make better and faster calculation lol
    public static double calculateGround() {
        final AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
        double blockHeight = 1D;

        for(double ground = mc.thePlayer.posY; ground > 0D; ground -= blockHeight) {
            final AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);

            if(mc.theWorld.checkBlockCollision(customBox)) {
                if(blockHeight <= 0.05D)
                    return ground + blockHeight;

                ground += blockHeight;
                blockHeight = 0.05D;
            }
        }

        return 0F;
    }
    public static double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if(mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if(mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
}