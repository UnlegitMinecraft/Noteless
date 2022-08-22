package net.ccbluex.liquidbounce.features.module.modules.movement;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
@ModuleInfo(name = "InvMove", description = ".", category = ModuleCategory.MOVEMENT)
public final class InvMove extends Module {
    private final TimerUtil delayTimer = new TimerUtil();
    public static final ListValue mode = new ListValue("Mode", new String[]{
            "Vanilla",
            "Spoof",
            "Delay",
    }, "Spoof");
    private static final List<KeyBinding> keys = Arrays.asList(
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    );

    public static void updateStates() {
        if (mc.currentScreen != null) {
            keys.forEach(k -> k.pressed = GameSettings.isKeyDown(k));
        }
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        switch (mode.get().toLowerCase()) {
            case "spoof":
            case "vanilla":
                if (event.isPre() && mc.currentScreen instanceof GuiContainer) {
                    updateStates();
                }
                break;
            case "delay":
                if (event.isPre() && mc.currentScreen instanceof GuiContainer) {
                    if (delayTimer.hasTimeElapsed(100)) {
                        updateStates();
                        delayTimer.reset();
                    }
                }
                break;
        }
    };

    @EventTarget
    public void onPacket(final PacketEvent event) {
        if (mode.get().equalsIgnoreCase("spoof")) {
            if (event.getPacket() instanceof S2DPacketOpenWindow) {
                event.cancelEvent();
            }
            if (event.getPacket() instanceof S2EPacketCloseWindow) {
                event.cancelEvent();
            }
        }
    };
    @Override
    public String getTag() {
        return mode.get();
    }

}
