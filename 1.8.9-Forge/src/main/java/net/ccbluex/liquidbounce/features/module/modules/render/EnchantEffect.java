package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;

import java.awt.*;
import java.util.Random;

@ModuleInfo(name = "EnchantEffect", description = "qwq", category = ModuleCategory.RENDER)
public class EnchantEffect extends Module {
    public IntegerValue redValue = new IntegerValue("Red", 255, 0, 255);
    public IntegerValue greenValue = new IntegerValue("Green", 0, 0, 255);
    public IntegerValue blueValue = new IntegerValue("Blue", 0, 0, 255);
    public ListValue modeValue = new ListValue("Mode", new String[]{"Rainbow","Custom"}, "Custom");
    public float hue = 0.0F;
    public int currentColor = new Color(redValue.get(),greenValue.get(),blueValue.get()).getRGB();


    @EventTarget
    public void Render2d(final Render2DEvent e) {
        hue += 1f / 5.0f;
        if (hue > 255.0f) {
            hue = 0.0f;
        }
    }

}
