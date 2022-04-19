package net.ccbluex.liquidbounce.features.module.modules.render;


import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui;
import net.ccbluex.liquidbounce.utils.math.MathUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtil;
import net.ccbluex.liquidbounce.utils.render.ESPUtil;
import net.ccbluex.liquidbounce.utils.render.GradientUtil;
import net.ccbluex.liquidbounce.utils.render.RoundedUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.ccbluex.liquidbounce.RenderUtils.drawRect2;
import static net.ccbluex.liquidbounce.RenderUtils.mc;
import static org.lwjgl.opengl.GL11.*;
@ModuleInfo(name = "ESP", description = "Tenacity.", category = ModuleCategory.RENDER)
public class ESP extends Module {
    public static final BoolValue Players = new BoolValue("Player", true);
    public static final BoolValue Animals = new BoolValue("Animals", false);
    public static final BoolValue Mobs = new BoolValue("Mobs", false);
    public final BoolValue mcfont = new BoolValue("Minecraft Font", false);
    public final BoolValue itemHeld = new BoolValue("Item Held", true);
    public final BoolValue boxEsp = new BoolValue("Box", false);
    public static final ListValue boxColorMode = new ListValue("Box Mode", new String[]{
            "Sync",
            "Light Rainbow",
            "Static",
            "Fade",
            "Double Color",
            "Analogous",
            "Default"
    }, "Light Rainbow");
    public static final ListValue degree = new ListValue("Degree", new String[]{
            "30",
            "-30"
    }, "30");
    public final BoolValue healthBar = new BoolValue("Health Bar", true);
    public static final ListValue healthBarMode = new ListValue("Health Bar Mode", new String[]{
            "Color",
            "Health"
    }, "Color");
    public final BoolValue healthBarText = new BoolValue("Health Bar Text", true);
    public final BoolValue nametags = new BoolValue("Tags", true);
    public final BoolValue redTags = new BoolValue("Red Tags", true);
    public final FloatValue scale = new FloatValue("Tag Scale", 0.5F, 0.1F, 10F);
    public final BoolValue healthtext = new BoolValue("Health Text", true);
    public final BoolValue Background = new BoolValue("Background", true);
    private final Map<Entity, Vector4f> entityPosition = new HashMap<>();


    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        entityPosition.clear();
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (shouldRender(entity) && ESPUtil.isInView(entity)) {
                entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
            }
        }

    };

    private final NumberFormat df = new DecimalFormat("0.#");
    private final Color backgroundColor = new Color(10, 10, 10, 130);

    private Color firstColor = Color.BLACK, secondColor = Color.BLACK, thirdColor = Color.BLACK, fourthColor = Color.BLACK;
    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final HUD hudMod = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        if (boxEsp.get()) {
            switch (boxColorMode.get().toLowerCase()) {
                case "sync":
                    Color[] colors = hudMod.getClientColors();
                    firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, colors[0], colors[1], hudMod.hueInterpolation.get());
                    secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90, colors[0], colors[1], hudMod.hueInterpolation.get());
                    thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180, colors[0], colors[1], hudMod.hueInterpolation.get());
                    fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270, colors[0], colors[1], hudMod.hueInterpolation.get());
                    break;
                case "light rainbow":
                    firstColor = ColorUtil.rainbow(15, 0, .6f, 1, 1);
                    secondColor = ColorUtil.rainbow(15, 90, .6f, 1, 1);
                    thirdColor = ColorUtil.rainbow(15, 180, .6f, 1, 1);
                    fourthColor = ColorUtil.rainbow(15, 270, .6f, 1, 1);
                    break;
                case "rainbow":
                    firstColor = ColorUtil.rainbow(15, 0, 1, 1, 1);
                    secondColor = ColorUtil.rainbow(15, 90, 1, 1, 1);
                    thirdColor = ColorUtil.rainbow(15, 180, 1, 1, 1);
                    fourthColor = ColorUtil.rainbow(15, 270, 1, 1, 1);
                    break;
                case "static":
                    firstColor = Color.PINK;
                    secondColor = firstColor;
                    thirdColor = firstColor;
                    fourthColor = firstColor;
                    break;
                case "fade":
                    firstColor = ColorUtil.fade(15, 0,  Color.PINK, 1);
                    secondColor = ColorUtil.fade(15, 90,  Color.PINK, 1);
                    thirdColor = ColorUtil.fade(15, 180,  Color.PINK, 1);
                    fourthColor = ColorUtil.fade(15, 270,  Color.PINK, 1);
                    break;
                case "double color":
                    firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, Color.PINK, Color.BLUE, hudMod.hueInterpolation.get());
                    secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90,  Color.PINK, Color.BLUE, hudMod.hueInterpolation.get());
                    thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180,  Color.PINK,Color.BLUE, hudMod.hueInterpolation.get());
                    fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270,  Color.PINK, Color.BLUE, hudMod.hueInterpolation.get());
                    break;
                case "analogous":
                    int val = degree.get().equals("30") ? 0 : 1;
                    Color analogous = ColorUtil.getAnalogousColor(Color.BLUE)[val];
                    firstColor = ColorUtil.interpolateColorsBackAndForth(15, 0, Color.BLUE, analogous, hudMod.hueInterpolation.get());
                    secondColor = ColorUtil.interpolateColorsBackAndForth(15, 90, Color.BLUE, analogous, hudMod.hueInterpolation.get());
                    thirdColor = ColorUtil.interpolateColorsBackAndForth(15, 180, Color.BLUE, analogous, hudMod.hueInterpolation.get());
                    fourthColor = ColorUtil.interpolateColorsBackAndForth(15, 270,Color.BLUE, analogous, hudMod.hueInterpolation.get());
                    break;
            }
        }

        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (Entity entity : entityPosition.keySet()) {
            Vector4f pos = entityPosition.get(entity);
            float x = pos.getX(),
                    y = pos.getY(),
                    right = pos.getZ(),
                    bottom = pos.getW();

            if (nametags.get() && entity instanceof EntityLivingBase) {
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);
                StringBuilder text = new StringBuilder((redTags.get() ? "§c" : "§f") + StringUtils.stripControlCodes(renderingEntity.getDisplayName().getUnformattedText()));
                if (healthtext.get()) {
                    text.append(String.format(" §7[§r%s HP§7]", df.format(renderingEntity.getHealth())));
                }
                double fontScale = scale.getValue();
                float middle = x + ((right - x) / 2);
                float textWidth = 0;
                double fontHeight;
                if (mcfont.get()) {
                    textWidth = mc.fontRendererObj.getStringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;
                    fontHeight = mc.fontRendererObj.FONT_HEIGHT * fontScale;
                } else {
                    textWidth = (float) Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.stringWidth(text.toString());
                    middle -= (textWidth * fontScale) / 2f;
                    fontHeight = Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.getHeight() * fontScale;
                }

                glPushMatrix();
                glTranslated(middle, y - (fontHeight + 2), 0);
                glScaled(fontScale, fontScale, 1);
                glTranslated(-middle, -(y - (fontHeight + 2)), 0);

                if (Background.get()) {
                        RoundedUtil.drawRound(middle - 3, (float) (y - (fontHeight + 7)), (float) (textWidth + 6),
                                (float) ((fontHeight / fontScale) + 4), 4, backgroundColor);
                }

                GlStateManager.bindTexture(0);
                GlStateManager.resetColor();
                if (mcfont.get()) {
                    mc.fontRendererObj.drawStringWithShadow(text.toString(), middle, (float) (y - (fontHeight + 4)), healthColor.getRGB());
                } else {
                    Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.drawString(text.toString(), middle, (float) (y - (fontHeight + 5)), healthColor.getRGB());
                }

                glPopMatrix();

            }
            if (itemHeld.get() && entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase.getHeldItem() != null) {

                    double fontScale = .5f;
                    float middle = x + ((right - x) / 2);
                    float textWidth = 0;
                    double fontHeight;
                    String text = entityLivingBase.getHeldItem().getDisplayName();
                    if (mcfont.get()) {
                        textWidth = mc.fontRendererObj.getStringWidth(text);
                        middle -= (textWidth * fontScale) / 2f;
                        fontHeight = mc.fontRendererObj.FONT_HEIGHT * fontScale;
                    } else {
                        textWidth = (float) Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.stringWidth(text);
                        middle -= (textWidth * fontScale) / 2f;
                        fontHeight = Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.getHeight() * fontScale;
                    }

                    glPushMatrix();
                    glTranslated(middle, (bottom + 4), 0);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-middle, -(bottom + 4), 0);
                    GlStateManager.bindTexture(0);
                    GlStateManager.resetColor();
                    if (mcfont.get()) {
                        mc.fontRendererObj.drawStringWithShadow(text, middle, bottom + 4, -1);
                    } else {
                        Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.drawString(text.toString(), middle, bottom + 4, -1,true);
                    }
                    glPopMatrix();
                }
            }


            if (healthBar.get() && entity instanceof EntityLivingBase) {
                EntityLivingBase renderingEntity = (EntityLivingBase) entity;
                float healthValue = renderingEntity.getHealth() / renderingEntity.getMaxHealth();
                Color healthColor = healthValue > .75 ? new Color(66, 246, 123) : healthValue > .5 ? new Color(228, 255, 105) : healthValue > .35 ? new Color(236, 100, 64) : new Color(255, 65, 68);

                float height = (bottom - y) + 1;
                if (healthBarMode.get().equals("Color")) {
                    GradientUtil.drawGradientTB(right + 3, y + (height - (height * healthValue)), 1, height * healthValue, 1, secondColor, thirdColor);
                } else {
                }

                if (healthBarText.get()) {
                    healthValue *= 100;
                    String health = String.valueOf(MathUtils.round(healthValue, 1)).substring(0, healthValue == 100 ? 3 : 2);
                    String text = health + "%";
                    double fontScale = .5;
                    float textX = right + 8;
                    float fontHeight = mcfont.get() ? (float) (mc.fontRendererObj.FONT_HEIGHT * fontScale) : (float) (Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.getHeight() * fontScale);
                    float newHeight = height - fontHeight;
                    float textY = y + (newHeight - (newHeight * (healthValue / 100)));

                    glPushMatrix();
                    glTranslated(textX - 5, textY, 1);
                    glScaled(fontScale, fontScale, 1);
                    glTranslated(-(textX - 5), -textY, 1);
                    if (mcfont.get()) {
                        mc.fontRendererObj.drawStringWithShadow(text, textX, textY, -1);
                    } else {
                        Fonts.SFBOLD.SFBOLD_20.SFBOLD_20.drawString(text, textX, textY, -1,true);
                    }
                    glPopMatrix();
                }


            }


            if (boxEsp.get()) {
                float outlineThickness = .5f;
                GlStateManager.resetColor();
                //top
                GradientUtil.drawGradientLR(x, y, (right - x), 1, 1, firstColor, secondColor);
                //left
                GradientUtil.drawGradientTB(x, y, 1, bottom - y, 1, firstColor, fourthColor);
                //bottom
                GradientUtil.drawGradientLR(x, bottom, right - x, 1, 1, fourthColor, thirdColor);
                //right
                GradientUtil.drawGradientTB(right, y, 1, (bottom - y) + 1, 1, secondColor, thirdColor);

                //Outline



            }

        }
    };

    public static boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (Players.get() && entity instanceof EntityPlayer) {
            if (entity == mc.thePlayer) {
                return mc.gameSettings.thirdPersonView != 0;
            }
            return true;
        }
        if (Animals.get() && entity instanceof EntityAnimal) {
            return true;
        }

        return Mobs.get() && entity instanceof EntityMob;
    }


}
