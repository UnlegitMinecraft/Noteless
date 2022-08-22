/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.utils.render.Colors;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.timer.TickTimer;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

// TODO: convert to kotlin
@ModuleInfo(name = "Fly", description = "Allows you to fly in survival mode.", category = ModuleCategory.MOVEMENT, keyBind = Keyboard.KEY_F)
public class Fly extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{
            "Vanilla",
            "SmoothVanilla",

            // NCP
            "NCP",
            "OldNCP",

            // RedeSky
            "Collide",
            "Smooth",

            // Verus
            "Verus",
            "DoMCer",
            "AAC5.2.0",
            "AAC5.2.0-Fast",

            // CubeCraft
            "CubeCraft",

            // Hypixel
            "Hypixel",
            "BlocksMC",

            "BlockWalk", //bypass horizon
            "FakeGround"

    }, "Vanilla");

    private final FloatValue vanillaSpeedValue = new FloatValue("VanillaSpeed", 2F, 0F, 5F);
    private final BoolValue vanillaKickBypassValue = new BoolValue("VanillaKickBypass", false);
    private final FloatValue viewbobbingValue = new FloatValue("ViewBobbing", 60.0F, 0.0F, 100.0F);
    private final FloatValue ncpMotionValue = new FloatValue("NCPMotion", 0F, 0F, 1F);

    // AAC
    private final FloatValue speedValue = new FloatValue("BlockMCSpeed", 2f, 0f, 5f);
    private final BoolValue kickBypassValue = new BoolValue("BlockMCKickBypass", false);
    private final BoolValue keepAliveValue = new BoolValue("BlockMCKeepAlive", false);// old KeepAlive fly combined
    private final BoolValue noClipValue = new BoolValue("BlockMCNoClip", false);
    private final BoolValue spoofValue = new BoolValue("BlockMCSpoofGround", false);
    // Hypixel
    private final BoolValue hypixelBoost = new BoolValue("Hypixel-Boost", true);
    private final IntegerValue hypixelBoostDelay = new IntegerValue("Hypixel-BoostDelay", 1200, 0, 2000);
    private final FloatValue hypixelBoostTimer = new FloatValue("Hypixel-BoostTimer", 1F, 0F, 5F);
    private final FloatValue hypixelSpeed = new FloatValue("HypixelNew-Speed", 0.5F, 0.3F, 0.7F);

    // RedeSky collide
    private final FloatValue CollideSpeedValue = new FloatValue("collideSpeed", 15.5F, 0F, 30F);
    private final FloatValue BoostValue = new FloatValue("collideBoost", 0.3F, 0.0F, 1F);
    private final FloatValue MaxSpeedValue = new FloatValue("collideMaxSpeed", 20F, 7F, 30F);
    private final FloatValue CollideTimerValue = new FloatValue("collideTimer", 0.8F, 0.1F, 1F);
    private final FloatValue SmoothSpeedValue = new FloatValue("smoothSpeed", 0.9F, 0.05F, 1F);
    private final FloatValue SpeedChangeValue = new FloatValue("smoothChangeSpeed", 0.1F, -1F, 1F);
    private final FloatValue MotionValue = new FloatValue("smoothMotion", 0.2F, 0F, 0.5F);
    private final FloatValue SmoothTimerValue = new FloatValue("smoothTimer", 0.3F, 0.1F, 1F);
    private final FloatValue DropoffValue = new FloatValue("smoothDropoff", 1F, 0F, 5F);
    private final IntegerValue aac520Append = new IntegerValue("AAC5.2.0Append", 13, 5, 30);
    private final FloatValue aac520AppendTimer = new FloatValue("AAC5.2.0FastAppendTimer", 0.4f, 0.1f, 0.7f);
    private final FloatValue aac520MaxTimer = new FloatValue("AAC5.2.0FastMaxTimer", 1.2f, 1f, 3f);
    private final BoolValue Dropoff = new BoolValue("smoothDropoffA", true);

    private final BoolValue motionResetValue = new BoolValue("MotionReset", false);

    // Visuals
    private final ListValue markValue = new ListValue("Mark", new String[]{"Up", "Down", "Off"}, "Up");
    private final BoolValue fakeDamageValue = new BoolValue("FakeDamage", true);
    private final BoolValue speedDisplay = new BoolValue("speedDisplay", true);
    private double startY;
    private double launchY;
    private final MSTimer flyTimer = new MSTimer();

    private final MSTimer groundTimer = new MSTimer();

    private boolean noPacketModify;

    private double aacJump;
   int ticks;
    private int aac3delay;
    private int aac3glideDelay;
    private int aac4glideDelay;

    private boolean noFlag;

    private final MSTimer mineSecureVClipTimer = new MSTimer();

    private final TickTimer spartanTimer = new TickTimer();

    private long minesuchtTP;

    private final MSTimer mineplexTimer = new MSTimer();

    private boolean wasDead;

    private final TickTimer hypixelTimer = new TickTimer();
    private final MSTimer theTimer = new MSTimer();

    private int boostHypixelState = 1;
    private double moveSpeed, lastDistance;
    private boolean failedStart = false;
    public static final String HYPIXELF = "WATCHDOG";

    private final TickTimer cubecraftTeleportTickTimer = new TickTimer();

    private final TickTimer freeHypixelTimer = new TickTimer();

    private int aac5Status = 0;
    private double aac5LastPosX = 0;
    private int aac5Same = 0;
    private C03PacketPlayer.C06PacketPlayerPosLook aac5QueuedPacket = null;
    private int aac5SameReach = 5;

    private float launchYaw = 0;
    private float launchPitch = 0;
    private float packets = 0;
    private int flyTick;

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;
        launchY = mc.thePlayer.posY;
        launchYaw = mc.thePlayer.rotationYaw;
        launchPitch = mc.thePlayer.rotationPitch;
        if (mc.thePlayer.onGround && fakeDamageValue.get()) {
            PacketEvent event = new PacketEvent(new S19PacketEntityStatus(mc.thePlayer, (byte) 2));
            LiquidBounce.eventManager.callEvent(event);
            if (!event.isCancelled()) {
                mc.thePlayer.handleStatusUpdate((byte) 2);
            }
        }
        ticks = 0 ;
        flyTimer.reset();
        flyTick = 0;
        aac4glideDelay = 0;

        noPacketModify = true;

        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        final String mode = modeValue.get();

        switch (mode.toLowerCase()) {
            case "aac5.2.0":
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionY = 0;
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, 1.7976931348623157E+308, z, true));
                break;
            case "aac5.2.0-fast":
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, 1.7976931348623157E+308, z, true));
                aac5LastPosX = 0;
                aac5QueuedPacket = null;
                aac5Same = 0;
                aac5SameReach = 5;
                aac5Status = 0;
                break;
            case "ncp":
                if (!mc.thePlayer.onGround)
                    break;

                for (int i = 0; i < 65; ++i) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.049D, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                }
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1D, z, true));

                mc.thePlayer.motionX *= 0.1D;
                mc.thePlayer.motionZ *= 0.1D;
                mc.thePlayer.swingItem();
                break;
            case "oldncp":
                if (!mc.thePlayer.onGround)
                    break;

                for (int i = 0; i < 4; i++) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 1.01, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                }
                mc.thePlayer.jump();
                mc.thePlayer.swingItem();
                break;
            case "smooth": {
                mc.thePlayer.addVelocity(0, MotionValue.get(), 0);
                break;
            }
        }

        startY = mc.thePlayer.posY;
        aacJump = -3.8D;
        noPacketModify = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        wasDead = false;

        if (mc.thePlayer == null)
            return;

        noFlag = false;

        final String mode = modeValue.get();

        switch (mode.toLowerCase()) {
            case "collide": {
                mc.thePlayer.motionY = 0;
                break;
            }
            case "smooth": {
                mc.thePlayer.capabilities.isFlying = false;
                break;
            }
        }

        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.capabilities.setFlySpeed(0.05f);

        mc.timer.timerSpeed = 1F;
        mc.thePlayer.speedInAir = 0.02F;

        if (motionResetValue.get()) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionZ = 0;
        }
    }
    @EventTarget
    public void onBlockBB(final BlockBBEvent event) {
        if (event.getBlock() instanceof BlockAir && event.getY() <= launchY) {
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1.0, launchY, event.getZ() + 1.0));
        }
    }
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        final float vanillaSpeed = vanillaSpeedValue.get();

        switch (modeValue.get().toLowerCase()) {
            case "blockwalk": {
                if (Math.random() > 0.5) {
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(0, -1, 0),
                            0, mc.thePlayer.inventory.getCurrentItem(), 0, 0, 0));
                }
                break;
            }
            case "blocksmc": {
                if (keepAliveValue.get()) {
                    mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive());
                }
                if (noClipValue.get()) {
                    mc.thePlayer.noClip = true;
                }

                mc.thePlayer.capabilities.isFlying = false;

                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionY = 0.0;
                mc.thePlayer.motionZ = 0.0;
                if (mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY += speedValue.get() * 0.5;
                }
                if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY -= speedValue.get() * 0.5;
                }
                MovementUtils.strafe(speedValue.get());
                break;
            }
            case "hypixelnew": {
                mc.timer.timerSpeed = 0.7F;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionZ = 0;
                if (theTimer.hasTimePassed(1000)) {
                    // hclip LMFAO
                    double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                    double x = -Math.sin(yaw) * hypixelSpeed.get();
                    double z = Math.cos(yaw) * hypixelSpeed.get();
                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                    theTimer.reset();
                }
                break;
            }
            case "aac5.2.0":
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionY = 0.003;
                if (mc.thePlayer.onGround) {
                    ClientUtils.displayChatMessage("§8[§c§Ynd§6§Client§8] §f" + "JUMP INTO AIR AND TOGGLE THIS MODULE");
                    setState(false);
                }
                break;
            case "aac5.2.0-fast":
                if (mc.thePlayer.onGround) {
                    ClientUtils.displayChatMessage("§8[§c§Ynd§6§Client§8] §f" + "JUMP INTO AIR AND TOGGLE THIS MODULE");
                    setState(false);
                    break;
                }
                mc.gameSettings.keyBindForward.pressed = aac5Status != 1;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.rotationYaw = launchYaw;
                mc.thePlayer.rotationPitch = launchPitch;
                if (aac5Status == 1) {
                    if (aac5QueuedPacket != null) {
                        PacketUtils.sendPacketNoEvent(aac5QueuedPacket);
                        double dist = 0.13;
                        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                        double x = -Math.sin(yaw) * dist;
                        double z = Math.cos(yaw) * dist;
                        mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                false));
                    }
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                    aac5QueuedPacket = new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false);
                }
                break;
            case "vanilla":
                mc.thePlayer.capabilities.isFlying = false;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
                if (mc.gameSettings.keyBindJump.isKeyDown())
                    mc.thePlayer.motionY += vanillaSpeed;
                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY -= vanillaSpeed;
                MovementUtils.strafe(vanillaSpeed);

                handleVanillaKickBypass();
                break;
            case "verus":
                mc.gameSettings.keyBindJump.pressed = false;
                if (mc.thePlayer.onGround && MovementUtils.isMoving()) {
                    mc.thePlayer.jump();
                    MovementUtils.strafe(0.48F);
                } else MovementUtils.strafe();
                break;
            case "smoothvanilla":
                mc.thePlayer.capabilities.isFlying = true;
                mc.thePlayer.capabilities.setFlySpeed(vanillaSpeed * 0.05f);

                handleVanillaKickBypass();
                break;
            case "cubecraft":
                mc.timer.timerSpeed = 0.6F;

                cubecraftTeleportTickTimer.update();
                break;
            case "ncp":
                mc.thePlayer.motionY = -ncpMotionValue.get();

                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -0.5D;
                MovementUtils.strafe();
                break;
            case "oldncp":
                if (startY > mc.thePlayer.posY)
                    mc.thePlayer.motionY = -0.000000000000000000000000000000001D;

                if (mc.gameSettings.keyBindSneak.isKeyDown())
                    mc.thePlayer.motionY = -0.2D;

                if (mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.posY < (startY - 0.1D))
                    mc.thePlayer.motionY = 0.2D;
                MovementUtils.strafe();
                break;
            case "hypixel":
                final int boostDelay = hypixelBoostDelay.get();
                if (hypixelBoost.get() && !flyTimer.hasTimePassed(boostDelay)) {
                    mc.timer.timerSpeed = 1F + (hypixelBoostTimer.get() * ((float) flyTimer.hasTimeLeft(boostDelay) / (float) boostDelay));
                }

                hypixelTimer.update();

                if (hypixelTimer.hasTimePassed(2)) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
                    hypixelTimer.reset();
                }
                break;
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (speedDisplay.get()) {
            ScaledResolution sr = new ScaledResolution(mc);
            double xDiff = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * 2;
            double zDiff = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * 2;
            BigDecimal bg = new BigDecimal(MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff) * 10d);
            int speed = (int) (bg.intValue() * mc.timer.timerSpeed);
            String str = speed + "block/sec";
            Fonts.SFUI35.drawString(str, (sr.getScaledWidth() - Fonts.SFUI35.getStringWidth(str)) / 2, sr.getScaledHeight() / 2 - 20, Colors.WHITE.c);
        }
    }

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        final String mode = modeValue.get();
        final String mark = markValue.get();
        mc.thePlayer.cameraYaw = viewbobbingValue.get() / 1000.0F;
        if (mark.equalsIgnoreCase("Off") || mode.equalsIgnoreCase("Vanilla") || mode.equalsIgnoreCase("SmoothVanilla"))
            return;

        double y = mark.equalsIgnoreCase("Up") ? startY + 2D : startY;

        RenderUtils.drawPlatform(y, (mc.thePlayer.getEntityBoundingBox().maxY < (startY + 2D)) ? new Color(0, 255, 0, 90) : new Color(255, 0, 0, 90), 1);
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (noPacketModify)
            return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C03PacketPlayer) {
            final C03PacketPlayer packetPlayer = (C03PacketPlayer) packet;

            final String mode = modeValue.get();

            if (mode.equalsIgnoreCase("NCP") && mc.thePlayer.inventory.getCurrentItem() == null)
                packetPlayer.onGround = true;

            if (spoofValue.get()) {
                ((C03PacketPlayer) packet).onGround = true;
            }
            packets++;
            if (packets == 40 && kickBypassValue.get()) {
                handleVanillaKickBypass();
                packets = 0;
            }

            if (mode.equalsIgnoreCase("Hypixel"))
                packetPlayer.onGround = false;

            if (mode.contains("AAC5.2.0"))
                event.cancelEvent();
        }


        if (packet instanceof S08PacketPlayerPosLook) {
            final String mode = modeValue.get();
            if (mode.equalsIgnoreCase("AAC5.2.0")) {
                event.cancelEvent();
                S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) packet;
                mc.thePlayer.setPosition(s08.getX(), s08.getY(), s08.getZ());
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, s08.getYaw(), s08.getPitch(), false));
                double dist = 0.14;
                double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * dist), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * dist));
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        false));
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
            } else if (mode.equalsIgnoreCase("AAC5.2.0-Fast")) {
                event.cancelEvent();
                S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) packet;
                if (aac5Status == 0) {
                    mc.thePlayer.setPosition(s08.getX(), s08.getY(), s08.getZ());
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, s08.getYaw(), s08.getPitch(), false));
                    if (mc.thePlayer.posX == aac5LastPosX) {
                        aac5Same++;
                        if (aac5Same >= 5) {
                            aac5Status = 1;
                            mc.timer.timerSpeed = 0.1f;
                            aac5Same = 0;
                            return;
                        }
                    }
                    double dist = 0.12;
                    double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
                    mc.thePlayer.setPosition(mc.thePlayer.posX + (-Math.sin(yaw) * dist), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * dist));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false));
                    aac5LastPosX = mc.thePlayer.posX;
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 1.7976931348623157E+308, mc.thePlayer.posZ, true));
                } else {
                    if (mc.timer.timerSpeed <= aac520MaxTimer.get()) {
                        aac5Same++;
                        if (aac5Same >= aac5SameReach) {
                            aac5Same = 0;
                            aac5SameReach += aac520Append.get();
                            mc.timer.timerSpeed += aac520AppendTimer.get();
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        flyTick++;
        switch (modeValue.get().toLowerCase()) {
            case "collide":
                mc.timer.timerSpeed = CollideTimerValue.get();
                RotationUtils.reset();
                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    float speed = CollideSpeedValue.get() / 100F + flyTick * (BoostValue.get() / 100F);
                    float maxSpeed = MaxSpeedValue.get() / 100F;
                    if (speed > maxSpeed) {
                        speed = maxSpeed;
                    }
                    float f = mc.thePlayer.rotationYaw * 0.017453292F;
                    mc.thePlayer.motionX -= MathHelper.sin(f) * speed;
                    mc.thePlayer.motionZ += MathHelper.cos(f) * speed;
                    event.setX(mc.thePlayer.motionX);
                    event.setZ(mc.thePlayer.motionZ);
                }
                break;

            case "domcer":{
                if (ticks % 10 == 0 && mc.thePlayer.onGround) {
                    MovementUtils.strafe(1f);
                    event.setY(0.42);
                    ticks = 0;
                    mc.thePlayer.motionY = 0.0;
                    mc.timer.timerSpeed = 4f;
                } else {
                    if (mc.gameSettings.keyBindJump.isKeyDown() && ticks % 2 == 1) {
                        event.setY(0.5);
                        MovementUtils.strafe(0.48f);
                        launchY += 0.5;
                        mc.timer.timerSpeed = 1f;
                        return;
                    }
                    mc.timer.timerSpeed = 1f;
                    if (mc.thePlayer.onGround) {
                        MovementUtils.strafe(0.8f);
                    } else {
                        MovementUtils.strafe(0.72f);
                    }
                }
                ticks++;
                break;
            }

            case "smooth": {
                if (flyTick > 10 && (mc.thePlayer.isCollided || mc.thePlayer.onGround)) {
                    setState(false);
                    return;
                }
                float speed = SmoothSpeedValue.get() / 10F + flyTick * (SpeedChangeValue.get() / 1000F);
                mc.timer.timerSpeed = SmoothTimerValue.get();
                mc.thePlayer.capabilities.setFlySpeed(speed);
                mc.thePlayer.capabilities.isFlying = true;
                mc.thePlayer.setPosition(mc.thePlayer.posX
                        , mc.thePlayer.posY - (Dropoff.get() ? (DropoffValue.get() / 1000F) * flyTick : (DropoffValue.get() / 300F))
                        , mc.thePlayer.posZ);
                break;
            }
            case "cubecraft": {
                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);

                if (cubecraftTeleportTickTimer.hasTimePassed(2)) {
                    event.setX(-Math.sin(yaw) * 2.4D);
                    event.setZ(Math.cos(yaw) * 2.4D);

                    cubecraftTeleportTickTimer.reset();
                } else {
                    event.setX(-Math.sin(yaw) * 0.2D);
                    event.setZ(Math.cos(yaw) * 0.2D);
                }
                break;
            }
            case "watchdog":
                if (!MovementUtils.isMoving()) {
                    event.setX(0D);
                    event.setZ(0D);
                    break;
                }

                if (failedStart)
                    break;

                final double amplifier = 1 + (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.2 *
                        (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) : 0);
                final double baseSpeed = 0.36D * amplifier;

                switch (boostHypixelState) {
                    case 1:
                        moveSpeed = baseSpeed;
                        boostHypixelState = 2;
                        break;
                    case 2:
                        moveSpeed *= 0.2D;
                        boostHypixelState = 3;
                        break;
                    case 3:
                        moveSpeed = lastDistance - (mc.thePlayer.ticksExisted % 2 == 0 ? 0.2103D : 0.2123D) * (lastDistance - baseSpeed);

                        boostHypixelState = 4;
                        break;
                    default:
                        moveSpeed = lastDistance - lastDistance / 159.8D;
                        break;
                }

                moveSpeed = Math.max(moveSpeed, 0.15D);

                final double yaw = MovementUtils.getDirection();
                event.setX(-Math.sin(yaw) * moveSpeed);
                event.setZ(Math.cos(yaw) * moveSpeed);
                mc.thePlayer.motionX = event.getX();
                mc.thePlayer.motionZ = event.getZ();
                break;
            case "freehypixel":
                if (!freeHypixelTimer.hasTimePassed(10))
                    event.zero();
                break;

        }
    }

    @EventTarget
    public void onBB(final BlockBBEvent event) {
        if (mc.thePlayer == null) return;

        final String mode = modeValue.get();

        if (event.getBlock() instanceof BlockAir && (mode.equalsIgnoreCase("Hypixel") ||
                mode.equalsIgnoreCase("watchdog") || mode.equalsIgnoreCase("Rewinside") ||
                (mode.equalsIgnoreCase("Mineplex") && mc.thePlayer.inventory.getCurrentItem() == null)) && event.getY() < mc.thePlayer.posY)
            event.setBoundingBox(AxisAlignedBB.fromBounds(event.getX(), event.getY(), event.getZ(), event.getX() + 1, mc.thePlayer.posY, event.getZ() + 1));
    }

    @EventTarget
    public void onJump(final JumpEvent e) {
            e.cancelEvent();
    }


    @EventTarget
    public void onStep(final StepEvent e) {
        final String mode = modeValue.get();

        if (mode.equalsIgnoreCase("Hypixel") || mode.equalsIgnoreCase("watchdog") ||
                mode.equalsIgnoreCase("Rewinside") || (mode.equalsIgnoreCase("Mineplex") && mc.thePlayer.inventory.getCurrentItem() == null))
            e.setStepHeight(0F);
    }


    private void handleVanillaKickBypass() {
        if (!vanillaKickBypassValue.get() || !groundTimer.hasTimePassed(1000)) return;

        final double ground = MovementUtils.calculateGround();

        for (double posY = mc.thePlayer.posY; posY > ground; posY -= 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if (posY - 8D < ground) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true));


        for (double posY = ground; posY < mc.thePlayer.posY; posY += 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if (posY + 8D > mc.thePlayer.posY) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        groundTimer.reset();
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
