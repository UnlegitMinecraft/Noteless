package core.Verify;




import antiskidderobfuscator.NativeMethod;
import cn.WbxMain;
import com.google.gson.JsonObject;
import core.GuiMainMenu;
import core.Insane.HydraButton;
import core.textbox.UIDField;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.features.special.AntiForge;
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof;
import net.ccbluex.liquidbounce.script.remapper.Remapper;
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.*;
import net.ccbluex.liquidbounce.utils.math.Base58;
import net.ccbluex.liquidbounce.utils.misc.QQUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.json.JSONObject;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import oshi.SystemInfo;
import oshi.hardware.Processor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;


public class GuiLogin extends GuiScreen {

    private String status;


    private long ticks;

    private boolean launched = true;
    private boolean verify = true;
    public static boolean login = false;
    private boolean darkTheme = false;
    private boolean falseError;
    private String token = null;
    private float fraction;
    public int alpha = 0;
    public static boolean NMSL = false;
    private int  hwidy = 65;

    private long initTime = System.currentTimeMillis(); // Initialize as a failsafe

    private final Color blackish = new Color(20, 23, 26);
    private final Color black = new Color(40, 46, 51);
    private final Color blueish = new Color(0, 150, 135);
    private final Color blue = new Color(0xFF2B71B1);

    public static String uid;
    static float timeDelta = -1000;
    public void checkTime() {
        try {
            if (timeDelta < -999) {
                long time = Long.parseLong(HttpUtil.performGetRequest(new URL("https://time.is/t/?zh.0.347.2464.0p.480.43d.1574683214663.1594044507830.")).substring(0, 13));
                float delta = (System.currentTimeMillis() - time) / 1000f;
                System.out.println("Sync time! " + delta);
                timeDelta = delta;
            }
        } catch (Throwable e) {
            status = "Warning!Failed to check system time!";
            button.enabled = true;
            e.printStackTrace();
        }

        if (Math.abs(timeDelta) > 10) {
            status = "You may not be able to log in! The system time is not synchronized, please synchronize the time! (" + timeDelta + "s)";
            button.enabled = true;
        }
    }
    public GuiLogin() {
        status = "Idle";
        initTime = System.currentTimeMillis();
    }

    @Override
    public void initGui() {
        buttonList.add(button);
        buttonList.add(hwid);
        field = new UIDField(1, mc.fontRendererObj, (int) hWidth - 70, (int) hHeight - 35, 140, 30, "idk");
        alpha = 100;
        new Thread() {
            @Override
            public void run() {
                checkTime();
            }
        }.start();
        darkTheme = true;
        super.initGui();
    }

    private float hHeight = 540;
    private float hWidth = 960;
    private float errorBoxHeight = 0;

    HydraButton button = new HydraButton(0, (int) hWidth - 70, (int) (hHeight + 5), 140, 30, "Log In");
    HydraButton hwid = new HydraButton(1, (int) hWidth - 70, (int) (hHeight - hwidy), 140, 30, "Copy Hwid");

