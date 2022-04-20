package net.ccbluex.liquidbounce;

import java.awt.Color;


import net.minecraft.client.renderer.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;


import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.List;

import static net.ccbluex.liquidbounce.RenderUtil.R2DUtils.arc;
import static net.ccbluex.liquidbounce.utils.render.ColorManager.rainbow;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class RenderUtil {

    public RenderUtil() {
        super();
    }

    public static float delta;

    public static float smooth(float current, float end, float smoothSpeed, float minSpeed) {
        float movement = (end - current) * smoothSpeed;
        if (movement > 0) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
        } else if (movement < 0) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
        }
        return current + movement;
    }

    public static void drawRoundedRect2(float x, float y, float x2, float y2, final float round, final int color) {
        x += (float) (round / 2.0f + 0.5);
        y += (float) (round / 2.0f + 0.5);
        x2 -= (float) (round / 2.0f + 0.5);
        y2 -= (float) (round / 2.0f + 0.5);
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, color);
        circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
        circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
        circle(x + round / 2.0f, y + round / 2.0f, round, color);
        circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
        Gui.drawRect((int) (x - round / 2.0f - 0.5f), (int) (y + round / 2.0f), (int) x2, (int) (y2 - round / 2.0f),
                color);
        Gui.drawRect((int) x, (int) (y + round / 2.0f), (int) (x2 + round / 2.0f + 0.5f), (int) (y2 - round / 2.0f),
                color);
        Gui.drawRect((int) (x + round / 2.0f), (int) (y - round / 2.0f - 0.5f), (int) (x2 - round / 2.0f),
                (int) (y2 - round / 2.0f), color);
        Gui.drawRect((int) (x + round / 2.0f), (int) y, (int) (x2 - round / 2.0f), (int) (y2 + round / 2.0f + 0.5f),
                color);
    }

    public static void drawRectBordered(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        rectangle(x + width, y, x1 - width, y + width, borderColor);
        rectangle(x, y, x + width, y1, borderColor);
        rectangle(x1 - width, y, x1, y1, borderColor);
        rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, Color color) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable((int) 2929);
        GL11.glEnable((int) 3042);
        GL11.glDepthMask((boolean) false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor3f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getBlue() / 255.0f), (float) ((float) color.getRed() / 255.0f));
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 2929);
    }

    public static void circle(float x, float y, float radius, int fill) {
        GL11.glEnable(3042);
        arc(x, y, 0.0f, 360.0f, radius, fill);
        GL11.glDisable(3042);
    }

    public static int width() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    }

    public static int height() {
        return new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight();
    }

    public static void circle(float x, float y, float radius, Color fill) {
        arc(x, y, 0.0f, 360.0f, radius, fill);
    }

    public static void drawFilledCircle(int xx, int yy, float radius, Color col) {
        int sections = 100;
        double dAngle = 6.283185307179586 / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glBegin(6);
        int i = 0;
        while (i < sections) {
            float x = (float) ((double) radius * Math.sin((double) i * dAngle));
            float y = (float) ((double) radius * Math.cos((double) i * dAngle));
            GL11.glColor4f((float) col.getRed() / 255.0f, (float) col.getGreen() / 255.0f, (float) col.getBlue() / 255.0f, (float) col.getAlpha() / 255.0f);
            GL11.glVertex2f((float) xx + x, (float) yy + y);
            ++i;
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }


    public static void drawFilledCircle(int xx, int yy, float radius, int col) {
        float f = (float) (col >> 24 & 255) / 255.0f;
        float f1 = (float) (col >> 16 & 255) / 255.0f;
        float f2 = (float) (col >> 8 & 255) / 255.0f;
        float f3 = (float) (col & 255) / 255.0f;
        int sections = 50;
        double dAngle = 6.283185307179586 / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(6);
        int i = 0;
        while (i < sections) {
            float x = (float) ((double) radius * Math.sin((double) i * dAngle));
            float y = (float) ((double) radius * Math.cos((double) i * dAngle));
            GL11.glColor4f(f1, f2, f3, f);
            GL11.glVertex2f((float) xx + x, (float) yy + y);
            ++i;
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawFilledCircle(float xx, float yy, float radius, int col) {
        float f = (float) (col >> 24 & 255) / 255.0f;
        float f1 = (float) (col >> 16 & 255) / 255.0f;
        float f2 = (float) (col >> 8 & 255) / 255.0f;
        float f3 = (float) (col & 255) / 255.0f;
        int sections = 50;
        double dAngle = 6.283185307179586 / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glBegin(6);
        int i = 0;
        while (i < sections) {
            float x = (float) ((double) radius * Math.sin((double) i * dAngle));
            float y = (float) ((double) radius * Math.cos((double) i * dAngle));
            GL11.glColor4f(f1, f2, f3, f);
            GL11.glVertex2f(xx + x, yy + y);
            ++i;
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawFilledCircle(int xx, int yy, float radius, int col, int xLeft, int yAbove, int xRight, int yUnder) {
        float f = (float) (col >> 24 & 255) / 255.0f;
        float f1 = (float) (col >> 16 & 255) / 255.0f;
        float f2 = (float) (col >> 8 & 255) / 255.0f;
        float f3 = (float) (col & 255) / 255.0f;
        int sections = 50;
        double dAngle = 6.283185307179586 / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glBegin(6);
        int i = 0;
        while (i < sections) {
            float x = (float) ((double) radius * Math.sin((double) i * dAngle));
            float y = (float) ((double) radius * Math.cos((double) i * dAngle));
            float xEnd = (float) xx + x;
            float yEnd = (float) yy + y;
            if (xEnd < (float) xLeft) {
                xEnd = xLeft;
            }
            if (xEnd > (float) xRight) {
                xEnd = xRight;
            }
            if (yEnd < (float) yAbove) {
                yEnd = yAbove;
            }
            if (yEnd > (float) yUnder) {
                yEnd = yUnder;
            }
            GL11.glColor4f(f1, f2, f3, f);
            GL11.glVertex2f(xEnd, yEnd);
            ++i;
        }
        GlStateManager.color(0.0f, 0.0f, 0.0f);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawArc(float x1, float y1, double r, int color, int startPoint, double arc, int linewidth) {
        r *= 2.0D;
        x1 *= 2;
        y1 *= 2;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glLineWidth(linewidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = startPoint; i <= arc; i += 1) {
            double x = Math.sin(i * 3.141592653589793D / 180.0D) * r;
            double y = Math.cos(i * 3.141592653589793D / 180.0D) * r;
            GL11.glVertex2d(x1 + x, y1 + y);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    public static void drawRainbowRectVertical(int x, int y, int x1, int y1, int segments, float alpha) {
        if (segments < 1) {
            segments = 1;
        }

        if (segments > y1 - y) {
            segments = y1 - y;
        }

        int segmentLength = (y1 - y) / segments;
        long time = System.nanoTime();

        for (int i = 0; i < segments; ++i) {
            Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y + segmentLength * i - 1, x1, y + (segmentLength + 1) * i, rainbow(time, (float) i * 0.1F, alpha).getRGB(), rainbow(time, ((float) i + 0.1F) * 0.1F, alpha).getRGB());
        }

    }

    public static void drawCircle(int xx, int yy, int radius, Color col) {
        byte sections = 70;
        double dAngle = 6.283185307179586D / (double) sections;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glBegin(2);

        for (int i = 0; i < sections; ++i) {
            float x = (float) ((double) radius * Math.cos((double) i * dAngle));
            float y = (float) ((double) radius * Math.sin((double) i * dAngle));
            GL11.glColor4f((float) col.getRed() / 255.0F, (float) col.getGreen() / 255.0F, (float) col.getBlue() / 255.0F, (float) col.getAlpha() / 255.0F);
            GL11.glVertex2f((float) xx + x, (float) yy + y);
        }

        GlStateManager.color(0.0F, 0.0F, 0.0F);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static double getAnimationState(double animation, double finalState, double speed) {
        float add = (float) ((double) delta * speed);
        if (animation < finalState) {
            if (animation + (double) add >= finalState) return finalState;
            return animation += (double) add;
        }
        if (animation - (double) add <= finalState) return finalState;
        return animation -= (double) add;
    }


    public static void drawOutline(float x, float y, float width, float height, float lineWidth, int color) {
        RenderUtil.drawRect(x, y, x + width, y + lineWidth, color);
        RenderUtil.drawRect(x, y, x + lineWidth, y + height, color);
        RenderUtil.drawRect(x, y + height - lineWidth, x + width, y + height, color);
        RenderUtil.drawRect(x + width - lineWidth, y, x + width, y + height, color);
    }

    public static double interpolate(double newPos, double oldPos) {
        return oldPos + (newPos - oldPos) * (double) Minecraft.getMinecraft().timer.renderPartialTicks;
    }

    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static float[] getRGBAs(int rgb) {
        return new float[]{((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F,
                ((rgb >> 24) & 255) / 255F};
    }

    public static Color makeDarker(Color curColor, int distance) {
        int red = curColor.getRed();
        int green = curColor.getGreen();
        int blue = curColor.getBlue();
        red -= distance;
        green -= distance;
        blue -= distance;
        if (red < 0)
            red = 0;
        if (green < 0)
            green = 0;
        if (blue < 0)
            blue = 0;
        return new Color(red, green, blue);
    }


    public static void drawBoundingBox(double x, double y, double z, double width, double height, float red, float green, float blue, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.color(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public static class R3DUtils {
        public static void startDrawing() {
            GL11.glEnable((int) 3042);
            GL11.glEnable((int) 3042);
            GL11.glBlendFunc((int) 770, (int) 771);
            GL11.glEnable((int) 2848);
            GL11.glDisable((int) 3553);
            GL11.glDisable((int) 2929);
            Minecraft.getMinecraft().entityRenderer
                    .setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
        }

        public static void stopDrawing() {
            GL11.glDisable((int) 3042);
            GL11.glEnable((int) 3553);
            GL11.glDisable((int) 2848);
            GL11.glDisable((int) 3042);
            GL11.glEnable((int) 2929);
        }

        public static void drawOutlinedBox(AxisAlignedBB box) {
            if (box == null) {
                return;
            }
            Minecraft.getMinecraft().entityRenderer
                    .setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks, 0);
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 1);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.minZ);
            GL11.glVertex3d((double) box.maxX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.maxX, (double) box.maxY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.minY, (double) box.maxZ);
            GL11.glVertex3d((double) box.minX, (double) box.maxY, (double) box.maxZ);
            GL11.glEnd();
        }


        public static void drawFilledBox(AxisAlignedBB mask) {
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 4);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.minX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.minZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.maxY, (double) mask.maxZ);
            GL11.glVertex3d((double) mask.maxX, (double) mask.minY, (double) mask.maxZ);
            GL11.glEnd();
        }

        public static void drawOutlinedBoundingBox(AxisAlignedBB aabb) {
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 3);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glEnd();
            GL11.glBegin((int) 1);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.minZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.maxX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.minY, (double) aabb.maxZ);
            GL11.glVertex3d((double) aabb.minX, (double) aabb.maxY, (double) aabb.maxZ);
            GL11.glEnd();
        }
    }

    public static class R2DUtils {
        public static void enableGL2D() {
            GL11.glDisable((int) 2929);
            GL11.glEnable((int) 3042);
            GL11.glDisable((int) 3553);
            GL11.glBlendFunc((int) 770, (int) 771);
            GL11.glDepthMask((boolean) true);
            GL11.glEnable((int) 2848);
            GL11.glHint((int) 3154, (int) 4354);
            GL11.glHint((int) 3155, (int) 4354);
        }

        public static void disableGL2D() {
            GL11.glEnable((int) 3553);
            GL11.glDisable((int) 3042);
            GL11.glEnable((int) 2929);
            GL11.glDisable((int) 2848);
            GL11.glHint((int) 3154, (int) 4352);
            GL11.glHint((int) 3155, (int) 4352);
        }


        public static void drawCircle(double x, double y, double radius, int c) {
            float f2 = (float) (c >> 24 & 255) / 255.0f;
            float f22 = (float) (c >> 16 & 255) / 255.0f;
            float f3 = (float) (c >> 8 & 255) / 255.0f;
            float f4 = (float) (c & 255) / 255.0f;
            GlStateManager.alphaFunc(516, 0.001f);
            GlStateManager.color(f22, f3, f4, f2);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tes = Tessellator.getInstance();
            double i = 0.0;
            while (i < 360.0) {
                double f5 = Math.sin(i * 3.141592653589793 / 180.0) * radius;
                double f6 = Math.cos(i * 3.141592653589793 / 180.0) * radius;
                GL11.glVertex2d((double) ((double) f3 + x), (double) ((double) f4 + y));
                i += 1.0;
            }
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.alphaFunc(516, 0.1f);
        }

        public static void drawRoundedRect2(float x, float y, float x2, float y2, final float round, final int color) {
            x += (float) (round / 2.0f + 0.5);
            y += (float) (round / 2.0f + 0.5);
            x2 -= (float) (round / 2.0f + 0.5);
            y2 -= (float) (round / 2.0f + 0.5);
            Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, color);
            circle(x2 - round / 2.0f, y + round / 2.0f, round, color);
            circle(x + round / 2.0f, y2 - round / 2.0f, round, color);
            circle(x + round / 2.0f, y + round / 2.0f, round, color);
            circle(x2 - round / 2.0f, y2 - round / 2.0f, round, color);
            Gui.drawRect((int) (x - round / 2.0f - 0.5f), (int) (y + round / 2.0f), (int) x2, (int) (y2 - round / 2.0f),
                    color);
            Gui.drawRect((int) x, (int) (y + round / 2.0f), (int) (x2 + round / 2.0f + 0.5f), (int) (y2 - round / 2.0f),
                    color);
            Gui.drawRect((int) (x + round / 2.0f), (int) (y - round / 2.0f - 0.5f), (int) (x2 - round / 2.0f),
                    (int) (y2 - round / 2.0f), color);
            Gui.drawRect((int) (x + round / 2.0f), (int) y, (int) (x2 - round / 2.0f), (int) (y2 + round / 2.0f + 0.5f),
                    color);
        }

        public static void circle(final float x, final float y, final float radius, final int fill) {
            arc(x, y, 0.0f, 360.0f, radius, fill);
        }

        public static void circle(final float x, final float y, final float radius, final Color fill) {
            arc(x, y, 0.0f, 360.0f, radius, fill);
        }

        public static void arc(final float x, final float y, final float start, final float end, final float radius,
                               final int color) {
            arcEllipse(x, y, start, end, radius, radius, color);
        }

        public static void arc(final float x, final float y, final float start, final float end, final float radius,
                               final Color color) {
            arcEllipse(x, y, start, end, radius, radius, color);
        }

        public static void arcEllipse(final float x, final float y, float start, float end, final float w,
                                      final float h, final int color) {
            GlStateManager.color(0.0f, 0.0f, 0.0f);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            float temp = 0.0f;
            if (start > end) {
                temp = end;
                end = start;
                start = temp;
            }
            final float var11 = (color >> 24 & 0xFF) / 255.0f;
            final float var12 = (color >> 16 & 0xFF) / 255.0f;
            final float var13 = (color >> 8 & 0xFF) / 255.0f;
            final float var14 = (color & 0xFF) / 255.0f;
            final Tessellator var15 = Tessellator.getInstance();
            final WorldRenderer var16 = var15.getWorldRenderer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(var12, var13, var14, var11);
            if (var11 > 0.5f) {
                GL11.glEnable(2848);
                GL11.glLineWidth(2.0f);
                GL11.glBegin(3);
                for (float i = end; i >= start; i -= 4.0f) {
                    final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                    final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                    GL11.glVertex2f(x + ldx, y + ldy);
                }
                GL11.glEnd();
                GL11.glDisable(2848);
            }
            GL11.glBegin(6);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }

        public static void arcEllipse(final float x, final float y, float start, float end, final float w,
                                      final float h, final Color color) {
            GlStateManager.color(0.0f, 0.0f, 0.0f);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            float temp = 0.0f;
            if (start > end) {
                temp = end;
                end = start;
                start = temp;
            }
            final Tessellator var9 = Tessellator.getInstance();
            final WorldRenderer var10 = var9.getWorldRenderer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f,
                    color.getAlpha() / 255.0f);
            if (color.getAlpha() > 0.5f) {
                GL11.glEnable(2848);
                GL11.glLineWidth(2.0f);
                GL11.glBegin(3);
                for (float i = end; i >= start; i -= 4.0f) {
                    final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w * 1.001f;
                    final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h * 1.001f;
                    GL11.glVertex2f(x + ldx, y + ldy);
                }
                GL11.glEnd();
                GL11.glDisable(2848);
            }
            GL11.glBegin(6);
            for (float i = end; i >= start; i -= 4.0f) {
                final float ldx = (float) Math.cos(i * 3.141592653589793 / 180.0) * w;
                final float ldy = (float) Math.sin(i * 3.141592653589793 / 180.0) * h;
                GL11.glVertex2f(x + ldx, y + ldy);
            }
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }

        public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
            R2DUtils.enableGL2D();
            GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
            R2DUtils.drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
            R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
            R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
            R2DUtils.drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
            R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
            R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
            R2DUtils.drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
            R2DUtils.drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
            R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
            GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
            R2DUtils.disableGL2D();
            Gui.drawRect(0, 0, 0, 0, 0);
        }

        public static void drawRect(double x2, double y2, double x1, double y1, int color) {
            R2DUtils.enableGL2D();
            R2DUtils.glColor(color);
            R2DUtils.drawRect(x2, y2, x1, y1);
            R2DUtils.disableGL2D();
        }

        private static void drawRect(double x2, double y2, double x1, double y1) {
            GL11.glBegin((int) 7);
            GL11.glVertex2d((double) x2, (double) y1);
            GL11.glVertex2d((double) x1, (double) y1);
            GL11.glVertex2d((double) x1, (double) y2);
            GL11.glVertex2d((double) x2, (double) y2);
            GL11.glEnd();
        }

        public static void glColor(int hex) {
            float alpha = (float) (hex >> 24 & 255) / 255.0f;
            float red = (float) (hex >> 16 & 255) / 255.0f;
            float green = (float) (hex >> 8 & 255) / 255.0f;
            float blue = (float) (hex & 255) / 255.0f;
            GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
        }

        public static void drawRect(float x, float y, float x1, float y1, int color) {
            R2DUtils.enableGL2D();
            glColor(color);
            R2DUtils.drawRect(x, y, x1, y1);
            R2DUtils.disableGL2D();
        }

        public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
            R2DUtils.enableGL2D();
            glColor(borderColor);
            R2DUtils.drawRect(x + width, y, x1 - width, y + width);
            R2DUtils.drawRect(x, y, x + width, y1);
            R2DUtils.drawRect(x1 - width, y, x1, y1);
            R2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
            R2DUtils.disableGL2D();
        }

        public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
            R2DUtils.enableGL2D();
            GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
            R2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
            R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
            R2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
            R2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
            R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
            GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
            R2DUtils.disableGL2D();
        }

        public static void drawGradientRect(float x, float y, float x1, float y1, int topColor, int bottomColor) {
            R2DUtils.enableGL2D();
            GL11.glShadeModel((int) 7425);
            GL11.glBegin((int) 7);
            glColor(topColor);
            GL11.glVertex2f((float) x, (float) y1);
            GL11.glVertex2f((float) x1, (float) y1);
            glColor(bottomColor);
            GL11.glVertex2f((float) x1, (float) y);
            GL11.glVertex2f((float) x, (float) y);
            GL11.glEnd();
            GL11.glShadeModel((int) 7424);
            R2DUtils.disableGL2D();
        }

        public static void drawHLine(float x, float y, float x1, int y1) {
            if (y < x) {
                float var5 = x;
                x = y;
                y = var5;
            }
            R2DUtils.drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
        }

        public static void drawVLine(float x, float y, float x1, int y1) {
            if (x1 < y) {
                float var5 = y;
                y = x1;
                x1 = var5;
            }
            R2DUtils.drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
        }

        public static void drawHLine(float x, float y, float x1, int y1, int y2) {
            if (y < x) {
                float var5 = x;
                x = y;
                y = var5;
            }
            R2DUtils.drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);
        }

        public static void drawRect(float x, float y, float x1, float y1) {
            GL11.glBegin((int) 7);
            GL11.glVertex2f((float) x, (float) y1);
            GL11.glVertex2f((float) x1, (float) y1);
            GL11.glVertex2f((float) x1, (float) y);
            GL11.glVertex2f((float) x, (float) y);
            GL11.glEnd();
        }
    }

    public static void drawBorderedCircle(double x, double y, float radius, int outsideC, int insideC) {
        //  GL11.glEnable((int)3042);
        GL11.glDisable((int) 3553);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glEnable((int) 2848);
        GL11.glPushMatrix();
        float scale = 0.1f;
        GL11.glScalef((float) 0.1f, (float) 0.1f, (float) 0.1f);
        drawCircle(x *= 10, y *= 10, radius *= 10.0f, insideC);
        // drawUnfilledCircle(x, y, radius, 1.0f, outsideC);
        GL11.glScalef((float) 10.0f, (float) 10.0f, (float) 10.0f);
        GL11.glPopMatrix();
        GL11.glEnable((int) 3553);
        //  GL11.glDisable((int)3042);
        GL11.glDisable((int) 2848);
    }

    public static int getHexRGB(final int hex) {
        return 0xFF000000 | hex;
    }

    public static void drawCustomImage(int x, int y, int width, int height, ResourceLocation image) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable((int) 2929);
        GL11.glEnable((int) 3042);
        GL11.glDepthMask((boolean) false);
        OpenGlHelper.glBlendFunc((int) 770, (int) 771, (int) 1, (int) 0);
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, (float) 0.0f, (float) 0.0f, (int) width, (int) height,
                (float) width, (float) height);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 2929);
        Gui.drawRect(0, 0, 0, 0, 0);
    }

    public static void prepareScissorBox(ScaledResolution sr, float x, float y, float width, float height) {
        float x2 = x + width;
        float y2 = y + height;
        int factor = sr.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((sr.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor),
                (int) ((y2 - y) * factor));
    }

    public static void drawBorderedRect(double x2, double d2, double x22, double e2, float l1, int col1, int col2) {
        drawRect(x2, d2, x22, e2, col2);
        float f2 = (float) (col1 >> 24 & 255) / 255.0f;
        float f22 = (float) (col1 >> 16 & 255) / 255.0f;
        float f3 = (float) (col1 >> 8 & 255) / 255.0f;
        float f4 = (float) (col1 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f22, f3, f4, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, d2);
        GL11.glVertex2d(x2, e2);
        GL11.glVertex2d(x22, e2);
        GL11.glVertex2d(x22, d2);
        GL11.glVertex2d(x2, d2);
        GL11.glVertex2d(x22, d2);
        GL11.glVertex2d(x2, e2);
        GL11.glVertex2d(x22, e2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, float width, int borderColor) {
        R2DUtils.enableGL2D();
        glColor(borderColor);
        R2DUtils.drawRect(x + width, y, x1 - width, y + width);
        R2DUtils.drawRect(x, y, x + width, y1);
        R2DUtils.drawRect(x1 - width, y, x1, y1);
        R2DUtils.drawRect(x + width, y1 - width, x1 - width, y1);
        R2DUtils.disableGL2D();
    }

    public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
        R2DUtils.enableGL2D();
        GL11.glScalef((float) 0.5f, (float) 0.5f, (float) 0.5f);
        R2DUtils.drawVLine(x *= 2.0f, y *= 2.0f, y1 *= 2.0f, borderC);
        R2DUtils.drawVLine((x1 *= 2.0f) - 1.0f, y, y1, borderC);
        R2DUtils.drawHLine(x, x1 - 1.0f, y, borderC);
        R2DUtils.drawHLine(x, x1 - 2.0f, y1 - 1.0f, borderC);
        R2DUtils.drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float) 2.0f, (float) 2.0f, (float) 2.0f);
        R2DUtils.disableGL2D();
    }

    public static void pre() {
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
    }

    public static void post() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0, 1.0, 1.0);
    }


    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static Color blend(final Color color1, final Color color2, final double ratio) {
        final float r = (float) ratio;
        final float ir = 1.0f - r;
        final float[] rgb1 = new float[3];
        final float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        final Color color3 = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir,
                rgb1[2] * r + rgb2[2] * ir);
        return color3;
    }


    public static void setupRender(final boolean start) {
        if (start) {
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
        }
        GlStateManager.depthMask(!start);
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable((int) 2929);
        GL11.glEnable((int) 3042);
        GL11.glDepthMask((boolean) false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, width, height);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 2929);
    }

    public static void layeredRect(double x1, double y1, double x2, double y2, int outline, int inline,
                                   int background) {
        R2DUtils.drawRect(x1, y1, x2, y2, outline);
        R2DUtils.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, inline);
        R2DUtils.drawRect(x1 + 2, y1 + 2, x2 - 2, y2 - 2, background);
    }

    public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
                                     float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
        GL11.glPushMatrix();
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glDisable((int) 3553);
        GL11.glEnable((int) 2848);
        GL11.glDisable((int) 2929);
        GL11.glDepthMask((boolean) false);
        GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glLineWidth((float) lineWdith);
        GL11.glColor4f((float) lineRed, (float) lineGreen, (float) lineBlue, (float) lineAlpha);
        RenderUtil
                .drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable((int) 2848);
        GL11.glEnable((int) 3553);
        GL11.glEnable((int) 2929);
        GL11.glDepthMask((boolean) true);
        GL11.glDisable((int) 3042);
        GL11.glPopMatrix();
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
        float red = 0.003921569F * redRGB;
        float green = 0.003921569F * greenRGB;
        float blue = 0.003921569F * blueRGB;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1, 1, 1, 1);
    }


    public static void drawVerticalGradientSideways(double left, double top, double right, double bottom, int col1,
                                                    int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        float f4 = (float) (col2 >> 24 & 255) / 255.0F;
        float f5 = (float) (col2 >> 16 & 255) / 255.0F;
        float f6 = (float) (col2 >> 8 & 255) / 255.0F;
        float f7 = (float) (col2 & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer world = tessellator.getWorldRenderer();
        world.begin(7, DefaultVertexFormats.POSITION_COLOR);
        world.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        world.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        world.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        world.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawHorizontalGradientSideways(double left, double top, double right, double bottom, int col1,
                                                      int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        float f4 = (float) (col2 >> 24 & 255) / 255.0F;
        float f5 = (float) (col2 >> 16 & 255) / 255.0F;
        float f6 = (float) (col2 >> 8 & 255) / 255.0F;
        float f7 = (float) (col2 & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer world = tessellator.getWorldRenderer();
        world.begin(7, DefaultVertexFormats.POSITION_COLOR);
        world.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        world.pos(left, bottom, 0).color(f1, f2, f3, f).endVertex();
        world.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        world.pos(right, top, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawImage(final int x, final int y, final int width, final int height,
                                 final ResourceLocation image, Color color) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glDisable(GL_POLYGON_SMOOTH);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void drawCircle(double x, double y, double radius, int c) {
        GL11.glEnable(GL_MULTISAMPLE);
        GL11.glEnable(GL_POLYGON_SMOOTH);
        float alpha = (float) (c >> 24 & 255) / 255.0f;
        float red = (float) (c >> 16 & 255) / 255.0f;
        float green = (float) (c >> 8 & 255) / 255.0f;
        float blue = (float) (c & 255) / 255.0f;
        boolean blend = GL11.glIsEnabled((int) 3042);
        boolean line = GL11.glIsEnabled((int) 2848);
        boolean texture = GL11.glIsEnabled((int) 3553);
        if (!blend) {
            GL11.glEnable((int) 3042);
        }
        if (!line) {
            GL11.glEnable((int) 2848);
        }
        if (texture) {
            GL11.glDisable((int) 3553);
        }
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
        GL11.glBegin((int) 9);
        int i = 0;
        while (i <= 360) {
            GL11.glVertex2d(
                    (double) ((double) x + Math.sin((double) ((double) i * 3.141526 / 180.0)) * (double) radius),
                    (double) ((double) y + Math.cos((double) ((double) i * 3.141526 / 180.0)) * (double) radius));
            ++i;
        }
        GL11.glEnd();
        if (texture) {
            GL11.glEnable((int) 3553);
        }
        if (!line) {
            GL11.glDisable((int) 2848);
        }
        if (!blend) {
            GL11.glDisable((int) 3042);
        }
        GL11.glDisable(GL_POLYGON_SMOOTH);
        GL11.glClear(0);
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {

        GL11.glPushMatrix();
        cx *= 2.0F;
        cy *= 2.0F;

        float theta = (float) (6.2831852D / num_segments);
        float p = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float x = r *= 2.0F;
        float y = 0.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderUtil.glColor(c);
        GL11.glBegin(2);
        int ii = 0;
        while (ii < num_segments) {
            GL11.glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
            ii++;
        }
        GL11.glEnd();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        RenderUtil.glColor(c);
        GL11.glPopMatrix();
    }

    public static void rectangleBordered(double x, double y, double x1, double y1, double width, int internalColor,
                                         int borderColor) {
        RenderUtil.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtil.rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtil.rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtil.rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
        RenderUtil.rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float) (color >> 24 & 255) / 255.0f;
        float var6 = (float) (color >> 16 & 255) / 255.0f;
        float var7 = (float) (color >> 8 & 255) / 255.0f;
        float var8 = (float) (color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate((int) 770, (int) 771, (int) 1, (int) 0);
        GlStateManager.color((float) var6, (float) var7, (float) var8, (float) var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
    }

    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (float) (col1 >> 24 & 255) / 255.0f;
        float f1 = (float) (col1 >> 16 & 255) / 255.0f;
        float f2 = (float) (col1 >> 8 & 255) / 255.0f;
        float f3 = (float) (col1 & 255) / 255.0f;
        float f4 = (float) (col2 >> 24 & 255) / 255.0f;
        float f5 = (float) (col2 >> 16 & 255) / 255.0f;
        float f6 = (float) (col2 >> 8 & 255) / 255.0f;
        float f7 = (float) (col2 & 255) / 255.0f;
        GL11.glEnable((int) 3042);
        GL11.glDisable((int) 3553);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glEnable((int) 2848);
        GL11.glShadeModel((int) 7425);
        GL11.glPushMatrix();
        GL11.glBegin((int) 7);
        GL11.glColor4f((float) f1, (float) f2, (float) f3, (float) f);
        GL11.glVertex2d((double) left, (double) top);
        GL11.glVertex2d((double) left, (double) bottom);
        GL11.glColor4f((float) f5, (float) f6, (float) f7, (float) f4);
        GL11.glVertex2d((double) right, (double) bottom);
        GL11.glVertex2d((double) right, (double) top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int) 3553);
        GL11.glDisable((int) 3042);
        GL11.glDisable((int) 2848);
        GL11.glShadeModel((int) 7424);
    }

    public static void drawblock(double a, double a2, double a3, int a4, int a5, float a6) {
        float a7 = (float) (a4 >> 24 & 255) / 255.0f;
        float a8 = (float) (a4 >> 16 & 255) / 255.0f;
        float a9 = (float) (a4 >> 8 & 255) / 255.0f;
        float a10 = (float) (a4 & 255) / 255.0f;
        float a11 = (float) (a5 >> 24 & 255) / 255.0f;
        float a12 = (float) (a5 >> 16 & 255) / 255.0f;
        float a13 = (float) (a5 >> 8 & 255) / 255.0f;
        float a14 = (float) (a5 & 255) / 255.0f;
        org.lwjgl.opengl.GL11.glPushMatrix();
        org.lwjgl.opengl.GL11.glEnable((int) 3042);
        org.lwjgl.opengl.GL11.glBlendFunc((int) 770, (int) 771);
        org.lwjgl.opengl.GL11.glDisable((int) 3553);
        org.lwjgl.opengl.GL11.glEnable((int) 2848);
        org.lwjgl.opengl.GL11.glDisable((int) 2929);
        org.lwjgl.opengl.GL11.glDepthMask((boolean) false);
        org.lwjgl.opengl.GL11.glColor4f((float) a8, (float) a9, (float) a10, (float) a7);
        drawOutlinedBoundingBox(new AxisAlignedBB(a, a2, a3, a + 1.0, a2 + 1.0, a3 + 1.0));
        org.lwjgl.opengl.GL11.glLineWidth((float) a6);
        org.lwjgl.opengl.GL11.glColor4f((float) a12, (float) a13, (float) a14, (float) a11);
        drawOutlinedBoundingBox(new AxisAlignedBB(a, a2, a3, a + 1.0, a2 + 1.0, a3 + 1.0));
        org.lwjgl.opengl.GL11.glDisable((int) 2848);
        org.lwjgl.opengl.GL11.glEnable((int) 3553);
        org.lwjgl.opengl.GL11.glEnable((int) 2929);
        org.lwjgl.opengl.GL11.glDepthMask((boolean) true);
        org.lwjgl.opengl.GL11.glDisable((int) 3042);
        org.lwjgl.opengl.GL11.glPopMatrix();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawBorderedRect(float x2, float y2, float x22, float y22, float l1, int col1, int col2) {
        RenderUtil.drawRect(x2, y2, x22, y22, col2);
        float f2 = (float) (col1 >> 24 & 255) / 255.0f;
        float f22 = (float) (col1 >> 16 & 255) / 255.0f;
        float f3 = (float) (col1 >> 8 & 255) / 255.0f;
        float f4 = (float) (col1 & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glColor4f(f22, f3, f4, f2);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y22);
        GL11.glVertex2d(x22, y22);
        GL11.glVertex2d(x22, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x22, y2);
        GL11.glVertex2d(x2, y22);
        GL11.glVertex2d(x22, y22);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawBordRect(double x, double y, double x1, double y1, double width, int internalColor,
                                    int borderColor) {
        drawRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }


    public static void color(int color, float alpha) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static Vec3 interpolateRender(EntityPlayer player) {
        float part = Minecraft.getMinecraft().timer.renderPartialTicks;
        double interpX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) part;
        double interpY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) part;
        double interpZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) part;
        return new Vec3(interpX, interpY, interpZ);
    }

    public static void disableSmoothLine() {
        GL11.glEnable((int) 3553);
        GL11.glEnable((int) 2929);
        GL11.glDisable((int) 3042);
        GL11.glEnable((int) 3008);
        GL11.glDepthMask((boolean) true);
        GL11.glCullFace((int) 1029);
        GL11.glDisable((int) 2848);
        GL11.glHint((int) 3154, (int) 4352);
        GL11.glHint((int) 3155, (int) 4352);
    }

    public static void enableSmoothLine(float width) {
        GL11.glDisable((int) 3008);
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glDisable((int) 3553);
        GL11.glDisable((int) 2929);
        GL11.glDepthMask((boolean) false);
        GL11.glEnable((int) 2884);
        GL11.glEnable((int) 2848);
        GL11.glHint((int) 3154, (int) 4354);
        GL11.glHint((int) 3155, (int) 4354);
        GL11.glLineWidth((float) width);
    }

    public static void drawCylinderESP(EntityLivingBase entity, double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GL11.glTranslated((double) x, (double) y, (double) z);
        GL11.glRotatef((float) (-entity.width), (float) 0.0f, (float) 1.0f, (float) 0.0f);
        RenderUtil.glColor(new Color(1, 89, 1, 150).getRGB());// color4f
        RenderUtil.enableSmoothLine(0.1f);//
        Cylinder c = new Cylinder();
        GL11.glRotatef((float) -90.0f, (float) 1.0f, (float) 0.0f, (float) 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.0f, 0.2f, 0.5f, 5, 300);
        RenderUtil.disableSmoothLine();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslated((double) x, (double) y + 0.5f, (double) z);
        GL11.glRotatef((float) (-entity.width), (float) 0.0f, (float) 1.0f, (float) 0.0f);
        RenderUtil.glColor(new Color(2, 168, 2, 150).getRGB());// color4f
        RenderUtil.enableSmoothLine(0.1f);//
        GL11.glRotatef((float) -90.0f, (float) 1.0f, (float) 0.0f, (float) 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.2f, 0.0f, 0.5f, 5, 300);
        RenderUtil.disableSmoothLine();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, 1f, 1).getRGB();

    }

    public static void drawScaledRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height,
                                      float tileWidth, float tileHeight) {
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
    }

    public static void drawIcon(float x, float y, int sizex, int sizey, ResourceLocation resourceLocation) {
        GL11.glPushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glEnable((int) 3042);
        GL11.glBlendFunc((int) 770, (int) 771);
        GL11.glEnable((int) 2848);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glTranslatef((float) x, (float) y, (float) 0.0f);
        GL11.glEnable(GL_POLYGON_SMOOTH);
        GL11.glDisable(GL_POLYGON_SMOOTH);
        RenderUtil.drawScaledRect(0, 0, 0.0f, 0.0f, sizex, sizey, sizex, sizey, sizex, sizey);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.disableRescaleNormal();
        GL11.glDisable((int) 2848);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void doGlScissor(int x, int y, int width, int height) {
        int scaleFactor = 1;
        int k = Minecraft.getMinecraft().gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && Minecraft.getMinecraft().displayWidth / (scaleFactor + 1) >= 320
                && Minecraft.getMinecraft().displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (x * scaleFactor),
                (int) (Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor),
                (int) (width * scaleFactor), (int) (height * scaleFactor));
    }

}
