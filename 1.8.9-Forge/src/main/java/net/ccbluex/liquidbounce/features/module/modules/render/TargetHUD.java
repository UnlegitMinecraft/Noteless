package net.ccbluex.liquidbounce.features.module.modules.render;

import antiskidderobfuscator.NativeMethod;
import com.sun.jna.platform.unix.X11;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.DropdownClickGui;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.utils.math.MathUtils;
import net.ccbluex.liquidbounce.utils.render.*;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.ccbluex.liquidbounce.ui.font.Fonts.tahoma35;
import static net.ccbluex.liquidbounce.utils.render.BlendUtils.getHealthColor;
import static net.ccbluex.liquidbounce.utils.render.RenderUtils.*;
import static net.ccbluex.liquidbounce.utils.render.RenderUtils3.drawFastRoundedRect;
import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTranslated;

@ModuleInfo(name = "TargetHUD", description = "Lnk.", category = ModuleCategory.RENDER, array = false)
public class TargetHUD extends Module {
    public static final ListValue mode = new ListValue("Mode", new String[]{
            "Moon",
            "Flux",
            "Remix",
            "Exhibition",
            "Ketamine",
            "Hanabi"
    }, "Hanabi");
    private final IntegerValue customX = new IntegerValue("X", -150, -500, 500);
    private final IntegerValue customY = new IntegerValue("Y", 80, -500, 500);


    private float lastHealth = 0.0F;
    private Translate damgeAnimation = new Translate(0f , 0f);


    private EntityLivingBase lasttarget;
    private double xPos, yPos;
    private double width, height;
    private double healthBarWidth;
    private boolean dragon;
    private DecimalFormat format = new DecimalFormat("0.0");
    private DecimalFormat format0 = new DecimalFormat("0");
    boolean nulltarget = false;

