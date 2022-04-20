/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 *
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.utils.render;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import org.lwjgl.opengl.GL11;

public class BlurUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    private static ShaderGroup shaderGroup;
    private static Framebuffer frbuffer;

    private static Framebuffer framebuffer, frameBuffer;

    private static int lastFactor;
    private static int lastWidth;
    private static int lastHeight;

    private static float lastX;
    private static float lastY;
    private static float lastW;
    private static float lastH;

    private static float lastStrength = 5F;

    //private static ResourceLocation blurShader = new ResourceLocation("liquidbounce+/mahiro.json");
    private static ResourceLocation blurShader = new ResourceLocation("shaders/post/blurArea.json");

    public static void init() {
        try {
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), blurShader);
            shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            framebuffer = shaderGroup.mainFramebuffer;
            frbuffer = shaderGroup.getFramebufferRaw("result");
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    /*private static void setValues(float strength) {
        if (strength == lastStrength) return;
        lastStrength = strength;
        for (int i = 0; i < 2; i++) {
            shaderGroup.listShaders.get(i).getShaderManager().getShaderUniform("Radius").set(strength);
        }
    }*/

    private static void setValues(float strength, float x, float y, float w, float h, float width, float height) {
        if (strength == lastStrength && lastX == x && lastY == y && lastW == w && lastH == h) return;
        lastStrength = strength;
        lastX = x;
        lastY = y;
        lastW = w;
        lastH = h;

        for (int i = 0; i < 2; i++) {
            shaderGroup.listShaders.get(i).getShaderManager().getShaderUniform("Radius").set(strength);
            shaderGroup.listShaders.get(i).getShaderManager().getShaderUniform("BlurXY").set(x, height - y - h);
            shaderGroup.listShaders.get(i).getShaderManager().getShaderUniform("BlurCoord").set(w, h);
        }
    }
    /*
        public static void downscale(boolean start, int strength) {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final int scaleFactor = scaledResolution.getScaleFactor();
            final int width = scaledResolution.getScaledWidth();
            final int height = scaledResolution.getScaledHeight();

            if (frameBuffer == null) {
                frameBuffer = new Framebuffer(mc.displayWidth / strength, mc.displayHeight / strength, true);
                frameBuffer.setFramebufferColor(0, 0, 0, 0);
                frameBuffer.setFramebufferFilter(GL11.GL_LINEAR);
            }

            if (sizeHasChanged(scaleFactor, width, height) || strength != lastWeight) {
                frameBuffer.createBindFramebuffer(mc.displayWidth / strength, mc.displayHeight / strength);
                frameBuffer.setFramebufferColor(0, 0, 0, 0);
                frameBuffer.setFramebufferFilter(GL11.GL_LINEAR);
            }

            lastFactor = scaleFactor;
            lastWidth = width;
            lastHeight = height;
            lastWeight = strength;

            if (start) {
                frameBuffer.framebufferClear();
                frameBuffer.bindFramebuffer(false);
            } else {
                frameBuffer.unbindFramebuffer();
                mc.getFramebuffer().bindFramebuffer(true);
                //frameBuffer.bindFramebuffer(true);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                if (OpenGlHelper.isFramebufferEnabled())
                {
                    GlStateManager.disableDepth();
                    GlStateManager.depthMask(false);
                    GlStateManager.colorMask(true, true, true, false);
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableLighting();
                    GlStateManager.disableAlpha();

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    frameBuffer.bindFramebufferTexture();
                    float f = (float)width;
                    float f1 = (float)height;
                    float f2 = (float)frameBuffer.framebufferWidth / (float)frameBuffer.framebufferTextureWidth;
                    float f3 = (float)frameBuffer.framebufferHeight / (float)frameBuffer.framebufferTextureHeight;
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                    worldrenderer.pos(0.0D, (double)f1, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
                    worldrenderer.pos((double)f, (double)f1, 0.0D).tex((double)f2, 0.0D).color(255, 255, 255, 255).endVertex();
                    worldrenderer.pos((double)f, 0.0D, 0.0D).tex((double)f2, (double)f3).color(255, 255, 255, 255).endVertex();
                    worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)f3).color(255, 255, 255, 255).endVertex();
                    tessellator.draw();
                    frameBuffer.unbindFramebufferTexture();
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(true);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableAlpha();
                }
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
            }
        }
    */
    public static void blurArea(float x, float y, float x2, float y2, float blurStrength) {
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        if (x > x2) {
            float z = x;
            x = x2;
            x2 = z;
        }

        if (y > y2) {
            float z = y;
            y = y2;
            y2 = y;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || frbuffer == null || shaderGroup == null) {
            init();
        }

        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        float _w = x2 - x;
        float _h = y2 - y;

        //setValues(blurStrength);
        setValues(blurStrength, x, y, _w, _h, width, height);

        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        Stencil.write(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtils.quickDrawRect(x, y, x2, y2);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        Stencil.erase(true);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.pushMatrix();
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        frbuffer.bindFramebufferTexture();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = (float)width;
        float f1 = (float)height;
        float f2 = (float)frbuffer.framebufferWidth / (float)frbuffer.framebufferTextureWidth;
        float f3 = (float)frbuffer.framebufferHeight / (float)frbuffer.framebufferTextureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)f1, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, (double)f1, 0.0D).tex((double)f2, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, 0.0D, 0.0D).tex((double)f2, (double)f3).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)f3).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        frbuffer.unbindFramebufferTexture();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        Stencil.dispose();

        GlStateManager.enableAlpha();
    }

    /*public static void preCustomBlur(float blurStrength) {
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || frbuffer == null || shaderGroup == null) {
            init();
        }

        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        setValues(blurStrength);

        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        GlStateManager.pushMatrix();
        Stencil.write(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }*/

    public static void preCustomBlur(float blurStrength, float x, float y, float x2, float y2, boolean renderClipLayer) {
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        if (x > x2) {
            float z = x;
            x = x2;
            x2 = z;
        }

        if (y > y2) {
            float z = y;
            y = y2;
            y2 = y;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();


        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || frbuffer == null || shaderGroup == null) {
            init();
        }

        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        float _w = x2 - x;
        float _h = y2 - y;

        setValues(blurStrength, x, y, _w, _h, width, height);

        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        Stencil.write(renderClipLayer);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    public static void postCustomBlur() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        Stencil.erase(true);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.pushMatrix();
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        frbuffer.bindFramebufferTexture();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = (float)width;
        float f1 = (float)height;
        float f2 = (float)frbuffer.framebufferWidth / (float)frbuffer.framebufferTextureWidth;
        float f3 = (float)frbuffer.framebufferHeight / (float)frbuffer.framebufferTextureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)f1, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, (double)f1, 0.0D).tex((double)f2, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, 0.0D, 0.0D).tex((double)f2, (double)f3).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)f3).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        frbuffer.unbindFramebufferTexture();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        Stencil.dispose();
        GlStateManager.enableAlpha();
    }

    public static void blurAreaRounded(float x, float y, float x2, float y2, float rad, float blurStrength) {
        if (!OpenGlHelper.isFramebufferEnabled()) return;

        if (x > x2) {
            float z = x;
            x = x2;
            x2 = z;
        }

        if (y > y2) {
            float z = y;
            y = y2;
            y2 = y;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || frbuffer == null || shaderGroup == null) {
            init();
        }

        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        //setValues(blurStrength);
        float _w = x2 - x;
        float _h = y2 - y;

        setValues(blurStrength, x, y, _w, _h, width, height);

        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        Stencil.write(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtils.fastRoundedRect(x, y, x2, y2, rad);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        Stencil.erase(true);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.pushMatrix();
        GlStateManager.colorMask(true, true, true, false);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        frbuffer.bindFramebufferTexture();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f = (float)width;
        float f1 = (float)height;
        float f2 = (float)frbuffer.framebufferWidth / (float)frbuffer.framebufferTextureWidth;
        float f3 = (float)frbuffer.framebufferHeight / (float)frbuffer.framebufferTextureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)f1, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, (double)f1, 0.0D).tex((double)f2, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, 0.0D, 0.0D).tex((double)f2, (double)f3).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)f3).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        frbuffer.unbindFramebufferTexture();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        Stencil.dispose();

        GlStateManager.enableAlpha();
    }

    private static boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return lastFactor != scaleFactor || lastWidth != width || lastHeight != height;
    }
}


