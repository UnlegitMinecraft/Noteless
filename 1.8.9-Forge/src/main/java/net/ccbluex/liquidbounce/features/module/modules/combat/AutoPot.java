package net.ccbluex.liquidbounce.features.module.modules.combat;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly;
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold;
import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;
@ModuleInfo(name = "AutoPot", description = "flux.", category = ModuleCategory.COMBAT)
public class AutoPot extends Module {
    private boolean jumping;
    private boolean rotated;
    public static FloatValue health = new FloatValue("Health", 13, 1, 20);
    public static FloatValue delay = new FloatValue("Delay", 500, 100, 1500);
    private final BoolValue jump = new BoolValue("Jump", false);
    private final BoolValue regen = new BoolValue("Regen Pot", true);
    private final BoolValue heal = new BoolValue("Heal Pot", true);
    private final BoolValue speed = new BoolValue("Speed Pot", true);
    private final BoolValue nofrog = new BoolValue("No Frog", true);



    public static TimerUtil timer = new TimerUtil();
    private TimerUtil cooldown = new TimerUtil();

    private int lastPottedSlot;

    @EventTarget
    private void onMove(final MoveEvent event) {
        if (this.jumping) {
          mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
            event.setX(0);
            event.setZ(0);

            if (cooldown.hasPassed(100) && mc.thePlayer.onGround) {
                this.jumping = false;
            }
        }
    }

    @EventTarget
    private void onPreUpdate(final UpdateEvent event) {
        if (MovementUtils.getBlockUnderPlayer(mc.thePlayer, 0.01) instanceof BlockGlass || !MovementUtils.isOnGround(0.01))  {
            timer.reset();
            return;
        }

        if (mc.thePlayer.openContainer != null) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                timer.reset();
                return;
            }
        }

        if (LiquidBounce.moduleManager.get(Scaffold.class).getState())
            return;

        if (LiquidBounce.combatManager.getTarget() != null) {
            rotated = false;
            timer.reset();
            return;
        }

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasPassed(delay.get())) {
            if (jump.get() && !BlockUtils.isInLiquid()) {
                RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw,-89.5f));

                this.jumping = true;
                if (this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.jump();
                    cooldown.reset();
                }
            } else {
                new Rotation(mc.thePlayer.rotationYaw,89.5f);
            }

            rotated = true;
        }
    }

    @EventTarget
    private void onPostUpdate(final UpdateEvent event) {
        if (!rotated)
            return;

        rotated = false;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasPassed(delay.get()) && mc.thePlayer.isCollidedVertically) {
            final int prevSlot = mc.thePlayer.inventory.currentItem;
            if (potSlot < 9) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot));
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(prevSlot));
                mc.thePlayer.inventory.currentItem = prevSlot;
                timer.reset();

                this.lastPottedSlot = potSlot;
            }
        }
    }

    // Auto Refill
    @EventTarget
    public void onTick(TickEvent event) {
        if (this.mc.currentScreen != null)
            return;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && potSlot > 8 && this.mc.thePlayer.ticksExisted % 4 == 0) {
            this.swap(potSlot, InventoryUtils.findEmptySlot(this.lastPottedSlot));
        }
    }

    private void swap(final int slot, final int hotbarNum) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
                this.mc.thePlayer);
    }

    private int getPotFromInventory() {
        // heals
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                ItemPotion pot = (ItemPotion) item;

                if (!ItemPotion.isSplash(is.getMetadata())) {
                    continue;
                }

                List<PotionEffect> effects = pot.getEffects(is);

                for (PotionEffect effect : effects) {
                    if (mc.thePlayer.getHealth() < health.get() && ((heal.get() && effect.getPotionID() == Potion.heal.id) || (regen.get() && effect.getPotionID() == Potion.regeneration.id && !hasEffect(Potion.regeneration.id))))
                        return i;
                }
            }
        }

        // others
        for (int i = 0; i < 36; ++i) {
            if (this.mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = this.mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                List<PotionEffect> effects = ((ItemPotion) item).getEffects(is);

                for (PotionEffect effect : effects) {
                    if (effect.getPotionID() == Potion.moveSpeed.id && speed.get()
                            && !hasEffect(Potion.moveSpeed.id))
                        if (!is.getDisplayName().contains("\247a") || !nofrog.get())
                            return i;
                }
            }
        }

        return -1;
    }

    private boolean hasEffect(int potionId) {
        for (PotionEffect item : mc.thePlayer.getActivePotionEffects()) {
            if (item.getPotionID() == potionId)
                return true;
        }
        return false;
    }
}
