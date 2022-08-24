/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;


import cn.WbxMain;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.features.special.AntiForge;
import net.ccbluex.liquidbounce.features.special.AutoReconnect;
import net.ccbluex.liquidbounce.utils.ServerUtils;
import net.ccbluex.liquidbounce.utils.login.LoginUtils;
import net.ccbluex.liquidbounce.utils.misc.RandomUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraftforge.fml.client.config.GuiSlider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

@Mixin(GuiDisconnected.class)
public abstract class MixinGuiDisconnected extends MixinGuiScreen {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0");

    @Shadow
    private int field_175353_i;

    private GuiButton reconnectButton;
    private GuiSlider autoReconnectDelaySlider;
    private GuiButton forgeBypassButton;
    private int reconnectTimer;
    public long sessionEndTime = 0;
    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        reconnectTimer = 0;
        buttonList.add(reconnectButton = new GuiButton(1, this.width / 2 - 100, this.height / 2 + field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 22, 98, 20, "Reconnect"));
        sessionEndTime = System.currentTimeMillis();
        this.drawReconnectDelaySlider();
        updateSliderText();
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        switch (button.id) {
            case 1:
                ServerUtils.connectToLastServer();
                break;
            case 4:
                LoginUtils.loginCracked(RandomUtils.randomString(RandomUtils.nextInt(5, 16)));
                ServerUtils.connectToLastServer();
                break;
            case 5:
                AntiForge.enabled = !AntiForge.enabled;
                forgeBypassButton.displayString = "Bypass AntiForge: " + (AntiForge.enabled ? "On" : "Off");
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.valuesConfig);
                break;
        }
    }

    @Override
    public void updateScreen() {
        if (AutoReconnect.INSTANCE.isEnabled()) {
            reconnectTimer++;
            if (reconnectTimer > AutoReconnect.INSTANCE.getDelay() / 50)
                ServerUtils.connectToLastServer();
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void drawScreen(CallbackInfo callbackInfo) {
        if (AutoReconnect.INSTANCE.isEnabled()) {
            this.updateReconnectButton();
        }

        Fonts.SF.SF_20.SF_20.drawCenteredString( "\u00a7oServer: " +  ServerUtils.serverData.serverIP, width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2 - 30, Color.LIGHT_GRAY.getRGB());
    }

    private void drawReconnectDelaySlider() {
        buttonList.add(autoReconnectDelaySlider =
                new GuiSlider(2, this.width / 2 + 2, this.height / 2 + field_175353_i / 2
                        + this.fontRendererObj.FONT_HEIGHT + 22, 98, 20, "AutoReconnect: ",
                        "ms", AutoReconnect.MIN, AutoReconnect.MAX, AutoReconnect.INSTANCE.getDelay(), false, true,
                        guiSlider -> {
                            AutoReconnect.INSTANCE.setDelay(guiSlider.getValueInt());

                            this.reconnectTimer = 0;
                            this.updateReconnectButton();
                            this.updateSliderText();
                        }));
    }

    private void updateSliderText() {
        if (this.autoReconnectDelaySlider == null)
            return;

        if (!AutoReconnect.INSTANCE.isEnabled()) {
            this.autoReconnectDelaySlider.displayString = "AutoReconnect: Off";
        } else {
            this.autoReconnectDelaySlider.displayString = "AutoReconnect: " + DECIMAL_FORMAT.format(AutoReconnect.INSTANCE.getDelay() / 1000.0) + "s";
        }
    }

    private void updateReconnectButton() {
        if (reconnectButton != null)
            reconnectButton.displayString = "Reconnect" + (AutoReconnect.INSTANCE.isEnabled() ? " (" + (AutoReconnect.INSTANCE.getDelay() / 1000 - reconnectTimer / 20) + ")" : "");
    }
}