package cn;



import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.api.FontManager;
import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.SimpleFontManager;

public class WbxMain {
    public static String Name = "Noteless";
    public static String Rank = "";
    public static String version = "";
    public static String username;
    public static FontManager fontManager = SimpleFontManager.create();
    public static FontManager getFontManager() {
        return fontManager;
    }
    }