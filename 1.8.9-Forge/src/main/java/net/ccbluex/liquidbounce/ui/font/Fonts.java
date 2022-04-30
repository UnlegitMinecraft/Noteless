package net.ccbluex.liquidbounce.ui.font;

import com.google.gson.*;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.FileUtils;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



@SideOnly(Side.CLIENT)
public class Fonts {

    @FontDetails(fontName = "Small", fontSize = 18)
    public static GameFontRenderer font35;
    @FontDetails(fontName = "Small", fontSize = 15)
    public static GameFontRenderer font30;
    @FontDetails(fontName = "jello", fontSize = 20)
    public static GameFontRenderer jello;
    @FontDetails(fontName = "jellom", fontSize = 20)
    public static GameFontRenderer jellom;
    @FontDetails(fontName = "Comfortaa35", fontSize = 18)
    public static GameFontRenderer Comfortaa35;
    @FontDetails(fontName = "test", fontSize = 16)
    public static GameFontRenderer test;
    @FontDetails(fontName = "Small", fontSize = 19)
    public static GameFontRenderer font36;
    @FontDetails(fontName = "icon80", fontSize = 200)
    public static GameFontRenderer icon80;
    @FontDetails(fontName = "flux", fontSize = 20)
    public static GameFontRenderer flux;
    @FontDetails(fontName = "SFUI40", fontSize = 20)
    public static GameFontRenderer SFUI40;
    @FontDetails(fontName = "SFUI35", fontSize = 18)
    public static GameFontRenderer SFUI35;
    @FontDetails(fontName = "SFUI24", fontSize = 10)
    public static GameFontRenderer SFUI24;
    @FontDetails(fontName = "SF", fontSize = 40)
    public static GameFontRenderer SF;
    @FontDetails(fontName = "icon100", fontSize = 100)
    public static GameFontRenderer icon100;

    @FontDetails(fontName = "black", fontSize = 100)
    public static GameFontRenderer black;

    @FontDetails(fontName = "black", fontSize = 20)
    public static GameFontRenderer black40;

    @FontDetails(fontName = "icon45", fontSize = 100)
    public static GameFontRenderer icon45;

    @FontDetails(fontName = "ICONFONT_50", fontSize = 50)
    public static GameFontRenderer ICONFONT_50;

    @FontDetails(fontName = "ETB", fontSize = 18)
    public static GameFontRenderer ETB35;

    @FontDetails(fontName = "GoogleSans", fontSize = 30)
    public static GameFontRenderer GoogleSans1;

    @FontDetails(fontName = "GoogleSans", fontSize = 18)
    public static GameFontRenderer GoogleSans;

    @FontDetails(fontName = "tahoma", fontSize = 18)
    public static GameFontRenderer tahoma35;

    @FontDetails(fontName = "tahoma1", fontSize = 25)
    public static GameFontRenderer tahoma25;

    @FontDetails(fontName = "KomikaTitleBold", fontSize = 18)
    public static GameFontRenderer KomikaTitleBold35;

    @FontDetails(fontName = "Wqy_Microhei", fontSize = 18)
    public static GameFontRenderer  wqy_microhei35;

    @FontDetails(fontName = "sbcnm", fontSize = 50)
    public static GameFontRenderer  sbcnm;

    @FontDetails(fontName = "Medium", fontSize = 20)
    public static GameFontRenderer font40;
    @FontDetails(fontName = "Font60", fontSize = 50)
    public static GameFontRenderer font60;
    @FontDetails(fontName = "Bold", fontSize = 20)
    public static GameFontRenderer fontBold40;

    @FontDetails(fontName = "Roboto Medium", fontSize = 100)
    public static GameFontRenderer fontBold180;

    @FontDetails(fontName = "Minecraft Font")
    public static final FontRenderer minecraftFont = Minecraft.getMinecraft().fontRendererObj;

    @FontDetails(fontName = "Icon",fontSize = 18)
    public static GameFontRenderer icon18;
    @FontDetails(fontName = "Icon",fontSize = 15)
    public static GameFontRenderer icon15;
    @FontDetails(fontName = "Icon",fontSize = 10)
    public static GameFontRenderer icon10;

