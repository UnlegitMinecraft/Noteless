package cn;



import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.api.FontManager;
import net.ccbluex.liquidbounce.cn.Insane.Module.fonts.impl.SimpleFontManager;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.SideGui.SideGui;

public class WbxMain {
    public static String Name = "Noteless";
    public static String Rank = "";
    public static long playTimeStart = 0;
    public static String version = "Build Test";
    public static String username;
    private final SideGui sideGui = new SideGui();
    private static WbxMain INSTANCE;
    public static boolean got = false;
    public  SideGui getSideGui() {
        return sideGui;
    }
    public static WbxMain getInstance() {
        try {
            if (INSTANCE == null) INSTANCE = new WbxMain();
            return INSTANCE;
        } catch (Throwable t) {
            //    ClientUtils.getLogger().warn(t);
            throw t;
        }
    }
    public static FontManager fontManager = SimpleFontManager.create();
    public static FontManager getFontManager() {
        return fontManager;
    }
}