    private double healthBarWidth2;
    private double hudHeight;

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mode.get().equals("Moon")) {
            KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
            boolean guichat = this.mc.currentScreen instanceof GuiChat;
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            float n = 2.0F;
            int x = customX.getValue();
            int y = customY.getValue();
            EntityLivingBase target = guichat ? this.mc.thePlayer : killAura.getTarget();
            if (target != null) {
                int staticColor;
                int n2;
                int n3;
                float health;
                float scaledWidth;
                float scaledHeight;
                Color healthcolor;
                float maxHealth1;
                float health1 = ((EntityLivingBase) target).getHealth();
                maxHealth1 = ((EntityLivingBase) target).getMaxHealth();
                float healthPercentage11 = health1 / maxHealth1;

                if (target instanceof EntityPlayer) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x, y, 0.0D);
                    n2 = 200;
                    n3 = 160;
                    RenderUtils.drawBorderedRect((float) n2 - 55.0F, (float) n3 + 40.0F, (float) n2 - 30.0F + 78.0F,
                            (float) n3 + 72.0F, 3.0F, new Color(ClickGUI.colorRedValue.get(),ClickGUI.colorGreenValue.get(),ClickGUI.colorBlueValue.get(),255).getRGB(),new Color(0,0,0,120).getRGB());
                    health = target.getHealth();
                    healthPercentage11 = health / ((EntityLivingBase) target).getMaxHealth();
                    scaledWidth = 0.0F;
                    if (healthPercentage11 != this.lastHealth) {
                        scaledHeight = healthPercentage11 - this.lastHealth;
                        scaledWidth = this.lastHealth;
                        this.lastHealth += scaledHeight / 20.0F;
                    }
                    healthcolor = Color.WHITE;
                    if (healthPercentage11 * 100.0F > 75.0F) {
                        healthcolor = Color.GREEN;
                    } else if (healthPercentage11 * 100.0F > 50.0F && healthPercentage11 * 100.0F < 75.0F) {
                        healthcolor = Color.YELLOW;
                    } else if (healthPercentage11 * 100.0F < 50.0F && healthPercentage11 * 100.0F > 25.0F) {
                        healthcolor = Color.ORANGE;
                    } else if (healthPercentage11 * 100.0F < 25.0F) {
                        healthcolor = Color.RED;
                    }
                    drawRect((float) n2 - 25.0F, (float) n3 + 61.0F,
                            (float) n2 - 25F + 69.7F * scaledWidth, (double) ((float) n3 + 63.1f),
                            healthcolor.getRGB());
                    float armorValue = (float) target.getTotalArmorValue();
                    double armorWidth = (double) armorValue / 31.0D;

                    drawRect((float) n2 - 25.0F, (double) ((float) n3 + 66.0F),
                            (double) ((float) n2 - 25F) + 108.0D * armorWidth, (double) ((float) n3 + 68.1F),
                            new Color(45, 171, 252).getRGB());
                    net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SF.SF_20.SF_20.drawString(((EntityPlayer) target).getGameProfile().getName(),
                            (float) (n2 - 25), (float) (n3 + 44), -1);

                    Fonts.SF.SF_14.SF_14.drawString("Blocking: " + ((EntityPlayer) target).isBlocking(), (float) (n2 - 25), n3 + 54.5f,
                            -1);
                    //FontLoaders.kiona14.drawStringWithShadow(
                    //			"Health: " + String.format("%.1f", ((EntityLivingBase) target).getHealth()),
                    //			(float) (n2 - 46.8f), (float) (n3 + 51), -1);
                    float hurtPercent = target.hurtTime;
                    float scale;
                    if (hurtPercent == 0f) {
                        scale = 1.0f;
                    } else if (hurtPercent < 0.5f) {
                        scale = 1.0f - (0.2f * hurtPercent * 1.0f);
                    } else {
                        scale = 0.8f + (0.2f * (hurtPercent - 0.5f) * 0.1f);
                    }
                    int size = 26;
                    GL11.glPushMatrix();
                    GL11.glTranslatef((n2 - 53), (n3 + 43), 0f);
                    // 受伤的缩放效果
                    GL11.glScalef(scale, scale, scale);
                    GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f);
                    // 受伤的红色效果
                    GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f);
                    //	RenderUtils.quickDrawHead(((AbstractClientPlayer) target).getLocationSkin(), 0, 0, size, size)
                    习玉米(((AbstractClientPlayer) target).getLocationSkin(), 0, 0, size, size);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    GlStateManager.resetColor();
                }
            }
        }
        if(mode.get().equals("Ketamine")) {
            KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);

            EntityLivingBase target = killAura.getTarget();

            if (target == null && mc.currentScreen instanceof GuiChat) {
                target = mc.thePlayer;
            }

            if (target == null) {
                return;
            }



            double hpClamped = target.getHealth() / target.getMaxHealth();
            hpClamped = MathHelper.clamp_double(hpClamped, 0.0, 1.0);

            final double hpWidth = 80 * hpClamped;
            healthBarWidth = RenderUtils.animateProgress(healthBarWidth, hpWidth, 75.f);
            double healthAnimatedPercent = 20.0 * (healthBarWidth / 80.0) * 5.0;

            final int startColour = ColorUtil.fadeBetween(ColorUtil.getClientColour(), ColorUtil.getSecondaryColour(), 0);
            final int endColour = ColorUtil.fadeBetween(ColorUtil.getSecondaryColour(), ColorUtil.getClientColour(), 250);

            double x = customX.get();
            double y = customY.get();
            double margin = 2.0f;





            double heightBounds = Fonts.SF.SF_20.SF_20.getHeight();

            double width = 85 + margin * 2.0f;
            double height = heightBounds + margin * 2.0f;

            this.width = width + 70;
            this.height = height + 30;


            // Draw TargetHud
            {

                // Top Gradient Bar
                RenderUtils.glDrawSidewaysGradientRect(x, y, width + 70, 1, startColour, endColour);

                // Background Rounded Rect - 60% Opacity
                RenderUtils.glDrawFilledQuad(x, y + 1, (float) width + 70, (float) (height + 28), 0x60 << 24);

                // Draw Face
                if (target instanceof EntityPlayer) {
                    drawFace(x + 3, y + 5, 33, 33, (AbstractClientPlayer) target);
                }

                // Target Name
                mc.fontRendererObj.drawStringWithShadow(target.getName(), (float) (x + 39f), (float) (y + 6f), 0xFFFFFFFF);
                // Genshin Moment
                String[] parts = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
                int[] partIndex = {0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB, 0xC8C9CB};

                boolean isArmor = target.getTotalArmorValue() > 0;
                boolean isInLiquid = target.isInWater();
                boolean isBurner = target.isBurning();
                boolean onGround = target.onGround;
                boolean canSee = target.canEntityBeSeen(mc.thePlayer);
                boolean isHeal =  target.getActivePotionEffect(Potion.regeneration) != null;
                boolean speed = target.getActivePotionEffect(Potion.moveSpeed) != null;

                if (isArmor)
                    partIndex[0] = 0xEFC434;
                if (isInLiquid)
                    partIndex[1] = 0x04ACE2;
                if (isBurner)
                    partIndex[2] = 0xF97824;
                if (onGround)
                    partIndex[3] = 0xBA9243;
                if (canSee)
                    partIndex[4] = 0xA776D4;
                if (isHeal)
                    partIndex[5] = 0x9AD019;
                if (speed)
                    partIndex[6] = 0x76E2A9;

                for (int i = 0; i < 7; i++)
                Fonts.GENSHIN.GENSHIN25.GENSHIN25.drawString(parts[i], x + 39f + (i * (Fonts.GENSHIN.GENSHIN25.GENSHIN25.stringWidth(parts[i])) + 1), y + 17f, partIndex[i], true);

                // Health Bar
                RenderUtils.glDrawSidewaysGradientRect(x + 39, y + 30, (float) healthBarWidth, 6.75f, startColour, endColour);
                // Draw Health Value
                Fonts.SF.SF_20.SF_20.drawString(format.format(healthAnimatedPercent) + "%", x + healthBarWidth + hpClamped + 41, y + 30, 0xFFFFFFFF,true);
            }
        }
        if (mode.get().equals("Hanabi")) {
            int blackcolor2;
            int blackcolor = (new Color(0, 0, 0, 180)).getRGB();
            blackcolor2 = (new Color(200, 200, 200, 160)).getRGB();
            ScaledResolution sr2 = new ScaledResolution(mc);
            float scaledWidth = (float)sr2.getScaledWidth();
            float scaledHeight = (float)sr2.getScaledHeight();
            float x;
            float y;
            float diff;
            KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
            EntityLivingBase target = killAura.getTarget();
            this.nulltarget = target == null;

            x = scaledWidth / 2.0F - 50.0F;
            y = scaledHeight / 2.0F + 32.0F;
            double hpPercentage;
            Color hurt;
            int healthColor;
            String healthStr;
            if (this.nulltarget) {
                diff = 0.0F;
                hpPercentage = (double)(diff / 20.0F);
                hurt = Color.getHSBColor(0.8333333F, 0.0F, 1.0F);
                healthStr = String.valueOf(0.0F);
                healthColor = getHealthColor(0.0F, 20.0F).getRGB();
            } else {
                diff = target.getHealth();
                hpPercentage = (double)(diff / target.getMaxHealth());
                hurt = Color.getHSBColor(0.8611111F, (float)target.hurtTime / 10.0F, 1.0F);
                healthStr = String.valueOf((float)((int)target.getHealth()) / 2.0F);
                healthColor = getHealthColor(target.getHealth(), target.getMaxHealth()).getRGB();
            }

            hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0);
            double hpWidth = 140.0 * hpPercentage;
            if (this.nulltarget) {
                this.healthBarWidth2 = RenderUtils.getAnimationStateSmooth(0.0, this.healthBarWidth2, (double)(6.0F / (float)Minecraft.getDebugFPS()));
                this.healthBarWidth = RenderUtils.getAnimationStateSmooth(0.0, this.healthBarWidth, (double)(14.0F / (float)Minecraft.getDebugFPS()));
                this.hudHeight = RenderUtils.getAnimationStateSmooth(0.0, this.hudHeight, (double)(8.0F / (float)Minecraft.getDebugFPS()));
            } else {
                this.healthBarWidth2 = (double)AnimationUtils.moveUD((float)this.healthBarWidth2, (float)hpWidth, 6.0F / (float)Minecraft.getDebugFPS(), 3.0F / (float)Minecraft.getDebugFPS());
                this.healthBarWidth = RenderUtils.getAnimationStateSmooth(hpWidth, this.healthBarWidth, (double)(14.0F / (float)Minecraft.getDebugFPS()));
                this.hudHeight = RenderUtils.getAnimationStateSmooth(40.0, this.hudHeight, (double)(8.0F / (float)Minecraft.getDebugFPS()));
            }

            if (this.hudHeight == 0.0) {
                this.healthBarWidth2 = 140.0;
                this.healthBarWidth = 140.0;
            }

            GL11.glEnable(3089);
            RenderUtils.prepareScissorBox(x, (float)((double)y + 40.0 - this.hudHeight), x + 140.0F, (float)((double)y + 40.0));
            RenderUtils.drawRect(x, y, x + 140.0F, y + 40.0F, blackcolor);
            RenderUtils.drawRect(x, y + 37.0F, x + 140.0F, y + 40.0F, (new Color(0, 0, 0, 49)).getRGB());
            RenderUtils.drawRect(x, y + 37.0F, (float)((double)x + this.healthBarWidth2), y + 40.0F, (new Color(255, 0, 213, 220)).getRGB());
            RenderUtils.drawGradientSideways((double)x, (double)(y + 37.0F), (double)x + this.healthBarWidth, (double)(y + 40.0F), (new Color(0, 81, 179)).getRGB(), healthColor);
            Fonts.SF.SF_16.SF_16.drawString(healthStr, x + 40.0F + 85.0F - (float)  Fonts.SF.SF_16.SF_16.stringWidth(healthStr) / 2.0F + (float)mc.fontRendererObj.getStringWidth("❤") / 1.9F, y + 29.0F, blackcolor2,true);
             mc.fontRendererObj.drawStringWithShadow("❤", x + 40.0F + 85.0F - (float)  Fonts.SF.SF_16.SF_16.stringWidth(healthStr) / 2.0F - (float)mc.fontRendererObj.getStringWidth("❤") / 1.9F, y + 26.5F, hurt.getRGB());

            if (this.nulltarget) {
               Fonts.SF.SF_14.SF_14.drawString("XYZ:0 0 0 | Hurt: false", x + 37.0F, y + 15.0F, Colors.WHITE.c,true);
                Fonts.SF.SF_16.SF_16.drawString("(No target)", x + 36.0F, y + 5.0F, Colors.WHITE.c,true);
            } else {
                Fonts.SF.SF_14.SF_14.drawString("XYZ : " + (int)target.posX + " " + (int)target.posY + " " + (int)target.posZ + " | Hurt: " + (target.hurtTime > 0), x + 37.0F, y + 17.0F, Colors.WHITE.c,true);
                if (target instanceof EntityPlayer) {
                    Fonts.SF.SF_14.SF_14.drawString("Block: " + (((EntityPlayer)target).isBlocking() ? "True" : "False"), x + 37.0F, y + 27.0F, Colors.WHITE.c,true);
                }

                mc.fontRendererObj.drawStringWithShadow(target.getName(), x + 36.0F, y + 4.0F, Colors.WHITE.c);
                if (target instanceof EntityPlayer) {
                    GlStateManager.resetColor();
                    mc.getTextureManager().bindTexture(((AbstractClientPlayer)target).getLocationSkin());
                    GlStateManager.color(1.0F, 1.0F, 1.0F);
                    Gui.drawScaledCustomSizeModalRect((int)x + 3, (int)y + 3, 8.0F, 8.0F, 8, 8, 32, 32, 64.0F, 64.0F);
                }
            }

            GL11.glDisable(3089);
        }
        if(mode.get().equals("Flux")) {
            final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
            EntityLivingBase ent = ka.getTarget();
            if (ent != null) {
                GL11.glPushMatrix();
                //更改TargetHUD在屏幕坐标的初始位置
                Color color;
                String playerName = ent.getName();
                String healthStr = Math.round(ent.getHealth() * 10) / 10d + " hp";
                float width = Math.max(75, Fonts.SF.SF_20.SF_20.stringWidth(playerName) + 25);
                GL11.glTranslatef(customX.get(), customY.get(), 0);
                drawFastRoundedRect(0, 0, 28 + width, 28, 2,new Color(0,0,0,140).getRGB());
                String str = ent.getDisplayName().getUnformattedText();
                char[] ch = str.toCharArray();

                Fonts.SF.SF_16.SF_16.drawString(playerName, 30f, 4f, FluxColor.WHITE.c);

                Fonts.SF.SF_17.SF_17.drawString(healthStr, 26 + width - Fonts.SF.SF_17.SF_17.stringWidth(healthStr) - 2, 4f, 0xffcccccc);

                boolean isNaN = Float.isNaN(ent.getHealth());
                float health = isNaN ? 20 : ent.getHealth();
                float maxHealth = isNaN ? 20 : ent.getMaxHealth();
                float healthPercent = MathUtils.clampValue(health / maxHealth, 0, 1);

                int hue = (int) (healthPercent * 120);
                color = Color.getHSBColor(hue / 360f, 0.7f, 1f);

                drawRect(37, 14.5f, 26 + width - 2, 17.5f, reAlpha(FluxColor.BLACK.c, 0.6f));

                float barWidth = (26 + width - 2) - 37;
                float drawPercent = 37 + (barWidth / 100) * (healthPercent * 100);


                if(ent != lasttarget || lasttarget == null) {
                    damgeAnimation.setX(drawPercent);
                } else{
                    damgeAnimation.translate(drawPercent , 0f , 0.3f);
                }

                drawRect(37, 14.5f,damgeAnimation.getX(), 17.5f, color.darker().getRGB());
                drawRect(37, 14.5f, drawPercent, 17.5f, color.getRGB());

                Fonts.FluxICONFONT.FluxICONFONT_10.FluxICONFONT_10.drawString("s", 30f, 16, FluxColor.WHITE.c);
                Fonts.FluxICONFONT.FluxICONFONT_10.FluxICONFONT_10.drawString("r", 30f, 23, FluxColor.WHITE.c);

                float f3 = 37 + (barWidth / 100f) * (ent.getTotalArmorValue() * 5);
                drawRect(37, 21.5f, 26 + width - 2, 24.5f, reAlpha(FluxColor.BLACK.c, 0.6f));
                drawRect(37, 21.5f, f3, 24.5f, 0xff4286f5);

                习包子(1.5f, 1.5f, 26.5f, 26.5f, 0.5f, 0x00000000, Color.GREEN.getRGB());
                GlStateManager.resetColor();
                for (NetworkPlayerInfo info : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.getNetHandler().getPlayerInfoMap())) {
                    if (mc.theWorld.getPlayerEntityByUUID(info.getGameProfile().getId()) == ent) {
                        mc.getTextureManager().bindTexture(info.getLocationSkin());
                        革命暴乱(2f, 2f, 8.0f, 8.0f, 8, 8, 24, 24, 64.0f, 64.0f);
                        GlStateManager.bindTexture(0);
                        break;
                    }
                }
                GL11.glPopMatrix();
                lasttarget = ent;
            }
        }
        if(mode.get().equals("Remix")){
            final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
            EntityLivingBase target = ka.getTarget();
            if (target == null || !(target instanceof EntityPlayer) || mc.theWorld.getEntityByID(target.getEntityId()) == null || mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(mc.thePlayer) > 100) {
                return;
            }
            GlStateManager.pushMatrix();
            // Width and height
            final float width = mc.displayWidth / (float) (mc.gameSettings.guiScale * 2) + 680;
            final float height = mc.displayHeight / (float) (mc.gameSettings.guiScale * 2) + 280;
            GlStateManager.translate(width - 660, height - 160.0f - 90.0f, 0.0f);
            // Border rect.
            RenderUtils.rectangle(2, -6, 156.0, 47.0, new Color(25, 25, 25).getRGB());
            // Main rect.
            RenderUtils.rectangle(4, -4, 154.0, 45.0, new Color(45, 45, 45).getRGB());
            // Draws name.
            mc.fontRendererObj.drawStringWithShadow(((EntityPlayer) target).getName(), 46f, 0.3f, -1);
            // Gets health.
            final float health = ((EntityPlayer) target).getHealth();
            // Color stuff for the healthBar.
            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            // Max health.
            final float progress = health / ((EntityPlayer) target).getMaxHealth();
            // Color.
            final Color healthColor = health >= 0.0f ? ColorUtil.blendColors(fractions, colors, progress).brighter() : Color.RED;
            // $$ draws the 4 fucking boxes killing my self btw. $$
            RenderUtils.rect(45, 11, 20, 20, new Color(25, 25, 25));
            RenderUtils.rect(46, 12, 18, 18, new Color(95, 95, 95));
            RenderUtils.rect(67, 11, 20, 20, new Color(25, 25, 25));
            RenderUtils.rect(68, 12, 18, 18, new Color(95, 95, 95));
            RenderUtils.rect(89, 11, 20, 20, new Color(25, 25, 25));
            RenderUtils.rect(90, 12, 18, 18, new Color(95, 95, 95));
            RenderUtils.rect(111, 11, 20, 20, new Color(25, 25, 25));
            RenderUtils.rect(112, 12, 18, 18, new Color(95, 95, 95));
            // Draws the current ping/ms.
            NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
            final String ping = (Objects.isNull(networkPlayerInfo) ? "0ms" : networkPlayerInfo.getResponseTime() + "ms");
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.6, 0.6, 0.6);
            mc.fontRendererObj.drawStringWithShadow(ping, 230, 40, Color.WHITE.getRGB());
            GlStateManager.popMatrix();
            // Draws the ping thingy from tab. :sunglasses:
            if (target != null && networkPlayerInfo != null) (mc.ingameGUI.getTabList()).drawPing(103, 50, 14, networkPlayerInfo);
            // Round.
            double cockWidth = 0.0;
            cockWidth = MathUtils.round(cockWidth, (int) 5.0);
            if (cockWidth < 50.0) {
                cockWidth = 50.0;
            }
            // Bar behind healthbar.
            RenderUtils.rectangle(6.5, 37.3, 151, 43, Color.RED.darker().darker().getRGB());
            final double healthBarPos = cockWidth * (double) progress;
            // health bar.
            RenderUtils.rect(6f, 37.3f, (healthBarPos * 2.9), 6f, healthColor);
            // Gets the armor thingy for the bar.
            float armorValue = ((EntityPlayer) target).getTotalArmorValue();
            double armorWidth = armorValue / 20D;
            // Bar behind armor bar.
            RenderUtils.rect(45.5f, 32.3f, 105, 2.5f, new Color(0, 0, 255));
            // Armor bar.
            RenderUtils.rect(45.5f, 32.3f, (105 * armorWidth), 2.5f, new Color(0, 45, 255));
            // White rect around head.
            RenderUtils.rect(6, -2, 37, 37, new Color(205, 205, 205));
            // Draws head.
            六四真相(7, -1, 3, 3, 3, 3, 35, 35, 24, 24, (AbstractClientPlayer) target);
            // Draws armor.
            GlStateManager.scale(1.1, 1.1, 1.1);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            // Draw targets armor the worst way possible.
            if (target != null) 全境示威运动(24, 11); 权贵家族(44, 11); 胡耀邦逝世(64, 11); 打倒中共(84, 11);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
        if(mode.get().equals("Exhibition")){
            final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
            EntityLivingBase target = ka.getTarget();
            if (target == null || !(target instanceof EntityPlayer) || mc.theWorld.getEntityByID(target.getEntityId()) == null || mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(mc.thePlayer) > 100) {
                return;
            }
            GlStateManager.pushMatrix();
            // Width and height
            final float width = mc.displayWidth / (float) (mc.gameSettings.guiScale * 2) + 680;
            final float height = mc.displayHeight / (float) (mc.gameSettings.guiScale * 2) + 280;
            GlStateManager.translate(width - 660, height - 160.0f - 90.0f, 0.0f);
            // Draws the skeet rectangles.
            RenderUtils.skeetRect(0, -2.0, tahoma35.getStringWidth(((EntityPlayer) target).getName()) > 70.0f ? (double) (124.0f + tahoma35.getStringWidth(((EntityPlayer) target).getName()) - 70.0f) : 124.0, 38.0, 1.0);
            RenderUtils.skeetRectSmall(0.0f, -2.0f, 124.0f, 38.0f, 1.0);
            // Draws name.
            tahoma35.drawStringWithShadow(((EntityPlayer) target).getName(), 42.3f, 0.3f, -1);
            // Gets health.
            final float health = ((EntityPlayer) target).getHealth();
            // Gets health and absorption
            final float healthWithAbsorption = ((EntityPlayer) target).getHealth() + ((EntityPlayer) target).getAbsorptionAmount();
            // Color stuff for the healthBar.
            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            // Max health.
            final float progress = health / ((EntityPlayer) target).getMaxHealth();
            // Color.
            final Color healthColor = health >= 0.0f ? ColorUtil.blendColors(fractions, colors, progress).brighter() : Color.RED;
            // Round.
            double cockWidth = 0.0;
            cockWidth = MathUtils.round(cockWidth, (int) 5.0);
            if (cockWidth < 50.0) {
                cockWidth = 50.0;
            }
            // Healthbar + absorption
            final double healthBarPos = cockWidth * (double) progress;
            RenderUtils.rectangle(42.5, 10.3, 103, 13.5, healthColor.darker().darker().darker().darker().getRGB());
            RenderUtils.rectangle(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.getRGB());
            if (((EntityPlayer) target).getAbsorptionAmount() > 0.0f) {
                RenderUtils.rectangle(97.5 - (double) ((EntityPlayer) target).getAbsorptionAmount(), 10.3, 103.5, 13.5, new Color(137, 112, 9).getRGB());
            }
            // Draws rect around health bar.
            RenderUtils.rectangleBordered(42.0, 9.8f, 54.0 + cockWidth, 14.0, 0.5, 0, Color.BLACK.getRGB());
            // Draws the lines between the healthbar to make it look like boxes.
            for (int dist = 1; dist < 10; ++dist) {
                final double cock = cockWidth / 8.5 * (double) dist;
                RenderUtils.rectangle(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.getRGB());
            }
            // Draw targets hp number and distance number.
            GlStateManager.scale(0.5, 0.5, 0.5);
            final int distance = (int) mc.thePlayer.getDistanceToEntity(target);
            final String nice = "HP: " + (int) healthWithAbsorption + " | Dist: " + distance;
            mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true);
            GlStateManager.scale(2.0, 2.0, 2.0);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            // Draw targets armor and tools and weapons and shows the enchants.
            if (target != null) 太子党关系网络(28, 20);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            // Draws targets model.
            GlStateManager.scale(0.31, 0.31, 0.31);
            GlStateManager.translate(73.0f, 102.0f, 40.0f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            RenderUtils.drawModel(target.rotationYaw, target.rotationPitch, (EntityLivingBase) target);
            GlStateManager.popMatrix();
        }
    }

    private void 太子党关系网络(final int x, final int y) {
        final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        EntityLivingBase target = ka.getTarget();
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            final ItemStack armor = ((EntityPlayer) target).getCurrentArmor(geraltOfNigeria);
            if (armor != null) {
                stuff.add(armor);
            }
        }
        if (((EntityPlayer) target).getHeldItem() != null) {
            stuff.add(((EntityPlayer) target).getHeldItem());
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, cock + x, y);
            RenderUtils.renderEnchantText(yes, cock + x, (y + 0.5f));
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }
    public static void 六四真相(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtils.drawScaledCustomSizeModalRect((int)x, (int)y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }
    public void 革命暴乱(float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        GL11.glColor4f(1, 1, 1, 1);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) x, (double) (y + height), 0.0D).tex((double) (u * f), (double) ((v + (float) vHeight) * f1)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), 0.0D).tex((double) ((u + (float) uWidth) * f), (double) ((v + (float) vHeight) * f1)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) y, 0.0D).tex((double) ((u + (float) uWidth) * f), (double) (v * f1)).endVertex();
        bufferbuilder.pos((double) x, (double) y, 0.0D).tex((double) (u * f), (double) (v * f1)).endVertex();
        tessellator.draw();
    }
    private void 全境示威运动(final int x, final int y) {
        final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        EntityLivingBase target = ka.getTarget();
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final java.util.List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack helmet = ((EntityPlayer) target).getCurrentArmor(3);
        if (helmet != null) {
            stuff.add(helmet);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void 权贵家族(final int x, final int y) {
        final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        EntityLivingBase target = ka.getTarget();
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final java.util.List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack chest = ((EntityPlayer) target).getCurrentArmor(2);
        if (chest != null) {
            stuff.add(chest);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void 胡耀邦逝世(final int x, final int y) {
        final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        EntityLivingBase target = ka.getTarget();
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final java.util.List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack legs = ((EntityPlayer) target).getCurrentArmor(1);
        if (legs != null) {
            stuff.add(legs);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void 打倒中共(final int x, final int y) {
        final KillAura ka = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
        EntityLivingBase target = ka.getTarget();
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack boots = ((EntityPlayer) target).getCurrentArmor(0);
        if (boots != null) {
            stuff.add(boots);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }
    public void 习包子(double x, double y, double x1, double y1, double width, int internalColor,
                                  int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
    private void drawFace(double x, double y, double width, double height, AbstractClientPlayer target) {
        ResourceLocation skin = target.getLocationSkin();
        mc.getTextureManager().bindTexture(skin);
        glEnable(GL_BLEND);
        glColor4f(255, 255, 255, 1);

        final float hurtTimePercentage = (target.hurtTime - mc.timer.renderPartialTicks) / target.maxHurtTime;

        if (hurtTimePercentage > 0) {
            x += 1 * hurtTimePercentage;
            y += 1 * hurtTimePercentage;
            height -= 2 * hurtTimePercentage;
            width -= 2 * hurtTimePercentage;
        }

        RenderUtils.drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, height, 64, 64);

        if (hurtTimePercentage > 0.0) {
            glTranslated(x, y, 0);
            glDisable(GL_TEXTURE_2D);
            final boolean restore = RenderUtils.glEnableBlend();
            glShadeModel(GL_SMOOTH);
            glDisable(GL_ALPHA_TEST);

            final float lineWidth = 10.f;
            glLineWidth(lineWidth);

            final int fadeOutColour = ColorUtil.fadeTo(0x00000000, ColorUtil.blendHealthColours(target.getHealth() / target.getMaxHealth()), hurtTimePercentage);

            glBegin(GL_QUADS);
            {
                // Left
                RenderUtils.glColour(fadeOutColour);
                glVertex2d(0, 0);
                glVertex2d(0, height);
                RenderUtils.glColour(0x00FF0000);
                glVertex2d(lineWidth, height - lineWidth);
                glVertex2d(lineWidth, lineWidth);

                // Right
                RenderUtils.glColour(0x00FF0000);
                glVertex2d(width - lineWidth, lineWidth);
                glVertex2d(width - lineWidth, height - lineWidth);
                RenderUtils.glColour(fadeOutColour);
                glVertex2d(width, height);
                glVertex2d(width, 0);

                // Top
                RenderUtils.glColour(fadeOutColour);
                glVertex2d(0, 0);
                RenderUtils.glColour(0x00FF0000);
                glVertex2d(lineWidth, lineWidth);
                glVertex2d(width - lineWidth, lineWidth);
                RenderUtils.glColour(fadeOutColour);
                glVertex2d(width, 0);

                // Bottom
                RenderUtils.glColour(0x00FF0000);
                glVertex2d(lineWidth, height - lineWidth);
                RenderUtils.glColour(fadeOutColour);
                glVertex2d(0, height);
                glVertex2d(width, height);
                RenderUtils.glColour(0x00FF0000);
                glVertex2d(width - lineWidth, height - lineWidth);
            }
            glEnd();

            glEnable(GL_ALPHA_TEST);
            glShadeModel(GL_FLAT);
            RenderUtils.glRestoreBlend(restore);
            glEnable(GL_TEXTURE_2D);
            glTranslated(-x, -y, 0);
        }
    }
    public static void 习馒头(int x, int y, float u, float v, int uWidth, int vHeight, int width,
                                                     int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1)
                .endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    public void 习玉米(ResourceLocation skin, int x, int y, int width, int height) {
        mc.getTextureManager().bindTexture(skin);
        习馒头(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
        习馒头(x, y, 40F, 8F, 8, 8, width, height, 64F, 64F);
    }
}