    private static final List<GameFontRenderer> CUSTOM_FONT_RENDERERS = new ArrayList<>();


    public static GameFontRenderer icon20;

    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts.");

        downloadFonts();

        initFonts();
        GoogleSans1 = new GameFontRenderer(getFont("GoogleSans.ttf", 30));
        GoogleSans = new GameFontRenderer(getFont("GoogleSans.ttf", 18));
        ETB35 = new GameFontRenderer(getFont("ETB.ttf", 18));
        test = new GameFontRenderer(getFont("test.ttf", 16));
        font35 = new GameFontRenderer(getFont("regular.ttf", 18));
        font30 = new GameFontRenderer(getFont("regular.ttf", 15));
        font36 = new GameFontRenderer(getFont("regular.ttf", 19));
        jello = new GameFontRenderer(getFont("jelloSB.ttf", 20));
        jellom = new GameFontRenderer(getFont("jellom.ttf", 20));
        font60 = new GameFontRenderer(getFont("regular.ttf", 50));
        wqy_microhei35 = new GameFontRenderer(getFont("wqy_microhei.ttf", 35));
        font40 = new GameFontRenderer(getFont("regular.ttf", 20));
        SF = new GameFontRenderer(getFont("SF.ttf", 20));
        fontBold40 = new GameFontRenderer(getFont("medium.ttf", 20));
        fontBold180 = new GameFontRenderer(getFont("Roboto-Medium.ttf", 100));
        sbcnm = new GameFontRenderer(getFont("sbcnm.ttf", 50));
        Comfortaa35 = new GameFontRenderer(getFont("Comfortaa.ttf", 18));
        KomikaTitleBold35 = new GameFontRenderer(getFont("KomikaTitleBold.ttf", 18));
        tahoma35 = new GameFontRenderer(getFont("tahoma.ttf", 18));
        tahoma25 = new GameFontRenderer(getFont("tahoma1.ttf", 25));
        ICONFONT_50 = new GameFontRenderer(getFont("stylesicons.ttf", 50));
        black = new GameFontRenderer(getFont("black.ttf", 100));
        black40 = new GameFontRenderer(getFont("black.ttf", 20));
        SFUI40 = new GameFontRenderer(getFont("sfui.ttf", 20));
        SFUI35 = new GameFontRenderer(getFont("sfui.ttf", 18));
        SFUI24 = new GameFontRenderer(getFont("sfui.ttf", 10));
        icon80 = new GameFontRenderer(getFont1(150));
        icon45 = new GameFontRenderer(getFont1(100));
        icon100 = new GameFontRenderer(getFont2(100));
        flux = new GameFontRenderer(getFont3(20));
        //

        icon18 = new GameFontRenderer(getFontcustom(18,"Icon"));
        icon15 = new GameFontRenderer(getFontcustom(15,"Icon"));
        icon10 = new GameFontRenderer(getFontcustom(10,"Icon"));
        try {
            CUSTOM_FONT_RENDERERS.clear();

            final File fontsFile = new File(LiquidBounce.fileManager.fontsDir, "fonts.json");

            if(fontsFile.exists()) {
                final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(fontsFile)));

                if(jsonElement instanceof JsonNull)
                    return;

                final JsonArray jsonArray = (JsonArray) jsonElement;

