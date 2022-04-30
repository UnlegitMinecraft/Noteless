package net.ccbluex.liquidbounce.cn.Insane.newdropdown;

import cn.WbxMain;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.impl.SettingComponents;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.Animation;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.Direction;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.impl.DecelerateAnimation;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.impl.EaseBackIn;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.normal.Main;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.render.DrRenderUtils;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class DropdownClickGui extends GuiScreen {
    private Animation openingAnimation;
    private EaseBackIn fadeAnimation;
    private DecelerateAnimation configHover;
    private final ResourceLocation hudIcon = new ResourceLocation("liquidbounce/custom_hud_icon.png");
    private List<MainScreen> categoryPanels;

    @Override
    public void initGui() {
        if (categoryPanels == null || Main.reloadModules) {
            categoryPanels = new ArrayList() {{
                for (ModuleCategory category : ModuleCategory.values()) {
                    //
                    add(new MainScreen(category));
                }
            }};
            Main.reloadModules = false;
        }
        WbxMain.getInstance().getSideGui().initGui();
        fadeAnimation = new EaseBackIn(400, 1, 2f);
        openingAnimation = new EaseBackIn(400, .4f, 2f);
        configHover = new DecelerateAnimation(250, 1);

        for (MainScreen catPanels : categoryPanels) {
            catPanels.animation = fadeAnimation;
            catPanels.openingAnimation = openingAnimation;
            catPanels.initGui();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            openingAnimation.setDirection(Direction.BACKWARDS);
            WbxMain.getInstance().getSideGui().focused = false;
            fadeAnimation.setDirection(openingAnimation.getDirection());
        }
        WbxMain.getInstance().getSideGui().keyTyped(typedChar, keyCode);
        categoryPanels.forEach(categoryPanel -> categoryPanel.keyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.isButtonDown(0) && mouseX >= 5 && mouseX <= 50 && mouseY <= height - 5 && mouseY >= height - 50)
            mc.displayGuiScreen(new GuiHudDesigner());
        RenderUtils.drawImage(hudIcon, 9, height - 41, 32, 32);

        if (Main.reloadModules) {
            initGui();
        }
     //   if (Main.walk.isEnabled()) {
    //        InventoryMove.updateStates();
   //     }

        //If the closing animation finished then change the gui screen to null
        if (openingAnimation.isDone() && openingAnimation.getDirection().equals(Direction.BACKWARDS)) {
            mc.displayGuiScreen(null);
            return;
        }

        boolean focusedConfigGui = WbxMain.getInstance().getSideGui().focused;
        int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;
        ScaledResolution sr = new ScaledResolution(mc);

        boolean hoveringConfig = DrRenderUtils.isHovering(width - 120, height - 65, 75, 25, fakeMouseX, fakeMouseY);

        configHover.setDirection(hoveringConfig ? Direction.FORWARDS : Direction.BACKWARDS);
        int alphaAnimation = Math.max(0, Math.min(255, (int) (255 * fadeAnimation.getOutput())));

        GlStateManager.color(1, 1, 1, 1);

        SettingComponents.scale = (float) (openingAnimation.getOutput() + .6f);
        DrRenderUtils.scale(sr.getScaledWidth() / 2f, sr.getScaledHeight() / 2f, (float) openingAnimation.getOutput() + .6f, () -> {
            for (MainScreen catPanels : categoryPanels) {
                catPanels.drawScreen(fakeMouseX, fakeMouseY);
            }

            WbxMain.getInstance().getSideGui().drawScreen(mouseX, mouseY, partialTicks, alphaAnimation);
        });


    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean focused = WbxMain.getInstance().getSideGui().focused;
        WbxMain.getInstance().getSideGui().mouseClicked(mouseX, mouseY, mouseButton);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseClicked(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        boolean focused = WbxMain.getInstance().getSideGui().focused;
        WbxMain.getInstance().getSideGui().mouseReleased(mouseX, mouseY, state);
        if (!focused) {
            categoryPanels.forEach(cat -> cat.mouseReleased(mouseX, mouseY, state));
        }
    }

}
