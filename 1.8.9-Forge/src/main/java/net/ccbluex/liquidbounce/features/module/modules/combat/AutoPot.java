package net.ccbluex.liquidbounce.features.module.modules.combat;


import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PacketUtils;
import net.ccbluex.liquidbounce.utils.Rotation;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.ThreadLocalRandom;
@ModuleInfo(name = "AutoPot", description = "Pot for u.", category = ModuleCategory.COMBAT)
public class AutoPot extends Module {

    private final FloatValue delay = new FloatValue("Delay", 50f, 0f, 2000f);
    private final FloatValue minHealHP = new FloatValue("minHP", 12f, 1f, 20f);
    private final BoolValue splashFrogPots = new BoolValue("Frog potions", true);
    private final TimerUtil timerUtil = new TimerUtil();
    public static boolean isPotting;
    private float prevPitch;
    public static final BlockPos  NEGATIVE = new BlockPos(-1, -1, -1);


    @EventTarget
    public void onMotion(MotionEvent e) {
        int prevSlot = mc.thePlayer.inventory.currentItem;
        if (e.isPre()) {
            if (MovementUtils.isOnGround(1.0E-5)
                    && (!mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    || mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getDuration() < 30)
                    && timerUtil.hasTimeElapsed(delay.get().longValue())) {
                if (isSpeedPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isSpeedPot(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw,90));
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveSpeedPots();
                }
            }

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && mc.thePlayer.getHealth() <= minHealHP.get()
                    && timerUtil.hasTimeElapsed(delay.get().longValue())) {
                if (isRegenPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isRegenPot(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw,90));
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveRegenPots();
                }
            }

            if (mc.thePlayer.getHealth() <= minHealHP.get() && timerUtil.hasTimeElapsed(delay.get().longValue())) {
                if (isHealthPotsInHotbar()) {
                    for (int i = 36; i < 45; i++) {
                        if (isHealthPot(mc.thePlayer.inventoryContainer.getSlot(i).getStack())) {
                            isPotting = true;
                            prevPitch = mc.thePlayer.rotationPitch;
                            throwPot(prevSlot, i);
                            RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw,90));
                            break;
                        }
                    }
                    timerUtil.reset();
                    isPotting = false;
                } else {
                    moveHealthPots();
                }
            }
        } else if (!e.isPre()) {
            isPotting = false;
        }
    };

    private void throwPot(int prevSlot, int index) {
        double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
        float yaw = mc.thePlayer.rotationYaw;
        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                x, y, z, yaw, 88.8F + ThreadLocalRandom.current().nextFloat(), mc.thePlayer.onGround));
        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(index - 36));
        PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(
                NEGATIVE, 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
        PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));
        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(
                x, y, z, yaw, prevPitch, mc.thePlayer.onGround));
    }

    private boolean isSpeedPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isSpeedPot(mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private boolean isHealthPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isHealthPot(mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private boolean isRegenPotsInHotbar() {
        for (int index = 36; index < 45; index++) {
            if (isRegenPot(mc.thePlayer.inventoryContainer.getSlot(index).getStack())) return true;
        }
        return false;
    }

    private int getPotionCount() {
        int count = 0;
        for (int index = 0; index < 45; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isHealthPot(stack) || isHealthPot(stack) || isRegenPot(stack))
                count++;
        }
        return count;
    }

    private void moveSpeedPots() {
        if (mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null) continue;
            if (!splashFrogPots.get() && stack.getDisplayName().contains("Frog")) continue;
            if (isSpeedPot(stack)) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    private void moveHealthPots() {
        if (mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isHealthPot(stack)) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    private void moveRegenPots() {
        if (mc.currentScreen instanceof GuiChest) return;
        for (int index = 9; index < 36; index++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (isRegenPot(stack)) {
                mc.playerController.windowClick(0, index, 6, 2, mc.thePlayer);
                break;
            }
        }
    }

    private boolean isSpeedPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (!splashFrogPots.get() && stack.getDisplayName().contains("Frog")) return false;
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.moveSpeed.id && e.getPotionID() != Potion.jump.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isHealthPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.heal.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRegenPot(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect e : ((ItemPotion) stack.getItem()).getEffects(stack)) {
                    if (e.getPotionID() == Potion.regeneration.id) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