                for(final JsonElement element : jsonArray) {
                    if(element instanceof JsonNull)
                        return;

                    final JsonObject fontObject = (JsonObject) element;

                    CUSTOM_FONT_RENDERERS.add(new GameFontRenderer(getFont(fontObject.get("fontFile").getAsString(), fontObject.get("fontSize").getAsInt())));
                }
            }else{
                fontsFile.createNewFile();

                final PrintWriter printWriter = new PrintWriter(new FileWriter(fontsFile));
                printWriter.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonArray()));
                printWriter.close();
            }
        }catch(final Exception e) {
            e.printStackTrace();
        }

        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    private static void initFonts() {
        try {
            initSingleFont("regular.ttf");
            initSingleFont("medium.ttf");
            initSingleFont("wqy_microhei.ttf");
            initSingleFont("ETB.ttf");
            initSingleFont("SF.ttf");
            initSingleFont("test.ttf");
            initSingleFont("stylesicons.ttf");
            initSingleFont("GoogleSans.ttf");
            initSingleFont("jelloSB.ttf");
            initSingleFont("jellom.ttf");
            initSingleFont("Comfortaa.ttf");
            initSingleFont("KomikaTitleBold.ttf");
            initSingleFont("tahoma.ttf");
            initSingleFont("tahoma1.ttf");
            initSingleFont("sbcnm.ttf");
            initSingleFont("black.ttf");
            initSingleFont("sfui.ttf");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void initSingleFont(String name) throws IOException {
        FileUtils.unpackFile(new File(LiquidBounce.fileManager.fontsDir, name),name);
    }
    private static Font getFontcustom(int size,String fontname) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("liquidbounce/font/"+fontname+".ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getFont1(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("liquidbounce/icons/icon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getFont2(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("liquidbounce/icons/HanabiFont.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }
    private static Font getFont3(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager()
                    .getResource(new ResourceLocation("liquidbounce/icons/flux.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("default", 0, size);
        }
        return font;
    }

    private static void downloadFonts() {
        try {
            final File outputFile = new File(LiquidBounce.fileManager.fontsDir, "roboto.zip");

            if(!outputFile.exists()) {
                ClientUtils.getLogger().info("Downloading fonts...");
                HttpUtils.download(LiquidBounce.CLIENT_CLOUD + "/fonts/Roboto.zip", outputFile);
                ClientUtils.getLogger().info("Extract fonts...");
                extractZip(outputFile.getPath(), LiquidBounce.fileManager.fontsDir.getPath());
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    public static FontRenderer getFontRenderer(final String name, final int size) {
        for(final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if(o instanceof FontRenderer) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if(fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (FontRenderer) o;
                }
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (final GameFontRenderer liquidFontRenderer : CUSTOM_FONT_RENDERERS) {
            final Font font = liquidFontRenderer.getDefaultFont().getFont();

            if(font.getName().equals(name) && font.getSize() == size)
                return liquidFontRenderer;
        }

        return minecraftFont;
    }


    public static Object[] getFontDetails(final FontRenderer fontRenderer) {
        for(final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if(o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new Object[] {fontDetails.fontName(), fontDetails.fontSize()};
                }
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (fontRenderer instanceof GameFontRenderer) {
            final Font font = ((GameFontRenderer) fontRenderer).getDefaultFont().getFont();

            return new Object[] {font.getName(), font.getSize()};
        }

        return null;
    }

    public static List<FontRenderer> getFonts() {
        final List<FontRenderer> fonts = new ArrayList<>();

        for(final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if(fontObj instanceof FontRenderer) fonts.add((FontRenderer) fontObj);
            }catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS);

        return fonts;
    }
    private static Font getFont1(final String fontName, final int size) {
        try {
            final InputStream inputStream = new FileInputStream(new File(LiquidBounce.fileManager.fontsDir, fontName));
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        }catch(final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }

    private static Font getFont(final String fontName, final int size) {
        try {
            final InputStream inputStream = new FileInputStream(new File(LiquidBounce.fileManager.fontsDir, fontName));
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        }catch(final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }

    private static void extractZip(final String zipFile, final String outputFolder) {
        final byte[] buffer = new byte[1024];

        try {
            final File folder = new File(outputFolder);

            if(!folder.exists()) folder.mkdir();

            final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while(zipEntry != null) {
                File newFile = new File(outputFolder + File.separator + zipEntry.getName());
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int i;
                while((i = zipInputStream.read(buffer)) > 0)
                    fileOutputStream.write(buffer, 0, i);

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        }catch(final IOException e) {
            e.printStackTrace();
        }
    }
}