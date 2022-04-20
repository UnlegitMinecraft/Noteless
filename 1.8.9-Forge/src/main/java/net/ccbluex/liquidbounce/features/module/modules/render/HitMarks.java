package net.ccbluex.liquidbounce.features.module.modules.render;


import net.ccbluex.liquidbounce.event.AttackEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static net.ccbluex.liquidbounce.RenderUtils.mc;

@ModuleInfo(name = "HitMarks", description = "Rise.", category = ModuleCategory.RENDER)
public class HitMarks extends Module {
    private int lastEntity;
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    public final IntegerValue pitch = new IntegerValue("Pitch", 1, 1, 10);
    public final BoolValue randomPitch = new BoolValue("Random Pitch", false);
    @EventTarget
    public void onAttackEvent(final AttackEvent event) {
        final Entity e = event.getTargetEntity();
        if (e != null) {
            lastEntity = e.getEntityId();
            attackTimer.reset();
        }
    }

    @EventTarget
    public void onRender2DEvent(final Render2DEvent event) {

        if (!timer.hasReached(500)) {
            final ScaledResolution sr = new ScaledResolution(mc);
            RenderUtils.color(Color.WHITE);
            RenderUtils.imageCentered(new ResourceLocation("liquidbounce/hitmarker.png"), sr.getScaledWidth() / 2f + 0.1f, sr.getScaledHeight() / 2f + 0.1f, 280 / 20f, 280 / 20f);
        }
        if (timer.hasReached(500) && !timer.hasReached(755)) {
            final ScaledResolution sr = new ScaledResolution(mc);
            RenderUtils.color(new Color(255, 255, 255, (int) Math.abs(((System.nanoTime() / 1000000L) - timer.lastMS) - 755)));
            RenderUtils.imageCentered(new ResourceLocation("liquidbounce/hitmarker.png"), sr.getScaledWidth() / 2f + 0.1f, sr.getScaledHeight() / 2f + 0.1f, 280 / 20f, 280 / 20f);
        }
    }
}
