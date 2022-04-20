package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.RenderU;
import net.ccbluex.liquidbounce.utils.misc.QQUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;

import static org.lwjgl.opengl.GL11.*;
@ElementInfo(name = "QQLogo")
public class QQLogo extends Element {
    private boolean got = false;

    protected Border draw() {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        glColor4f(1, 1, 1, 1);
        try {
            if (!got) {
                mc.getTextureManager().loadTexture(
                        new ResourceLocation("sb"),
                        new DynamicTexture(ImageIO.read(new URL("https://q.qlogo.cn/headimg_dl?dst_uin=" + QQUtils.QQNumber + "&spec=640&img_type=png"))));
                        got = true;
            }
        }catch(final Throwable throwable) {

        }
        mc.getTextureManager().bindTexture(new ResourceLocation("sb"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 60, 60, 60, 60);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        final int color = new Color(45,45,45).getRGB();
        for(float i = 26;i <= 42; i += 1) {
            RenderU.drawOutFullCircle(31.5F, 30, i, color, 5);
        }
        RenderU.drawOutFullCircle(31.5F, 30, 27, new Color(53,141,204).getRGB(), 2);
        RenderU.drawGradientSideways(60F, 30F, 180F, 45F, new Color(45,45,45, 255).getRGB(), new Color(45,45,45, 0).getRGB());
        RenderU.drawOutFullCircle(31.5F, 30, 44, new Color(0,230,0).getRGB(), 3,-7F, 320F);
        RenderU.drawGradientSideways(60F, 1F, 200F, 26.5F, new Color(45,45,45, 255).getRGB(), new Color(45,45,45, 0).getRGB());
       Fonts.SFUI35.drawString(mc.getSession().getUsername() + " | " + Math.round(mc.thePlayer.getHealth()) + "hp", 80F, 10F, new Color(200, 200, 200).getRGB());
        Fonts.SFUI35.drawString(String.valueOf(mc.thePlayer.getFoodStats().getFoodLevel()), 90F, 34F, -1, false);
        Fonts.SFUI35.drawString("Food", 105F, 34F, new Color(236,161,4).getRGB(), false);
        return new Border(0F, 0F, 120F, 30F);
    }


    @Override
    public Border drawElement() {

        return draw();
    }
}