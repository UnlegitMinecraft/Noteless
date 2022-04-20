package core;

import antiskidderobfucate.NativeMethod;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.io.IOException;

public final class GuiChatLogin extends GuiScreen {
    private final GuiScreen parentScreen;

    private GuiTextField userNameTextField;
    private GuiTextField passwordTextField;

    private GuiButton loginButton;

    private Thread chatLoginThread;

    private volatile String statusText = "等待中...";

    public GuiChatLogin(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }
    @NativeMethod
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final float wMid = width / 2.0f;
        final float hMid = height / 2.0f;
        RenderUtils.drawBorderedRect((float) wMid - 120,(float) hMid - 70,(float) wMid + 120,(float) hMid + 70,1,new Color(100,255,100).getRGB(),new Color(0,0,0,100).getRGB());
        mc.fontRendererObj.drawString("IRC Login",wMid -  mc.fontRendererObj.getStringWidth("IRC Login") / 2.0f,hMid - 68,-1,true);
        mc.fontRendererObj.drawString(statusText,wMid -  mc.fontRendererObj.getStringWidth(statusText) / 2.0f,hMid - 58,-1,true);

        userNameTextField.drawTextBox();
        passwordTextField.drawTextBox();

        if (!userNameTextField.isFocused() && StringUtils.isNullOrEmpty(userNameTextField.getText())) {
            mc.fontRendererObj.drawStringWithShadow("UserName",(float) wMid - 114,(float) hMid - 24,new Color(100,100,100).getRGB());
        }

        if (!passwordTextField.isFocused() && StringUtils.isNullOrEmpty(passwordTextField.getText())) {
            mc.fontRendererObj.drawStringWithShadow("Password",(float) wMid - 114,(float) hMid + 6,new Color(100,100,100).getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        userNameTextField.textboxKeyTyped(typedChar,keyCode);
        passwordTextField.textboxKeyTyped(typedChar,keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        userNameTextField.mouseClicked(mouseX,mouseY,mouseButton);
        passwordTextField.mouseClicked(mouseX,mouseY,mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        userNameTextField.updateCursorCounter();
        passwordTextField.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 0) {
            if (userNameTextField.getText() == null || userNameTextField.getText().isEmpty()) {
                statusText = "Username cannot be empty";
                return;
            }

            if (passwordTextField.getText() == null || passwordTextField.getText().isEmpty()) {
                statusText = "Password cannot be empty";
                return;
            }

            statusText = "登录中...";
            loginButton.enabled = false;

            if (chatLoginThread != null) {
                chatLoginThread.interrupt();
                chatLoginThread = null;
            }

            chatLoginThread.start();
        } else if (button.id == 1) {
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (chatLoginThread != null) {
            if (!chatLoginThread.isInterrupted()) {
                chatLoginThread.interrupt();
            }
            chatLoginThread = null;
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        final int wMid = width / 2;
        final int hMid = height / 2;
        buttonList.add(loginButton = new GuiButton(0,wMid - 118,hMid + 48,60,20,"登录"));
        buttonList.add(new GuiButton(1,wMid + 58,hMid + 48,60,20,"返回"));

        userNameTextField = new GuiTextField(0,mc.fontRendererObj,wMid - 116,hMid - 30,233,20);
        passwordTextField = new GuiTextField(1,mc.fontRendererObj,wMid - 116,hMid,233,20);
    }
}
