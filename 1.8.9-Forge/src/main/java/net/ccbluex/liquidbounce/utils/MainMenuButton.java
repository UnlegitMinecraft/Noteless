/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class MainMenuButton extends GuiButton {

    public MainMenuButton(final int buttonId, final int x, final int y, final int width, final int height, final String buttonText) {
        super(buttonId, x, y, width, height, buttonText);
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            Color color = new Color(180, 180, 180);
            FontRenderer fontrenderer = Fonts.SFUI35;
            hovered = (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height);

            RenderUtils.drawRoundedRect((double) xPosition, yPosition, xPosition + width, yPosition + height, 6, new Color(1,1,1,80).getRGB());

            if (hovered) {
                GL11.glPushMatrix();
                RenderUtils.color(color.darker().getRGB());
                GL11.glPopMatrix();
            }

            mouseDragged(mc, mouseX, mouseY);
            int stringColor = new Color(255,255,255,200).getRGB();

            if (hovered)
                stringColor = color.darker().getRGB();

            drawCenteredString(fontrenderer, displayString.toUpperCase(), xPosition + width / 2, yPosition + (height - 6) / 2, stringColor);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {}
}
