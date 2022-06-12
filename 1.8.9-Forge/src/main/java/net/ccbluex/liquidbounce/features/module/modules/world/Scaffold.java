/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.block.PlaceInfo;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.GLUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Scaffold", description = "Automatically places blocks beneath your feet.", category = ModuleCategory.WORLD, keyBind = Keyboard.KEY_G)
public class Scaffold extends Module {

    /**
     * OPTIONS
     */

    // Mode
    public final ListValue modeValue = new ListValue("Mode", new String[]{"Normal", "Rewinside", "Expand"}, "Expand");

    // Delay
    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 0, 0, 1000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = minDelayValue.get();

            if (i > newValue)
                set(i);
        }
    };

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 0, 0, 1000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int i = maxDelayValue.get();

            if (i < newValue)
                set(i);
        }
    };

    private final BoolValue smart = new BoolValue("SmartPlaceableDelay", false);
    // AutoBlock
    private final ListValue autoBlockValue = new ListValue("AutoBlock", new String[]{"Off", "Matrix", "Spoof", "Switch"}, "Spoof");

    // Basic stuff
    public final BoolValue sprintValue = new BoolValue("Sprint", true);
    private final BoolValue swingValue = new BoolValue("Swing", true);
    private final BoolValue searchValue = new BoolValue("Search", true);
    private final BoolValue downValue = new BoolValue("Down", false);
    private final ListValue placeModeValue = new ListValue("PlaceTiming", new String[]{"Pre", "Post"}, "Post");
    private final BoolValue timerboost = new BoolValue("TimerBoost", false);
    private IntegerValue Fasttimer = new IntegerValue("FastTimer", 1200, 0, 3000);
    private IntegerValue slowtimer = new IntegerValue("SlowTimer", 1200, 0, 3000);
    private final FloatValue timerValue = new FloatValue("Timer", 1F, 0.1F, 10F);
    private final FloatValue timer2Value = new FloatValue("Timer2", 1F, 0.1F, 10F);

    // Eagle
    private final BoolValue eagleValue = new BoolValue("Eagle", false);
    private final BoolValue eagleSilentValue = new BoolValue("EagleSilent", false);
    private final IntegerValue blocksToEagleValue = new IntegerValue("BlocksToEagle", 0, 0, 10);

    // Expand
    private final IntegerValue expandLengthValue = new IntegerValue("ExpandLength", 1, 1, 6);

    // Rotations
    public final ListValue rotationsValue = new ListValue("Rotations", new String[]{"None", "Custom", "Vanilla", "AAC", "Hypixel"}, "AAC");
    private final IntegerValue customyaw = new IntegerValue("CustomYaw", 79, 60, 100);
    private final IntegerValue custompitch = new IntegerValue("CustomPitch", 79, 60, 100);
    private final BoolValue silentRotationValue = new BoolValue("Rotation", true);
    private final IntegerValue HypixelYawValue = new IntegerValue("HypixelYaw", 180, -360, 360);
    private final IntegerValue HypixelPitchValue = new IntegerValue("HypixelPitch", 79, 60, 100);
    private final IntegerValue aacPitchValue = new IntegerValue("AACPitch", 79, 60, 100);
    private final BoolValue aacSmartPitchValue = new BoolValue("AACSmartPitch", false);
    private final IntegerValue aacYawValue = new IntegerValue("AACYaw", 0, 0, 90);
    private final IntegerValue keepLengthValue = new IntegerValue("KeepRotationLength", 10, 0, 20);

    // Zitter
    private final BoolValue zitterValue = new BoolValue("Zitter", false);
    private final ListValue zitterModeValue = new ListValue("ZitterMode", new String[]{"Teleport", "Smooth"}, "Teleport");
    private final FloatValue zitterSpeed = new FloatValue("ZitterSpeed", 0.13F, 0.1F, 0.3F);
    private final FloatValue zitterStrength = new FloatValue("ZitterStrength", 0.072F, 0.05F, 0.2F);

    // Game
    private final FloatValue speedModifierValue = new FloatValue("SpeedModifier", 1F, 0, 2F);

    // Safety
    private final BoolValue sameYValue = new BoolValue("SameY", false);
    private final BoolValue smartSpeedValue = new BoolValue("SmartSpeed", false);
    private final BoolValue autoJumpValue = new BoolValue("AutoJump", false);
    private final BoolValue safeWalkValue = new BoolValue("SafeWalk", true);
    private final BoolValue airSafeValue = new BoolValue("AirSafe", false);

    // Visuals
    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 255, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private final IntegerValue alphaValue = new IntegerValue("A", 255, 0, 255);

    // Visuals
    public final ListValue counterDisplayValue = new ListValue("Counter", new String[]{"Off", "Simple", "Advanced", "Sigma", "Novoline"}, "Simple");
    private final BoolValue blurValue = new BoolValue("Blur-Advanced", false);
    private final FloatValue blurStrength = new FloatValue("Blur-Strength", 1F, 0F, 30F);
    private final BoolValue markValue = new BoolValue("Mark", false);
    private final BoolValue tower = new BoolValue("Tower", true);
    private final BoolValue debugValue = new BoolValue("Debug", true);


    /**
     * MODULE
     */
    //counter
    private ItemStack currentlyHolding;
    private float progress = 0;
    private long lastMS = 0L;
    // Target block
    private PlaceInfo targetPlace;

    //Rotation
    private boolean stage = false;

    private boolean rotation;

    // Launch position
    private int launchY;

    // Rotation lock
    private Rotation lockRotation;

    // Auto block slot
    private int slot;

    // Zitter Smooth
    private boolean zitterDirection;

    // Delay
    private final MSTimer timer = new MSTimer();
    private final MSTimer rotationTimer = new MSTimer();
    private final MSTimer delayTimer = new MSTimer();
    private final MSTimer zitterTimer = new MSTimer();
    private long delay;
    private int lastPlace = 0;

    // Eagle
    private int placedBlocksWithoutEagle = 0;
    private boolean eagleSneaking;

    // Down
    private boolean shouldGoDown = false;
    private int width;

    // Turn Speed
    private final FloatValue maxTurnSpeedValue = new FloatValue("MaxTurnSpeed", 180, 1, 180) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            float v = minTurnSpeedValue.get();
            if (v > newValue) set(v);
            if (getMaximum() < newValue) {
                set(getMaximum());
            } else if (getMinimum() > newValue) {
                set(getMinimum());
            }
        }
    };
    private final FloatValue minTurnSpeedValue = new FloatValue("MinTurnSpeed", 180, 1, 180) {
        @Override
        protected void onChanged(final Float oldValue, final Float newValue) {
            float v = maxTurnSpeedValue.get();
            if (v < newValue) set(v);
            if (getMaximum() < newValue) {
                set(getMaximum());
            } else if (getMinimum() > newValue) {
                set(getMinimum());
            }
        }
    };


    /**
     * Enable module
     */
    @Override
    public void onEnable() {
        lastPlace = 2;
        progress = 0;
        if (mc.thePlayer == null) return;

        launchY = (int) mc.thePlayer.posY;
        this.currentlyHolding = null;
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    public void onUpdate(final UpdateEvent event) {

        if (timerboost.get()) {

            if (stage) {
                mc.timer.timerSpeed = timer2Value.get();
                if (timer.hasTimePassed(slowtimer.get())) {
                    timer.reset();
                    stage = !stage;
                }
            } else {
                mc.timer.timerSpeed = timerValue.get();
                if (timer.hasTimePassed(Fasttimer.get())) {
                    timer.reset();
                    stage = !stage;
                }
            }

        }

        if (tower.get() && Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !LiquidBounce.moduleManager.getModule(Speed.class).getState()) {
            mc.thePlayer.onGround = false;
            LiquidBounce.moduleManager.getModule(Tower.class).setState(true);
            //System.out.println("1");
        } else {
            LiquidBounce.moduleManager.getModule(Tower.class).setState(false);
        }

        shouldGoDown = downValue.get() && !sameYValue.get() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && getBlocksAmount() > 1;
        if (shouldGoDown)
            mc.gameSettings.keyBindSneak.pressed = false;

        if (mc.thePlayer.onGround) {
            final String mode = modeValue.get();

            // Rewinside scaffold mode
            if (mode.equalsIgnoreCase("Rewinside")) {
                MovementUtils.strafe(0.2F);
                mc.thePlayer.motionY = 0D;
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("smooth")) {
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
                    mc.gameSettings.keyBindRight.pressed = false;

                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
                    mc.gameSettings.keyBindLeft.pressed = false;

                if (zitterTimer.hasTimePassed(100)) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }

                if (zitterDirection) {
                    mc.gameSettings.keyBindRight.pressed = true;
                    mc.gameSettings.keyBindLeft.pressed = false;
                } else {
                    mc.gameSettings.keyBindRight.pressed = false;
                    mc.gameSettings.keyBindLeft.pressed = true;
                }
            }

            // Eagle
            if (eagleValue.get() && !shouldGoDown) {
                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    final boolean shouldEagle = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX,
                            mc.thePlayer.posY - 1D, mc.thePlayer.posZ)).getBlock() == Blocks.air;

                    if (eagleSilentValue.get()) {
                        if (eagleSneaking != shouldEagle) {
                            mc.getNetHandler().addToSendQueue(
                                    new C0BPacketEntityAction(mc.thePlayer, shouldEagle ?
                                            C0BPacketEntityAction.Action.START_SNEAKING :
                                            C0BPacketEntityAction.Action.STOP_SNEAKING)
                            );
                        }

                        eagleSneaking = shouldEagle;
                    } else
                        mc.gameSettings.keyBindSneak.pressed = shouldEagle;

                    placedBlocksWithoutEagle = 0;
                } else
                    placedBlocksWithoutEagle++;
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equalsIgnoreCase("teleport")) {
                MovementUtils.strafe(zitterSpeed.get());

                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw + (zitterDirection ? 90D : -90D));
                mc.thePlayer.motionX -= Math.sin(yaw) * zitterStrength.get();
                mc.thePlayer.motionZ += Math.cos(yaw) * zitterStrength.get();
                zitterDirection = !zitterDirection;
            }
        }
        //Auto Jump thingy
        if (shouldGoDown) launchY = (int) mc.thePlayer.posY - 1;
        else if (!sameYValue.get()) {
            if ((!autoJumpValue.get() && !(smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState())) || GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.thePlayer.posY < launchY)
                launchY = (int) mc.thePlayer.posY;
            if (autoJumpValue.get() && !LiquidBounce.moduleManager.getModule(Speed.class).getState() && MovementUtils.isMoving() && mc.thePlayer.onGround && mc.thePlayer.jumpTicks == 0) {
                mc.thePlayer.jump();
                mc.thePlayer.jumpTicks = 10;
            }
        }
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mc.thePlayer == null)
            return;

        final Packet<?> packet = event.getPacket();

        // AutoBlock
        if (packet instanceof C09PacketHeldItemChange) {
            final C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange) packet;

            slot = packetHeldItemChange.getSlotId();
        }
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        final EventState eventState = event.getEventState();

        if (rotationTimer.hasTimePassed(3000L) && debugValue.get()) {
            ClientUtils.displayChatMessage("§8[§9§lDebug§8] RotationYaw:" + mc.thePlayer.rotationYaw + " | RotationPitch:" + mc.thePlayer.rotationPitch);
            rotationTimer.reset();
        }
        // Lock Rotation
        if (rotationsValue.get() != "None" && keepLengthValue.get() > 0 && lockRotation != null && silentRotationValue.get()) {
            Rotation limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, lockRotation, getSpeed());
            RotationUtils.setTargetRotation(limitedRotation, keepLengthValue.get());
        }

        // Place block
        if (placeModeValue.get().equalsIgnoreCase(eventState.getStateName()))
            place();

        // Update and search for new block
        if (eventState == EventState.PRE)
            update();
        if(eventState == EventState.PRE){
            // Speed 2 Slowdown
            if(mc.thePlayer.isPotionActive(Potion.moveSpeed.id)){
                mc.thePlayer.motionX *= 0.66;
                mc.thePlayer.motionZ *= 0.66;
            }
        }
        // Reset placeable delay
        if (targetPlace == null) {
            if (smart.get()) {
                if (lastPlace == 0) {
                    delayTimer.reset();
                }
            } else {
                delayTimer.reset();
            }
        }
    }

    private void update() {
        final boolean isHeldItemBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
        if (!autoBlockValue.get().equalsIgnoreCase("Off") ? InventoryUtils.findAutoBlockBlock() == -1 && !isHeldItemBlock : !isHeldItemBlock)
            return;
        findBlock(modeValue.get().equalsIgnoreCase("expand"));
    }

    /**
     * Search for new target block
     */
    private void findBlock(final boolean watchdog) {
        final BlockPos blockPosition = shouldGoDown ? (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.6D, mc.thePlayer.posZ)
                : new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ).down()) :
                ((sameYValue.get() || ((autoJumpValue.get() || (smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed.class).getState())) && !GameSettings.isKeyDown(mc.gameSettings.keyBindJump))) && launchY <= mc.thePlayer.posY ? (new BlockPos(mc.thePlayer.posX, launchY - 1, mc.thePlayer.posZ)) :
                        (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? new BlockPos(mc.thePlayer)
                                : new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()));

        if (!watchdog && (!BlockUtils.isReplaceable(blockPosition) || search(blockPosition, !shouldGoDown)))
            return;

        if (watchdog) {
            for (int i = 0; i < expandLengthValue.get(); i++) {
                if (search(blockPosition.add(
                        mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST ? i : 0,
                        0,
                        mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH ? i : 0
                ), false))

                    return;
            }
        } else if (searchValue.get()) {
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown))
                        return;
        }
    }

    /**
     * Place target block
     */
    private void place() {
        if (targetPlace == null) {
            if (lastPlace == 0 && smart.get()) delayTimer.reset();
            if (lastPlace > 0) lastPlace--;
            return;
        }
        if (!delayTimer.hasTimePassed(delay) || (sameYValue.get() && launchY - 1 != (int) targetPlace.getVec3().yCoord))
            return;
        int blockSlot = -1;
        ItemStack itemStack = mc.thePlayer.getHeldItem();
        if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) || mc.thePlayer.getHeldItem().stackSize <= 0) {
            if (autoBlockValue.get().equalsIgnoreCase("Off"))
                return;
            blockSlot = InventoryUtils.findAutoBlockBlock();
            if (blockSlot == -1)
                return;
            if (autoBlockValue.get().equalsIgnoreCase("Matrix")) {
                if (blockSlot - 36 != slot) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
                }
            }
            if (autoBlockValue.get().equalsIgnoreCase("Spoof")) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
            }
            if (autoBlockValue.get().equalsIgnoreCase("Switch")) {
                mc.thePlayer.inventory.currentItem = blockSlot - 36;
                mc.playerController.updateController();
            }
            itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
        }
        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, targetPlace.getBlockPos(), targetPlace.getEnumFacing(), targetPlace.getVec3());
            delayTimer.reset();
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());
            if (mc.thePlayer.onGround) {
                final float modifier = speedModifierValue.get();
                mc.thePlayer.motionX = (mc.thePlayer.motionX * modifier);
                mc.thePlayer.motionZ = (mc.thePlayer.motionZ * modifier);
            }
            lastPlace = 2;
            if (swingValue.get())
                mc.thePlayer.swingItem();
            else
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        // Reset
        this.targetPlace = null;
    }

    /**
     * Disable scaffold module
     */
    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        LiquidBounce.moduleManager.getModule(Tower.class).setState(false);
        if (mc.thePlayer == null) return;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false;

            if (eagleSneaking)
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight))
            mc.gameSettings.keyBindRight.pressed = false;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft))
            mc.gameSettings.keyBindLeft.pressed = false;

        lockRotation = null;
        shouldGoDown = false;

        if (slot != mc.thePlayer.inventory.currentItem)
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    /**
     * Entity movement event
     *
     * @param event
     * @param event
     */
    @EventTarget
    public void onMove(final MoveEvent event) {
        if (!safeWalkValue.get() || shouldGoDown)
            return;

        if (airSafeValue.get() || mc.thePlayer.onGround)
            event.setSafeWalk(true);
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        progress = (float) (System.currentTimeMillis() - lastMS) / 100F;
        if (progress >= 1) progress = 1;

        String counterMode = counterDisplayValue.get();
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final String info = getBlocksAmount() + " blocks";
        int infoWidth = Fonts.SFUI40.getStringWidth(info);
        int infoWidth2 = Fonts.minecraftFont.getStringWidth(getBlocksAmount() + "");
        if (counterMode.equalsIgnoreCase("simple")) {
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) - 1, scaledResolution.getScaledHeight() / 2 - 36, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2) + 1, scaledResolution.getScaledHeight() / 2 - 36, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 35, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 37, 0xFF000000, false);
            Fonts.minecraftFont.drawString(getBlocksAmount() + "", scaledResolution.getScaledWidth() / 2 - (infoWidth2 / 2), scaledResolution.getScaledHeight() / 2 - 36, -1, false);
        }
        if (counterMode.equalsIgnoreCase("advanced")) {
            boolean canRenderStack = (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock);
            if (blurValue.get())
                BlurUtils.blurArea(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 39, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - (canRenderStack ? 5 : 26), blurStrength.get());

            RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 40, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 39, (getBlocksAmount() > 1 ? 0xFFFFFFFF : 0xFFFF1010));
            RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 39, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 26, 0xA0000000);

            if (canRenderStack) {
                RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 8, scaledResolution.getScaledHeight() / 2 - 25, scaledResolution.getScaledWidth() / 2 - 8);
                renderItemStack(mc.thePlayer.inventory.mainInventory[slot], 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.SFUI40.drawCenteredString(info, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 - 36, -1);
        }

        if (counterMode.equalsIgnoreCase("sigma")) {
            GlStateManager.translate(0, -14F - (progress * 4F), 0);
            //GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glColor4f(0.15F, 0.15F, 0.15F, progress);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2 - 3, scaledResolution.getScaledHeight() - 60);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() - 57);
            GL11.glVertex2d(scaledResolution.getScaledWidth() / 2 + 3, scaledResolution.getScaledHeight() - 60);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            //GL11.glPopMatrix();
            RenderUtils.drawRoundedRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() - 60, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() - 74, 2F, new Color(0.15F, 0.15F, 0.15F, progress).getRGB());
            GlStateManager.resetColor();
            Fonts.SFUI35.drawCenteredString(info, scaledResolution.getScaledWidth() / 2 + 0.1F, scaledResolution.getScaledHeight() - 70, new Color(1F, 1F, 1F, 0.8F * progress).getRGB(), false);
            GlStateManager.translate(0, 14F + (progress * 4F), 0);
        }

        if (counterMode.equalsIgnoreCase("novoline")) {
            if (slot >= 0 && slot < 9 && mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() != null && mc.thePlayer.inventory.mainInventory[slot].getItem() instanceof ItemBlock) {
                //RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledResolution.getScaledWidth() / 2 - 22, scaledResolution.getScaledHeight() / 2 + 16, scaledResolution.getScaledWidth() / 2 - 22);
                renderItemStack(mc.thePlayer.inventory.mainInventory[slot], 0, 0);
                GlStateManager.popMatrix();
            }
            GlStateManager.resetColor();

            Fonts.minecraftFont.drawString(getBlocksAmount() + " blocks", scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 2 + 20, -1, true);
        }
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (!markValue.get())
            return;

        for (int i = 0; i < (modeValue.get().equalsIgnoreCase("Expand") ? expandLengthValue.get() + 1 : 2); i++) {
            final BlockPos blockPos = new BlockPos(mc.thePlayer.posX + (mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST ? i : 0), mc.thePlayer.posY - (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? 0D : 1.0D) - (shouldGoDown ? 1D : 0), mc.thePlayer.posZ + (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH ? i : 0));
            final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

            if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                RenderUtils.drawBlockBox(blockPos, new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), alphaValue.get()), true);
                break;
            }
        }
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private boolean search(final BlockPos blockPosition, final boolean checks) {
        if (!BlockUtils.isReplaceable(blockPosition))
            return false;

        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        PlaceRotation placeRotation = null;

        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtils.canBeClicked(neighbor))
                continue;

            final Vec3 dirVec = new Vec3(side.getDirectionVec());

            for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
                for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                    for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                        final Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18D || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)) {
                            zSearch += 0.1;
                            continue;
                        }

                        // face block
                        final double diffX = hitVec.xCoord - eyesPos.xCoord;
                        final double diffY = hitVec.yCoord - eyesPos.yCoord;
                        final double diffZ = hitVec.zCoord - eyesPos.zCoord;

                        final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                        final Rotation rotation = new Rotation(
                                MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F),
                                MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)))
                        );

                        final Vec3 rotationVector = RotationUtils.getVectorForRotation(rotation);
                        final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                        final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);

                        if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor))) {
                            zSearch += 0.1;
                            continue;
                        }
                        if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(placeRotation.getRotation()))
                            placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                        zSearch += 0.1;
                    }
                    ySearch += 0.1;
                }
                xSearch += 0.1;
            }
        }

        if (placeRotation == null) return false;
        if ((!rotationsValue.get().equals("None"))) {
            Rotation rotation = new Rotation(0, 0);

            switch (rotationsValue.get().toLowerCase()) {
                case "aac": {
                    rotation = new Rotation(mc.thePlayer.rotationYaw + ((mc.thePlayer.movementInput.moveForward > 0) ? 180 : 0) + aacYawValue.get(), aacSmartPitchValue.get() ? placeRotation.getRotation().getPitch() : aacPitchValue.get());
                    break;
                }
                case "vanilla": {
                    rotation = placeRotation.getRotation();
                    break;
                }
                case "custom": {
                    rotation = new Rotation(mc.thePlayer.rotationYaw + customyaw.get(), custompitch.get().floatValue());
                    break;
                }
                case "hypixel": {
                    rotation = new Rotation(mc.thePlayer.rotationYaw + ((mc.thePlayer.movementInput.moveForward > 0) ? 180 : 0) + HypixelYawValue.get(), HypixelPitchValue.get());
                }
            }
            if (rotation != null) {
                if (silentRotationValue.get()) {
                    Rotation limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation, getSpeed());
                    RotationUtils.setTargetRotation(limitedRotation, keepLengthValue.get());
                } else {
                    mc.thePlayer.rotationYaw = rotation.getYaw();
                    mc.thePlayer.rotationPitch = rotation.getPitch();
                }
            }
            lockRotation = rotation;
        }
        targetPlace = placeRotation.getPlaceInfo();
        return true;
    }

    private float getSpeed() {
        return (float) (Math.random() * (maxTurnSpeedValue.get() - minTurnSpeedValue.get()) + minTurnSpeedValue.get());
    }

    /**
     * @return hotbar blocks amount
     */
    private int getBlocksAmount() {
        int amount = 0;

        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) itemStack.getItem()).getBlock();
                if (mc.thePlayer.getHeldItem() == itemStack || !InventoryUtils.BLOCK_BLACKLIST.contains(block))
                    amount += itemStack.stackSize;
            }
        }

        return amount;
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}