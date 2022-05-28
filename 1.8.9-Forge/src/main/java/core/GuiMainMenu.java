/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package core;


import antiskidderobfuscator.NativeMethod;
import com.google.common.collect.Lists;
import net.ccbluex.liquidbounce.ui.client.GuiBackground;
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.MainMenuButton;
import net.ccbluex.liquidbounce.utils.RenderUtils;
import net.ccbluex.liquidbounce.utils.misc.DimplesUtils;
import net.ccbluex.liquidbounce.utils.misc.RandomUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class GuiMainMenu extends GuiScreen {

    private static final Random RANDOM = new Random();
    private final int astolfo;
    final float updateCounter;
    private String splashText;
    private int panoramaTimer;
    private final Object threadLock = new Object();
    private String openGLWarning1;
    private String openGLWarning2;
    private String openGLWarningLink;
    private static final ResourceLocation splashTexts = new ResourceLocation("Insane/splashes.txt");
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("liquidbounce/panorama/panorama_0.png"), new ResourceLocation("liquidbounce/panorama/panorama_1.png"), new ResourceLocation("liquidbounce/panorama/panorama_2.png"), new ResourceLocation("liquidbounce/panorama/panorama_3.png"), new ResourceLocation("liquidbounce/panorama/panorama_4.png"), new ResourceLocation("liquidbounce/panorama/panorama_5.png")};
    int field_92024_r;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundTexture;
    private boolean field_183502_L;
    private boolean got;
    private GuiScreen field_183503_M;

    public GuiMainMenu() {
        field_183502_L = false;
        splashText = "missingno";
        BufferedReader bufferedreader = null;

        try {

            List<String> list = Lists.newArrayList();
            bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));

            String s;
            while((s = bufferedreader.readLine()) != null) {
                s = s.trim();
                if (!s.isEmpty()) {
                    list.add(s);
                }
            }

            if (!list.isEmpty()) {
                do {
                    splashText = list.get(RANDOM.nextInt(list.size()));
                } while(splashText.hashCode() == 125780783);
            }
        } catch (IOException ignored) {
        } finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close();
                } catch (IOException ignored) {}
            }
        }

        astolfo = RandomUtils.nextInt(0, 4);
        updateCounter = RANDOM.nextFloat();
        openGLWarning1 = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            openGLWarning1 = I18n.format("title.oldgl1");
            openGLWarning2 = I18n.format("title.oldgl2");
            openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    private boolean func_183501_a() {
        return mc.gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && field_183503_M != null;
    }

    public void updateScreen() {
        ++panoramaTimer;

        if (func_183501_a()) field_183503_M.updateScreen();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) throws IOException {}
    @NativeMethod
    public void initGui() {
        // if(!got) {
        //       try {
        //         if (!WebUtils.get("https://gitee.com/shuimenglol/TEST/raw/master/testhwid.txt").contains(HWIDUtils.getHWID())) {
        //            new DimplesUtils().NMSL();  //HWID库不匹配触发后门
        //        }
        //    } catch (Throwable e) {
        //        e.printStackTrace();
        //    }
        //     got = true;
        //}
        DynamicTexture viewportTexture = new DynamicTexture(256, 256);
        backgroundTexture = mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);

        int j = height / 4 + 48;
        if (mc.isDemo()) addDemoButtons(j, 24);
        else addSingleplayerMultiplayerButtons();

        int defaultHeight = height / 2 - 8;

        buttonList.add(new MainMenuButton(0, width / 2 - 72, defaultHeight + 96, 140, 20, I18n.format("menu.options")));

        synchronized (threadLock) {
            int field_92023_s = fontRendererObj.getStringWidth(openGLWarning1);
            field_92024_r = fontRendererObj.getStringWidth(openGLWarning2);
            int k = Math.max(field_92023_s, field_92024_r);
            field_92022_t = (width - k) / 2;
            field_92021_u = (buttonList.get(0)).yPosition - 24;
            field_92020_v = field_92022_t + k;
            field_92019_w = field_92021_u + 24;
        }

        mc.setConnectedToRealms(false);

        if (mc.gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !field_183502_L) {
            RealmsBridge realmsbridge = new RealmsBridge();
            field_183503_M = realmsbridge.getNotificationScreen(this);
            field_183502_L = true;
        }

        if (func_183501_a()) {
            field_183503_M.setGuiSize(width, height);
            field_183503_M.initGui();
        }
    }

    private void addSingleplayerMultiplayerButtons() {
        int defaultHeight = height / 2 - 8;

        buttonList.add(new MainMenuButton(1, width / 2 - 72, defaultHeight, 140, 20, I18n.format("menu.singleplayer")));
        buttonList.add(new MainMenuButton(2, width / 2 - 72, defaultHeight + 24, 140, 20, I18n.format("menu.multiplayer")));
        buttonList.add(new MainMenuButton(3, width / 2 - 72, defaultHeight + 48, 140, 20, I18n.format("Account Manager")));
        buttonList.add(new MainMenuButton(4, width / 2 - 72, defaultHeight + 72, 140, 20, I18n.format("Background")));
    }

    private void addDemoButtons(int p_addDemoButtons_1_, int p_addDemoButtons_2_) {
        buttonList.add(new GuiButton(11, width / 2 - 100, p_addDemoButtons_1_, I18n.format("menu.playdemo")));
        GuiButton buttonResetDemo;
        buttonList.add(buttonResetDemo = new GuiButton(12, width / 2 - 100, p_addDemoButtons_1_ + p_addDemoButtons_2_, I18n.format("menu.resetdemo")));
        ISaveFormat isaveformat = mc.getSaveLoader();
        WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
        if (worldinfo == null) buttonResetDemo.enabled = false;
    }

    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        if (p_actionPerformed_1_.id == 1) mc.displayGuiScreen(new GuiSelectWorld(this));
        if (p_actionPerformed_1_.id == 2) mc.displayGuiScreen(new GuiMultiplayer(this));
        if (p_actionPerformed_1_.id == 3) mc.displayGuiScreen(new GuiAltManager(this));
        if (p_actionPerformed_1_.id == 4) mc.displayGuiScreen(new GuiBackground(this));
        if (p_actionPerformed_1_.id == 0) mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));

        //if (p_actionPerformed_1_.id == 777) mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
        if (p_actionPerformed_1_.id == 666) mc.shutdown();
        if (p_actionPerformed_1_.id == 11) mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        if (p_actionPerformed_1_.id == 12) {
            ISaveFormat isaveformat = mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if (worldinfo != null) {
                GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
                mc.displayGuiScreen(guiyesno);
            }
        }
    }
    @NativeMethod
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableAlpha();
        renderSkybox(mouseX, mouseY, partialTicks);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(width / 2 + 90), 70.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
        f = f * 100.0F / (float)(fontRendererObj.getStringWidth(splashText) + 32);
        GlStateManager.scale(f, f, f);
        GlStateManager.popMatrix();

        final ArrayList<String> changes = new ArrayList();
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());


        changes.add("");
        changes.add("Build 220525");
        changes.add("+Improve Disabler");
        changes.add("+Improve Strafe");

        if (sr.getScaledWidth() > 600 && sr.getScaledHeight() > 300) {
            net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SF.SF_20.SF_20.drawString("SkidLog:", 5, 5, new Color(255, 255, 255, 220).hashCode());

            for (int i = 0; i < changes.size(); i++) {
                net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.Fonts.SF.SF_20.SF_20.drawString(changes.get(i), 5, 16 + i * 12, new Color(255, 255, 255, 220).hashCode());
            }
        }

        RenderUtils.drawmage(new ResourceLocation("liquidbounce/astolfos/logo.png"), width / 2 - 50, height / 2 - 144, 96, 96, 1);
        final int[] imageWidth = {230, 250, 400};
        final int[] imageHeight = {312, 353, 422, 305, 242};
        int imageX, imageY;
        switch (astolfo) {
            case 0:
                imageX = imageWidth[0];
                imageY = imageHeight[0];
                break;
            case 1:
                imageX = imageWidth[1];
                imageY = imageHeight[1];
                break;
            case 2:
                imageX = imageWidth[0];
                imageY = imageHeight[2];
                break;
            case 3:
                imageX = imageWidth[2];
                imageY = imageHeight[3];
                break;
            case 4:
                imageX = imageWidth[0];
                imageY = imageHeight[4];
                break;
            default: {
                imageX = 0;
                imageY = 0;
                break;
            }
        }
        RenderUtils.drawmage(new ResourceLocation("liquidbounce/astolfos/" + astolfo + ".png"), width - imageX / 3, height - imageY / 3, imageX / 3, imageY / 3, 0.5f);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Fonts.SFUI35.drawString("Welcome, User", width - Fonts.SFUI35.getStringWidth("Welcome, User") - 4f, height - 12f, new Color(255, 255, 255, 200).getRGB(), true);
        Fonts.SFUI35.drawString("Noteless Client(#Build 220525)", 4f, height - 12f, new Color(255, 255, 255, 200).getRGB(), true);

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (func_183501_a()) field_183503_M.drawScreen(mouseX, mouseY, partialTicks);
    }
    private void drawPanorama(int p_drawPanorama_1_, int p_drawPanorama_2_, float p_drawPanorama_3_) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = 8;

        for(int j = 0; j < i * i; ++j) {
            GlStateManager.pushMatrix();
            float f = ((float)(j % i) / (float)i - 0.5F) / 64.0F;
            float f1 = ((float)(j / i) / (float)i - 0.5F) / 64.0F;
            float f2 = 0.0F;
            GlStateManager.translate(f, f1, f2);
            GlStateManager.rotate(MathHelper.sin(((float)panoramaTimer + p_drawPanorama_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-((float)panoramaTimer + p_drawPanorama_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int k = 0; k < 6; ++k) {
                GlStateManager.pushMatrix();
                if (k == 1) GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                if (k == 2) GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                if (k == 3) GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                if (k == 4) GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                if (k == 5) GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);

                mc.getTextureManager().bindTexture(titlePanoramaPaths[k]);
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int l = 255 / (j + 1);
                float f3 = 0.0F;
                worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
                worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
                worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
                worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }
    private void rotateAndBlurSkybox(float p_rotateAndBlurSkybox_1_) {
        mc.getTextureManager().bindTexture(backgroundTexture);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int i = 3;

        for(int j = 0; j < i; ++j) {
            float f = 1.0F / (float)(j + 1);
            int k = width;
            int l = height;
            float f1 = (float)(j - i / 2) / 256.0F;
            worldrenderer.pos(k, l, zLevel).tex((0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(k, 0.0D, zLevel).tex((1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, 0.0D, zLevel).tex((1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            worldrenderer.pos(0.0D, l, zLevel).tex((0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox(int p_renderSkybox_1_, int p_renderSkybox_2_, float p_renderSkybox_3_) {
        mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        drawPanorama(p_renderSkybox_1_, p_renderSkybox_2_, p_renderSkybox_3_);
        rotateAndBlurSkybox(p_renderSkybox_3_);
        rotateAndBlurSkybox(p_renderSkybox_3_);
        rotateAndBlurSkybox(p_renderSkybox_3_);
        rotateAndBlurSkybox(p_renderSkybox_3_);
        rotateAndBlurSkybox(p_renderSkybox_3_);
        rotateAndBlurSkybox(p_renderSkybox_3_);
        //rotateAndBlurSkybox(p_renderSkybox_3_);
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
        float f = width > height ? 120.0F / (float)width : 120.0F / (float)height;
        float f1 = (float)height * f / 256.0F;
        float f2 = (float)width * f / 256.0F;
        int i = width;
        int j = height;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, j, zLevel).tex((0.5F - f1), (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(i, j, zLevel).tex((0.5F - f1), (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(i, 0.0D, zLevel).tex((0.5F + f1), (0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        worldrenderer.pos(0.0D, 0.0D, zLevel).tex((0.5F + f1), (0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) throws IOException {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
        synchronized(threadLock) {
            if (openGLWarning1.length() > 0 && p_mouseClicked_1_ >= field_92022_t && p_mouseClicked_1_ <= field_92020_v && p_mouseClicked_2_ >= field_92021_u && p_mouseClicked_2_ <= field_92019_w) {
                GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, openGLWarningLink, 13, true);
                guiconfirmopenlink.disableSecurityWarning();
                mc.displayGuiScreen(guiconfirmopenlink);
            }
        }
    }

    public void onGuiClosed() {
        if (field_183503_M != null) {
            field_183503_M.onGuiClosed();
        }
    }
}
