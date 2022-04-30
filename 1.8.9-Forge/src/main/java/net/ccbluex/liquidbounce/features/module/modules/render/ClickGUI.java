/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.cn.Insane.Ui.TestUi.dropdown.Client;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.DropdownClickGui;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.*;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "ClickGUI", description = "Opens the ClickGUI.", category = ModuleCategory.RENDER, keyBind = Keyboard.KEY_RSHIFT)
public class ClickGUI extends Module {
    private final ListValue styleValue = new ListValue("Style", new String[] {"LiquidBounce","Astolfo", "Null", "Slowly", "Black"}, "Null") {
        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            updateStyle();
        }
    };
    private final ListValue mode = new ListValue("Mode",new String[]{"LiquidBounce","Novoline","Tenacity"},"Tenacity");
    public static final BoolValue backback = new BoolValue("Background Accent",true);
    public static final ListValue scrollMode = new ListValue("Scroll Mode", new String[]{"Screen Height", "Value"},"Value");
    public static final ListValue colormode = new ListValue("Setting Accent", new String[]{"White", "Color"},"Color");
    public static final IntegerValue clickHeight = new IntegerValue("Tab Height", 250, 100, 500);
    public final FloatValue scaleValue = new FloatValue("Scale", 1F, 0.7F, 2F);
    public final IntegerValue maxElementsValue = new IntegerValue("MaxElements", 15, 1, 20);
    public final ListValue backgroundValue = new ListValue("Background", new String[] {"Default", "Gradient", "None"}, "None");
    public final ListValue animationValue = new ListValue("Animation", new String[] {"Azura", "Slide", "SlideBounce", "Zoom", "ZoomBounce", "None"}, "zoombounce");
    private static final ListValue colorModeValue = new ListValue("Color", new String[] {"Custom", "Sky", "Rainbow", "LiquidSlowly", "Fade", "Mixer"}, "Fade");
    public static final IntegerValue colorRedValue = new IntegerValue("Red", 0, 0, 255);
    public static final IntegerValue colorGreenValue = new IntegerValue("Green", 160, 0, 255);
    public static final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
    private static final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
    private static final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
    private static final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
    private static final ListValue clickguicolormode = new ListValue("ClickGuiColor",new String[]{"Drak","White"},"Drak");
    public final BoolValue getClosePrevious = (BoolValue) new BoolValue("ClosePrevious",true);




    public static boolean isLight (){
        if (clickguicolormode.get().equalsIgnoreCase("White")){
            return true;
        }
        return false;
    }
    public static Color generateColor() {
        Color c = new Color(255, 255, 255, 255);
        switch (colorModeValue.get().toLowerCase()) {
            case "custom":
                c = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
                break;
            case "rainbow":
                c = new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
                break;
            case "sky":
                c = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
                break;
            case "liquidslowly":
                c = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
                break;
            case "fade":
                c = ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
                break;
        }
        return c;
    }

    @Override
    public void onEnable() {

        if (mode.get().equalsIgnoreCase("LiquidBounce")){
            updateStyle();
            mc.displayGuiScreen(LiquidBounce.clickGui);
        }
        if (mode.get().equalsIgnoreCase("Novoline")){
            mc.displayGuiScreen(Client.getInstance().getDropDownGUI());
        }
        if (mode.get().equalsIgnoreCase("Tenacity")){
            mc.displayGuiScreen(new DropdownClickGui());
        }

    }

    @EventTarget
    public void onTick(TickEvent event) {
        this.setState(false);
    }

    private void updateStyle() {
        switch(styleValue.get().toLowerCase()) {
            case "liquidbounce":
                LiquidBounce.clickGui.style = new LiquidBounceStyle();
                break;
            case "astolfo":
                LiquidBounce.clickGui.style = new AstolfoStyle();
                break;
            case "null":
                LiquidBounce.clickGui.style = new NullStyle();
                break;
            case "slowly":
                LiquidBounce.clickGui.style = new SlowlyStyle();
                break;
            case "black":
                LiquidBounce.clickGui.style = new BlackStyle();
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onPacket(final PacketEvent event) {
        final Packet packet = event.getPacket();

        if (packet instanceof S2EPacketCloseWindow && mc.currentScreen instanceof ClickGui) {
            event.cancelEvent();
        }
    }
}
