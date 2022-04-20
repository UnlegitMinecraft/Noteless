/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.misc;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public final class StringUtils {
    private static HashMap<String,String> stringCache = new HashMap<>();
    public static String toCompleteString(final String[] args, final int start) {
        if(args.length <= start) return "";

        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }
    public static String fixString(String str){

        if(stringCache.containsKey(str)) return stringCache.get(str);

        str=str.replaceAll("\uF8FF","");//remove air chars

        StringBuilder sb=new StringBuilder();
        for(char c:str.toCharArray()){
            if((int) c >(33+65248)&& (int) c <(128+65248)){
                sb.append(Character.toChars((int) c - 65248));
            }else{
                sb.append(c);
            }
        }
        String result=sb.toString();
        stringCache.put(str,result);

        return result;
    }
    public static String replace(final String string, final String searchChars, String replaceChars) {
        if(string.isEmpty() || searchChars.isEmpty() || searchChars.equals(replaceChars))
            return string;

        if(replaceChars == null)
            replaceChars = "";

        final int stringLength = string.length();
        final int searchCharsLength = searchChars.length();
        final StringBuilder stringBuilder = new StringBuilder(string);

        for(int i = 0; i < stringLength; i++) {
            final int start = stringBuilder.indexOf(searchChars, i);

            if(start == -1) {
                if(i == 0)
                    return string;

                return stringBuilder.toString();
            }

            stringBuilder.replace(start, start + searchCharsLength, replaceChars);
        }

        return stringBuilder.toString();
    }
}
