package net.ccbluex.liquidbounce.utils;


import net.ccbluex.liquidbounce.utils.messages.MessageFactory;
import net.ccbluex.liquidbounce.utils.messages.TextMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;

import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE;

public class DebugUtil {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static void log(String prefix, Object message) {
        String text = prefix(prefix).getFormattedText() + " " + message;
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }


    public static TextMessage prefix(String text) {
        return MessageFactory.text(text, LIGHT_PURPLE).append(" \u00bb ", GRAY);
    }
}