    UIDField field;
    @NativeMethod
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableCull();
        drawBackground(0);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);


        if (launched && darkTheme && fraction != 1.0049993F) {
            fraction = 1.0049993F;
        }

        if (darkTheme && fraction < 1) {
            fraction += 0.015;
        } else if (!darkTheme && fraction > 0) {
            fraction -= 0.015;
        }

        if (mouseX <= 20 && mouseY <= 20 && alpha < 255) {
            alpha++;
        } else if (alpha > 100) {
            alpha--;
        }

        Color whiteish = new Color(0xFFF4F5F8);
        Color white = Color.WHITE;
        Color shitGray = new Color(150, 150, 150);

        button.setColor(interpolateColor(
                button.hovered(mouseX, mouseY) ? blue.brighter() : blue,
                button.hovered(mouseX, mouseY) ? blueish.brighter() : blueish,
                fraction));
        field.setColor(interpolateColor(white, black, fraction));
        field.setTextColor(interpolateColor(shitGray, white, fraction));
        hwid.setColor(interpolateColor(
                hwid.hovered(mouseX, mouseY) ? blue.brighter() : blue,
                hwid.hovered(mouseX, mouseY) ? blueish.brighter() : blueish,
                fraction));

        ScaledResolution scaledResolution = new ScaledResolution(mc);
        button.updateCoordinates(hWidth - 70, hHeight + 5);
        hwid.updateCoordinates(hWidth - 70, hHeight -hwidy);
        field.updateCoordinates(hWidth - 70, hHeight - 35);
        int scaledWidthScaled = scaledResolution.getScaledWidth();
        int scaledHeightScaled = scaledResolution.getScaledHeight();

        hHeight = hHeight + (scaledHeightScaled / 2 - hHeight) * 0.02f;
        hWidth = scaledWidthScaled / 2;

        RenderUtils.drawRect(0, 0, scaledWidthScaled, scaledHeightScaled, new Color(0,0,0,150).getRGB());

        Color vis = new Color(interpolateColor(blue, blueish, fraction));

        Fonts.ICONFONT_50.drawString("M", 5, 5, new Color(vis.getRed(), vis.getGreen(), vis.getBlue(), alpha).getRGB());



        // LOGO
        Fonts.sbcnm.drawString(
                "Noteless",
                hWidth - Fonts.sbcnm.getStringWidth("Noteless") / 2 + 12,
                hHeight - 90,
                interpolateColor(blue, blueish, fraction));
        Fonts.ICONFONT_50.drawString("L", hWidth - 95, hHeight - 90, interpolateColor(blue, blueish, fraction));


        // LOG IN BUTTON
        button.drawButton(mc, mouseX, mouseY);
        hwid.drawButton(mc, mouseX, mouseY);

        //STATUS
        if (status.startsWith("Idle") || status.startsWith("Initializing") || status.startsWith("Logging")) {
            Fonts.font40.drawString(status, hWidth - Fonts.font40.getStringWidth(status) / 2, hHeight + 45, interpolateColor(new Color(150, 150, 150), white, fraction));
            errorBoxHeight = 0;
        } else {
            if (status.equals("Success")) {
                errorBoxHeight = errorBoxHeight + (10 - errorBoxHeight) * 0.01f;
                RenderUtils.drawBorderedRect(hWidth - Fonts.font40.getStringWidth(status) / 2 - 10, errorBoxHeight, hWidth + Fonts.font40.getStringWidth(status) / 2 + 10, errorBoxHeight + 12, 1f, new Color(170, 253, 126).getRGB(), interpolateColor(new Color(232, 255, 213), new Color(232, 255, 213).darker().darker(), fraction));
                Fonts.font40.drawString(status, hWidth - Fonts.font40.getStringWidth(status) / 2, errorBoxHeight + 7 - Fonts.font40.getHeight() / 2, new Color(201, 255, 167).darker().getRGB(), true);
            } else {
                errorBoxHeight = errorBoxHeight + (10 - errorBoxHeight) * 0.01f;
                RenderUtils.drawBorderedRect(hWidth - Fonts.font40.getStringWidth(status) / 2 - 10, errorBoxHeight, hWidth + Fonts.font40.getStringWidth(status) / 2 + 10, errorBoxHeight + 12, 1f, 0xFFF5DAE1, interpolateColor(new Color(0xFFF8E5E8), new Color(0xFFF8E5E8).darker().darker(), fraction));
                Fonts.font40.drawString(status, hWidth - Fonts.font40.getStringWidth(status) / 2, errorBoxHeight + 7 - Fonts.font40.getHeight() / 2, 0XFFEB6E85, true);
            }
        }

        // UID TEXTBOX
        field.drawTextBox();

        // CREDITS
        Fonts.SFUI35.drawString("Protected By DimplesAntiLeak", hWidth - Fonts.SFUI35.getStringWidth("Protected By DimplesAntiLeak") / 2, scaledHeightScaled - Fonts.SFUI35.getHeight() - 4, new Color(150, 150, 150).getRGB());



        if (falseError) {
            try {
                ScaledResolution sr = new ScaledResolution(mc);
                mouseClicked(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 20, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            falseError = false;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (field.isFocused() && keyCode >= 2 && keyCode <= 11 || keyCode == 14 /* number check */) {
            field.textboxKeyTyped(typedChar, keyCode);
        }

        if (keyCode == 64) {
            mc.displayGuiScreen(this);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        field.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    @NativeMethod
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if (button.id == 0) {

            button.enabled = false;
            try {
                Class clazz = Class.forName("java.lang.ProcessEnvironment");
                Field field2 = clazz.getDeclaredField("theUnmodifiableEnvironment");
                field2.setAccessible(true);
                Map<String, String> map = (Map<String, String>) field2.get(clazz);
                Processor[] processor = (new SystemInfo()).getHardware().getProcessors();
                String a = new Base58(14513).encode((map.get("PROCESSOR_IDENTIFIER") + map.get("LOGNAME") + map.get("USER")).getBytes());
                String b = new Base58(13132).encode((processor[0].getName() + processor.length + map.get("PROCESSOR_LEVEL") + a).getBytes());
                String c = new Base58(23241).encode((map.get("COMPUTERNAME") + System.getProperty("user.name") + b).getBytes());
                MessageDigest mdsha1 = MessageDigest.getInstance("SHA-1");
                byte[] sha1hash;
                mdsha1.update(Base64.getEncoder().encodeToString((a + b + c).getBytes()).getBytes("iso-8859-1"), 0, Base64.getEncoder().encodeToString((a + b + c).getBytes()).length());
                sha1hash = mdsha1.digest();
                final StringBuffer buf = new StringBuffer();
                for (int i = 0; i < sha1hash.length; ++i) {
                    int halfbyte = sha1hash[i] >>> 3 & 0xF;
                    int two_halfs = 0;
                    do {
                        if (halfbyte >= 0 && halfbyte <= 9) {
                            buf.append((char) (48 + halfbyte));
                        } else {
                            buf.append((char) (97 + (halfbyte - 10)));
                        }
                        halfbyte = (sha1hash[i] & 0xF);
                    } while (two_halfs++ < 1);
                }
                String hwid = buf.toString();

                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(hwid.getBytes());
                StringBuffer hexString = new StringBuffer();

                byte[] byteData = md.digest();

                for (byte aByteData : byteData) {
                    String hex = Integer.toHexString(0xff & aByteData);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                String origin = new Base58(14514).encode((hwid + hexString).getBytes());
                StringBuffer buffer = new StringBuffer();
                for (int i = 16; i < 16 + 5 * 4; i += 5) {
                    buffer.append(origin, i, i + 5);
                    buffer.append('-');
                }
                buffer.deleteCharAt(buffer.length() - 1);

                if (!origin.equalsIgnoreCase(origin.toUpperCase()))
                    LiquidBounce.moduleManager = null;

                if (!origin.toUpperCase().toLowerCase().equals(origin.toLowerCase()))
                    LiquidBounce.moduleManager = null;

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String text = "imagine cracking noteless";
                try {
                    String.class.getMethods()[76].getName();
                } catch (Throwable throwable) {
                    text = buffer.toString().toUpperCase();
                }
                Transferable trans = new StringSelection(text);
                clipboard.setContents(trans, null);

                status = "Logging in";
                token=text;
                if (verify()) {
                    status = "Success";
                    uid = field.getText();
                    NMSL = true;
                    //验证通过后加载功能
                    // Register listeners
                    LiquidBounce.eventManager.registerListener(new RotationUtils());
                    LiquidBounce.eventManager.registerListener(new AntiForge());
                    LiquidBounce.eventManager.registerListener(new BungeeCordSpoof());
                    LiquidBounce.eventManager.registerListener(new InventoryUtils());

                    // Setup module manager and register modules 注册功能
                    LiquidBounce.moduleManager.registerModules();

                    // Remapper js系统
                    try {
                        Remapper.INSTANCE.loadSrg();
                        // ScriptManager
                        LiquidBounce.scriptManager.loadScripts();
                        LiquidBounce.scriptManager.enableScripts();
                    } catch (Exception e) {
                        ClientUtils.getLogger().error("Failed to load scripts.", e);
                        e.printStackTrace();
                    }

                    LiquidBounce.fileManager.loadConfigs(
                            LiquidBounce.fileManager.modulesConfig, LiquidBounce.fileManager.valuesConfig, LiquidBounce.fileManager.accountsConfig,
                            LiquidBounce.fileManager.friendsConfig, LiquidBounce.fileManager.xrayConfig, LiquidBounce.fileManager.shortcutsConfig);
                    // Register commands 注册指令
                    LiquidBounce.commandManager.registerCommands();


                    // ClickGUI
                    LiquidBounce.clickGui =new ClickGui();
                    LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.clickGuiConfig);
                    mc.displayGuiScreen(new GuiMainMenu());
                    login = true;
                            Display.setTitle("Noteless 220515");
                            WbxMain.version = "Build 220515";
                } else if (field.getText().isEmpty()){
                    status = "User ID Empty";
                    button.enabled = true;
                }

                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1F));
            } catch (Throwable t) {
                t.printStackTrace();

                if (t.getMessage().contains("ConcurrentModificationException")) {
                    falseError = false;
                }

                status = t.getMessage();
                button.enabled = true;
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), -1F));
            }

        }
        if (button.id == 1){
            try {
                Class clazz = Class.forName("java.lang.ProcessEnvironment");
                Field field2 = clazz.getDeclaredField("theUnmodifiableEnvironment");
                field2.setAccessible(true);
                Map<String, String> map = (Map<String, String>) field2.get(clazz);
                Processor[] processor = (new SystemInfo()).getHardware().getProcessors();
                String a = new Base58(14513).encode((map.get("PROCESSOR_IDENTIFIER") + map.get("LOGNAME") + map.get("USER")).getBytes());
                String b = new Base58(13132).encode((processor[0].getName() + processor.length + map.get("PROCESSOR_LEVEL") + a).getBytes());
                String c = new Base58(23241).encode((map.get("COMPUTERNAME") + System.getProperty("user.name") + b).getBytes());
                MessageDigest mdsha1 = MessageDigest.getInstance("SHA-1");
                byte[] sha1hash;
                mdsha1.update(Base64.getEncoder().encodeToString((a + b + c).getBytes()).getBytes("iso-8859-1"), 0, Base64.getEncoder().encodeToString((a + b + c).getBytes()).length());
                sha1hash = mdsha1.digest();
                final StringBuffer buf = new StringBuffer();
                for (int i = 0; i < sha1hash.length; ++i) {
                    int halfbyte = sha1hash[i] >>> 3 & 0xF;
                    int two_halfs = 0;
                    do {
                        if (halfbyte >= 0 && halfbyte <= 9) {
                            buf.append((char) (48 + halfbyte));
                        } else {
                            buf.append((char) (97 + (halfbyte - 10)));
                        }
                        halfbyte = (sha1hash[i] & 0xF);
                    } while (two_halfs++ < 1);
                }
                String hwid = buf.toString();

                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(hwid.getBytes());
                StringBuffer hexString = new StringBuffer();

                byte[] byteData = md.digest();

                for (byte aByteData : byteData) {
                    String hex = Integer.toHexString(0xff & aByteData);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                String origin = new Base58(14514).encode((hwid + hexString).getBytes());
                StringBuffer buffer = new StringBuffer();
                for (int i = 16; i < 16 + 5 * 4; i += 5) {
                    buffer.append(origin, i, i + 5);
                    buffer.append('-');
                }
                buffer.deleteCharAt(buffer.length() - 1);

                if (!origin.equalsIgnoreCase(origin.toUpperCase()))
                    LiquidBounce.moduleManager = null;

                if (!origin.toUpperCase().toLowerCase().equals(origin.toLowerCase()))
                    LiquidBounce.moduleManager = null;

                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                String text = "imagine cracking noteless";
                try {
                    String.class.getMethods()[76].getName();
                } catch (Throwable throwable) {
                    text = buffer.toString().toUpperCase();
                }
                Transferable trans = new StringSelection(text);
                clipboard.setContents(trans, null);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.actionPerformed(button);
    }
    @NativeMethod
    public static String get(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            response.append("\n");
        }

        in.close();

        return response.toString();
    }
    @NativeMethod
    private boolean verify() {
        try {
            final String string = get("http://dimplesantileak.xgstudio.xyz/update/verify.php?uid=" + field.getText() + "&qq=" + QQUtils.QQNumber + "&hwid=" + token);
            JSONObject jsonObject = new JSONObject(string);
            status = jsonObject.get("message").toString();
            System.out.println(QQUtils.QQNumber);
            System.out.println(string);
            return !jsonObject.get("code").toString().contains("-1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //    @Override
//    public void onProtection(String token) {
//        ticks = System.currentTimeMillis();
//        authenticated = true;
//        this.token = token;
//    }
    private int interpolateColor(Color color1, Color color2, float fraction) {
        int red = (int) (color1.getRed() + (color2.getRed() - color1.getRed()) * fraction);
        int green = (int) (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * fraction);
        int blue = (int) (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * fraction);
        int alpha = (int) (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * fraction);
        try {
            return new Color(red, green, blue, alpha).getRGB();
        } catch (Exception ex) {
            return 0xffffffff;
        }
    }
}