/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;


import cn.WbxMain;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.misc.SoundFxPlayer;
import net.ccbluex.liquidbounce.utils.render.ColorUtil;
import net.ccbluex.liquidbounce.utils.render.GradientUtil;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.timer.TimeHelper;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI.*;

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
@SideOnly(Side.CLIENT)
public class HUD extends Module {

    public final BoolValue blackHotbarValue = new BoolValue("BlackHotbar", true);
    public final BoolValue inventoryParticle = new BoolValue("InventoryParticle", false);
    public static final ListValue shadowValue = new ListValue("ShadowMode", new String[]{
            "LiquidBounce",
            "Outline",
            "Default",
            "Autumn"
    }, "LiquidBounce");
    public static final ListValue clolormode = new ListValue("ColorMode", new String[]{
            "Rainbow",
            "Light Rainbow",
            "Static",
            "Double Color",
            "Default"
    }, "Light Rainbow");
    public static final IntegerValue fontWidthValue = new IntegerValue("FontWidth", 7, 5, 10);
    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 255, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 255, 0, 255);
    private final BoolValue blurValue = new BoolValue("Blur", false);
    public static final BoolValue hueInterpolation = new BoolValue("hueInterpolation", false);
    public final BoolValue movingcolors = new BoolValue("MovingColors", false);
    public final BoolValue playTimeValue = new BoolValue("PlayTime", true);
    private int startTime;
    public final BoolValue logo = new BoolValue("Logo", true);
    public final BoolValue test1 = new BoolValue("BMCTest", false);
    public final TextValue clientname =  new TextValue("ClientName", "Noteless");
    public final TextValue domainValue =  new TextValue("Scoreboard-Domain", "One.Noteless");
    public final BoolValue hotbar = new BoolValue("HUD_Hotbar", true);
    public final BoolValue WhiteInfo = new BoolValue("WhiteInfo", false);
    public final BoolValue paimon = new BoolValue("paimon", true);
    public final BoolValue fontChatValue = new BoolValue("FontChat", false);
    public final  BoolValue chatRectValue = new BoolValue("ChatRect", true);
    public final BoolValue HealthValue  = new BoolValue("Health", true);
    public final  BoolValue chatCombineValue = new BoolValue("ChatCombine", true);
    public final  BoolValue chatAnimValue = new BoolValue("ChatAnimation", true);
    public double High1123;
    private TimeHelper timeHelper = new TimeHelper();
    public static double alpha;
    public int photoIndex = 0;
    public static float hue = 0.0F;
    public HUD() {
        setState(true);
    }
    final MSTimer timer = new MSTimer();
    private final Map<String, String> bottomLeftText = new LinkedHashMap<>();

    private void drawInfo(Color[] clientColors) {
        ScaledResolution sr = new ScaledResolution(mc);
        bottomLeftText.put("XYZ:", Math.round(mc.thePlayer.posX) + " " + Math.round(mc.thePlayer.posY) + " " + Math.round(mc.thePlayer.posZ));
        bottomLeftText.put("FPS:", String.valueOf(Minecraft.getDebugFPS()));
        bottomLeftText.put("Speed:", String.valueOf(calculateBPS()));


        float yOffset = (float) (0);
        if(WhiteInfo.get()){
            float boldFontMovement =  net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.getHeight() + 2 + yOffset;
            for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.drawString(line.getKey()+line.getValue(), 2, sr.getScaledHeight() - boldFontMovement, -1);
                boldFontMovement += net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.getHeight() + 2;
            }
        }else {
            float height = (net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.getHeight() + 2) * bottomLeftText.keySet().size();
            float width = (float) net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.stringWidth("Speed:");
            GradientUtil.applyGradientVertical(2, sr.getScaledHeight() - (height + yOffset), width, height, 1, clientColors[0], clientColors[1], () -> {
                float boldFontMovement = net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.getHeight() + 2 + yOffset;
                for (Map.Entry<String, String> line : bottomLeftText.entrySet()) {
                    net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.drawString(line.getKey()+line.getValue(), 2, sr.getScaledHeight() - boldFontMovement, -1);
                    boldFontMovement += net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_18.SFBOLD_18.getHeight() + 2;
                }
            });
        }
    }
    private double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {

        Color[] clientColors = getClientColors();
        if(logo.get()) {
            GradientUtil.applyGradientHorizontal(5, 5, (float) net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_35.SFBOLD_35.stringWidth(clientname.get()), 20, 1, clientColors[0], clientColors[1], () -> {
                RenderUtils.setAlphaLimit(0);
                net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.TenacityBold.TenacityBold40.TenacityBold40.drawString(clientname.get(), 5, 5, new Color(0, 0, 0, 0).getRGB());
            });
            GlStateManager.resetColor();
            net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SF.SF_20.SF_20.drawString(WbxMain.version, net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_35.SFBOLD_35.stringWidth(clientname.get()) + 6, 5, clientColors[1].getRGB());
        }
        drawInfo(clientColors);
        ScaledResolution sr = new ScaledResolution(mc);
        float width = sr.getScaledWidth();
        float height = sr.getScaledHeight();
        if(test1.get() && !timer.hasTimePassed(5000L)){
            net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_50.SFBOLD_50.drawString("VICTORY!",sr.getScaledWidth() / 2F - net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_50.SFBOLD_50.stringWidth("Victory!") / 2F, (BossStatus.bossName != null && BossStatus.statusBarTime > 0) ? 47 : 30, Color.YELLOW.getRGB(),true);
            net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_35.SFBOLD_35.drawString("You were the last man standing!",sr.getScaledWidth() / 2F - net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SFBOLD.SFBOLD_35.SFBOLD_35.stringWidth("You were the last man standing!") / 2F, (BossStatus.bossName != null && BossStatus.statusBarTime > 0) ? 47 : 60, Color.GRAY.getRGB(),true);
        }
        // PlayTime
        if (playTimeValue.get()) {
            final int endTime = (int) System.currentTimeMillis();
            final int pasted = (endTime - startTime);
            String timed = String.format("%dh %dm %ds",
                    TimeUnit.MILLISECONDS.toHours(pasted),
                    TimeUnit.MILLISECONDS.toMinutes(pasted) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(pasted)),
                    TimeUnit.MILLISECONDS.toSeconds(pasted) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(pasted)));
            mc.fontRendererObj.drawStringWithShadow(timed, sr.getScaledWidth() / 2F - mc.fontRendererObj.getStringWidth(timed) / 2F, (BossStatus.bossName != null && BossStatus.statusBarTime > 0) ? 47 : 30, -1);
        }
        if (mc.currentScreen instanceof GuiHudDesigner)
            return;
        High1123 = RenderUtils.height() - 50;
        LiquidBounce.hud.render(false);
        if (HealthValue.get())
           mc.fontRendererObj.drawStringWithShadow(String.valueOf(MathHelper.ceiling_float_int( mc.thePlayer.getHealth())), width / 2 - 4, height / 2 - 13, mc.thePlayer.getHealth() <= 15 ? new Color(255, 0, 0).getRGB() : new Color(0, 255, 0).getRGB());
        GlStateManager.resetColor();
    }


    @EventTarget
    public void onConnecting(ConnectingEvent e) {
        startTime = (int) System.currentTimeMillis();
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (timeHelper.delay(35)) {
            if(this.photoIndex + 1 > 52) {
                photoIndex = 0;
            } else {
                ++photoIndex;
            }
            timeHelper.reset();
        }
        LiquidBounce.hud.update();
    }
    public Color[] getClientColors() {
        Color firstColor;
        Color secondColor;
        switch (clolormode.get().toLowerCase()) {
            case "light rainbow":
                firstColor = ColorUtil.rainbow(15, 1, .6f, 1, 1);
                secondColor = ColorUtil.rainbow(15, 40, .6f, 1, 1);
                break;
            case "rainbow":
                firstColor = ColorUtil.rainbow(15, 1, 1, 1, 1);
                secondColor = ColorUtil.rainbow(15, 40, 1, 1, 1);
                break;
            case "double color":
                firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, Color.PINK, Color.BLUE, hueInterpolation.get());
                secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90,  Color.PINK, Color.BLUE, hueInterpolation.get());
                break;
            case "static":
                firstColor = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                secondColor = firstColor;
                break;
            default:
                firstColor = new Color(-1);
                secondColor = new Color(-1);
                break;
        }
        return new Color[]{firstColor, secondColor};
    }
    @EventTarget
    public void onKey(final KeyEvent event) {
        LiquidBounce.hud.handleKey('a', event.getKey());
    }

    @EventTarget(ignoreCondition = true)
    public void onScreen(final ScreenEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null)
            return;

        if (getState() && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.getGuiScreen() != null &&
                !(event.getGuiScreen() instanceof GuiChat || event.getGuiScreen() instanceof GuiHudDesigner))
            mc.entityRenderer.loadShader(new ResourceLocation( "liquidbounce/blur.json"));
        else if (mc.entityRenderer.getShaderGroup() != null &&
                mc.entityRenderer.getShaderGroup().getShaderGroupName().contains("liquidbounce/blur.json"))
            mc.entityRenderer.stopUseShader();
    }
}
