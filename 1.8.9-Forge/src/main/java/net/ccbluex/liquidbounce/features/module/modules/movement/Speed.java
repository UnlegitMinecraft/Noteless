/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.anticheat.*;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other.*;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Speed", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private final SpeedMode[] speedModes = new SpeedMode[]{
            new NCPBHop(),
            new NCPFHop(),
            new NCPHop(),
           new Hypixel(),
            new AACHop350(),
            new AntiCheatB(),
            new Matrix(),
            new CustomSpeed(),
            new VerusHop(),
            new VulcanHop(),
            new RiseVulcan(),
    };
    public final ListValue modeValue = new ListValue("Mode", getModes(), "NCPBHop") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if (getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if (getState())
                onEnable();
        }
    };
    public final BoolValue lagback = new BoolValue("LagBackCheck", true);
    public final FloatValue customSpeedValue = new FloatValue("CustomSpeed", 1.6F, 0.2F, 2F);
    public final FloatValue customYValue = new FloatValue("CustomY", 0F, 0F, 4F);
    public final FloatValue customTimerValue = new FloatValue("CustomTimer", 1F, 0.1F, 2F);
    public final BoolValue customStrafeValue = new BoolValue("CustomStrafe", true);
    public final BoolValue resetXZValue = new BoolValue("CustomResetXZ", false);
    public final BoolValue resetYValue = new BoolValue("CustomResetY", false);
    public final FloatValue speedtimerValue = new FloatValue("SpeedTimer", 1F , 0.1F , 3F);
    public final FloatValue hypixelspeedValue = new FloatValue("hypixelspeed", 0.47F, 0.2F, 2F);
    public final FloatValue speedYValue = new FloatValue("speedY", 0.42F, 0.2F, 2F);
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mc.thePlayer.isSneaking())
            return;

        if (MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);

        final SpeedMode speedMode = getMode();

        if (speedMode != null)
            speedMode.onUpdate();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if (mc.thePlayer.isSneaking() || event.getEventState() != EventState.PRE)
            return;
        final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);


        if (MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);

        final SpeedMode speedMode = getMode();

        if (speedMode != null)
            speedMode.onMotion();
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.isSneaking())
            return;

        final SpeedMode speedMode = getMode();

        if (speedMode != null)
            speedMode.onMove(event);
    }

    @EventTarget
    public void onTick(final TickEvent event) {
        if (mc.thePlayer.isSneaking())
            return;

        final SpeedMode speedMode = getMode();

        if (speedMode != null)
            speedMode.onTick();
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = 1F;

        final SpeedMode speedMode = getMode();

        if (speedMode != null)
            speedMode.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null)
            return;
        mc.timer.timerSpeed = 1F;

        final SpeedMode speedMode = getMode();

        if (speedMode != null)
            speedMode.onDisable();
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && lagback.get()) {
            this.setState(false);
            LiquidBounce.hud.addNotification(new Notification("LagBackCheck", "Speed was disabled due to lagback", NotifyType.WARNING, 2000,1000));
        }
        if (modeValue.get().equals("RiseVulcan")) {
            if (event.getPacket() instanceof S08PacketPlayerPosLook && mc.thePlayer.ticksExisted > 20) {
                S08PacketPlayerPosLook s08 = ((S08PacketPlayerPosLook) event.getPacket());
                if (mc.thePlayer.getDistanceSq(s08.getX(), s08.getY(), s08.getZ()) < 25 * 4) {
                    event.cancelEvent();
                }
            }
        }
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }

    private SpeedMode getMode() {
        final String mode = modeValue.get();

        for (final SpeedMode speedMode : speedModes)
            if (speedMode.modeName.equalsIgnoreCase(mode))
                return speedMode;

        return null;
    }

    private String[] getModes() {
        final List<String> list = new ArrayList<>();
        for (final SpeedMode speedMode : speedModes)
            list.add(speedMode.modeName);
        return list.toArray(new String[0]);
    }
}
