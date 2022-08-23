package net.ccbluex.liquidbounce.features.module.modules.combat;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.InventoryUtils;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.timer.TimeHelper;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "AutoArmor", description = "AutoArmor.", category = ModuleCategory.COMBAT)
public class AutoArmor extends Module {
    public static boolean isDone = false;
    private final TimeHelper timer = new TimeHelper();
    private final TimeHelper glitchFixer = new TimeHelper();
    private final BoolValue openInv = new BoolValue("Sort In Inv", false);
    private final BoolValue noMove = new BoolValue("No Move", false);
    public final FloatValue delay = new FloatValue("Delay", 50F, 0F, 1000F);


    enum ArmorType {
        BOOTS,
        LEGGINGS,
        CHEST_PLATE,
        HELMET;
    }
    @EventTarget
    public void onTick(TickEvent event) {
        if (!(Boolean)this.noMove.get() || !MovementUtils.isMoving()) {
            if (this.openInv.get()) {
                if (!(mc.currentScreen instanceof GuiInventory)) {
                    return;
                }
            } else {
                if (mc.currentScreen != null) {
                    this.glitchFixer.reset();
                }

                if (!this.glitchFixer.isDelayComplete(300L)) {
                    return;
                }
            }

            if (this.timer.isDelayComplete(this.delay.get())) {
                if (!mc.thePlayer.capabilities.isCreativeMode && (mc.currentScreen == null || this.openInv.get())) {
                    ArmorType[] var3 = ArmorType.values();
                    int var4 = var3.length;

                    for(int var5 = 0; var5 < var4; ++var5) {
                        ArmorType armorType = var3[var5];
                        int slot;
                        if ((slot = this.findArmor(armorType, InventoryUtils.getArmorScore(mc.thePlayer.inventory.armorItemInSlot(armorType.ordinal())))) != -1) {
                            isDone = false;
                            if (mc.thePlayer.inventory.armorItemInSlot(armorType.ordinal()) != null) {
                                this.dropArmor(armorType.ordinal());
                                this.timer.reset();
                                return;
                            }

                            this.warmArmor(slot);
                            this.timer.reset();
                            return;
                        }

                        isDone = true;
                    }

                } else {
                    this.timer.reset();
                }
            }
        }
    }

    private int findArmor(ArmorType armorType, float minimum) {
        float best = 0.0F;
        int result = -1;

        for(int i = 0; i < mc.thePlayer.inventory.mainInventory.length; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (!(InventoryUtils.getArmorScore(itemStack) < 0.0F) && !(InventoryUtils.getArmorScore(itemStack) <= minimum) && !(InventoryUtils.getArmorScore(itemStack) < best) && this.isValid(armorType, itemStack)) {
                best = InventoryUtils.getArmorScore(itemStack);
                result = i;
            }
        }

        return result;
    }

    private boolean isValid(ArmorType type, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemArmor)) {
            return false;
        } else {
            ItemArmor armor = (ItemArmor)itemStack.getItem();
            if (type == ArmorType.HELMET && armor.armorType == 0) {
                return true;
            } else if (type == ArmorType.CHEST_PLATE && armor.armorType == 1) {
                return true;
            } else if (type == ArmorType.LEGGINGS && armor.armorType == 2) {
                return true;
            } else {
                return type == ArmorType.BOOTS && armor.armorType == 3;
            }
        }
    }

    private void warmArmor(int slot_In) {
        if (slot_In >= 0 && slot_In <= 8) {
            this.clickSlot(slot_In + 36, 0, true);
        } else {
            this.clickSlot(slot_In, 0, true);
        }

    }

    private void clickSlot(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    private void dropArmor(int armorSlot) {
        int slot = InventoryUtils.armorSlotToNormalSlot(armorSlot);
        if (!InventoryUtils.isFull()) {
            this.clickSlot(slot, 0, true);
        } else {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
        }

    }
}
