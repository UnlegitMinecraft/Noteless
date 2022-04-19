/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils.render

import com.google.gson.JsonObject
import net.minecraft.util.ChatAllowedCharacters
import java.awt.Color
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

object ColorUtils {

    private val COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]")

    @JvmField
    val hexColors = IntArray(16)
    @JvmStatic
    fun healthColor(hp: Float,maxHP: Float):Color{
        val pct=((hp/maxHP)*255F).toInt()
        return Color(max(min(255-pct, 255),0), max(min(pct, 255),0), 0)
    }

    @JvmStatic
    fun fade(color: Color, index: Int, count: Int): Color {
        val hsb = FloatArray(3)
        Color.RGBtoHSB(color.red, color.green, color.blue, hsb)
        var brightness =
            abs(((System.currentTimeMillis() % 2000L).toFloat() / 1000.0f + index.toFloat() / count.toFloat() * 2.0f) % 2.0f - 1.0f)
        brightness = 0.5f + 0.5f * brightness
        hsb[2] = brightness % 2.0f
        return Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]))
    }
    @JvmStatic
    fun LiquidSlowly(time: Long, count: Int, qd: Float, sq: Float): Color? {
        val color = Color(Color.HSBtoRGB((time.toFloat() + count * -3000000f) / 2 / 1.0E9f, qd, sq))
        return Color(color.red / 255.0f * 1, color.green / 255.0f * 1, color.blue / 255.0f * 1, color.alpha / 255.0f)
    }
    @JvmStatic
    fun getHealthColor(health: Float, maxHealth: Float): Color? {
        val fractions = floatArrayOf(0.0f, 0.5f, 1.0f)
        val colors = arrayOf(Color(108, 0, 0), Color(255, 51, 0), Color.GREEN)
        val progress = health / maxHealth
        return blendColors(fractions, colors, progress).brighter()
    }

    fun blendColors(fractions: FloatArray, colors: Array<Color>, progress: Float): Color {
        if (fractions.size == colors.size) {
            val indices: IntArray = getFractionIndices(fractions, progress)
            val range = floatArrayOf(fractions[indices[0]], fractions[indices[1]])
            val colorRange = arrayOf(colors[indices[0]], colors[indices[1]])
            val max = range[1] - range[0]
            val value = progress - range[0]
            val weight = value / max
            return blend(colorRange[0], colorRange[1], 1.0 - weight)
        }
        throw IllegalArgumentException("Fractions and colours must have equal number of elements")
    }
    fun getFractionIndices(fractions: FloatArray, progress: Float): IntArray {
        var startPoint: Int
        val range = IntArray(2)
        startPoint = 0
        while (startPoint < fractions.size && fractions[startPoint] <= progress) {
            ++startPoint
        }
        if (startPoint >= fractions.size) {
            startPoint = fractions.size - 1
        }
        range[0] = startPoint - 1
        range[1] = startPoint
        return range
    }
    @JvmStatic
    fun reAlpha(color: Color,alpha: Int): Color{
        return Color(color.red,color.green,color.blue,alpha)
    }
    fun blend(color1: Color, color2: Color, ratio: Double): Color {
        val r = ratio.toFloat()
        val ir = 1.0f - r
        val rgb1 = FloatArray(3)
        val rgb2 = FloatArray(3)
        color1.getColorComponents(rgb1)
        color2.getColorComponents(rgb2)
        return Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir)
    }
    @JvmStatic
    fun rainbow3(offset: Long, rainbowSpeed: Float, rainbowBright: Float): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, rainbowSpeed, rainbowBright))
        return Color(currentColor.red.toFloat() / 255.0f * 1.0f, currentColor.green.toFloat() / 255.0f * 1.0f, currentColor.blue.toFloat() / 255.0f * 1.0f, currentColor.alpha.toFloat() / 255.0f)
    }

    @JvmStatic
    fun darker(color: Color,percentage: Float):Color{
        return Color((color.red*percentage).toInt(),(color.green*percentage).toInt(),(color.blue*percentage).toInt(),(color.alpha*percentage).toInt())
    }

    @JvmStatic
    fun skyRainbow(var2: Int, bright: Float, st: Float, speed: Double): Color {
        var v1 = ceil(System.currentTimeMillis() / speed + var2 * 109L) / 5
        return Color.getHSBColor(if ((360.0.also { v1 %= it } / 360.0)<0.5){ -(v1 / 360.0).toFloat() } else { (v1 / 360.0).toFloat() }, st, bright)
    }

    @JvmStatic
    fun rainbowWithAlpha(alpha: Int) = reAlpha(hslRainbow(1),alpha)

    @JvmStatic
    fun rainbowW(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.6F, 1F))
        return Color(0F, currentColor.red.toFloat() / 255.0F * 1.0F, currentColor.blue.toFloat() / 255.0F * 1.0F, currentColor.alpha.toFloat() / 255.0F)
    }

    @JvmStatic
    fun redRainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.5F, 1F))
        return Color(currentColor.red.toFloat() / 255.0F * 1.0F, 0F, 0F, currentColor.alpha.toFloat() / 255.0F)
    }

    @JvmStatic
    fun greenRainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.5F, 1F))
        return Color(0F, currentColor.green / 255.0F * 1.0F, 0F, currentColor.alpha.toFloat() / 255.0F)
    }

    @JvmStatic
    fun blueRainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 0.5F, 1F))
        return Color(0F, 0F, currentColor.blue.toFloat() / 255.0F * 1.0F, currentColor.alpha.toFloat() / 255.0F)
    }

    init {
        repeat(16) { i ->
            val baseColor = (i shr 3 and 1) * 85

            val red = (i shr 2 and 1) * 170 + baseColor + if (i == 6) 85 else 0
            val green = (i shr 1 and 1) * 170 + baseColor
            val blue = (i and 1) * 170 + baseColor

            hexColors[i] = red and 255 shl 16 or (green and 255 shl 8) or (blue and 255)
        }
    }

    @JvmStatic
    fun stripColor(input: String?): String? {
        return COLOR_PATTERN.matcher(input ?: return null).replaceAll("")
    }

    @JvmStatic
    fun translateAlternateColorCodes(textToTranslate: String): String {
        val chars = textToTranslate.toCharArray()

        for (i in 0 until chars.size - 1) {
            if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".contains(chars[i + 1], true)) {
                chars[i] = '§'
                chars[i + 1] = Character.toLowerCase(chars[i + 1])
            }
        }

        return String(chars)
    }

    fun randomMagicText(text: String): String {
        val stringBuilder = StringBuilder()
        val allowedCharacters = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"

        for (c in text.toCharArray()) {
            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                val index = Random().nextInt(allowedCharacters.length)
                stringBuilder.append(allowedCharacters.toCharArray()[index])
            }
        }

        return stringBuilder.toString()
    }

    @JvmStatic
    fun rainbow(): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + 400000L) / 10000000000F % 1, 1F, 1F))
        return Color(currentColor.red / 255F * 1F, currentColor.green / 255f * 1F, currentColor.blue / 255F * 1F, currentColor.alpha / 255F)
    }

    // TODO: Use kotlin optional argument feature

    @JvmStatic
    fun rainbow(offset: Long): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 1F, 1F))
        return Color(currentColor.red / 255F * 1F, currentColor.green / 255F * 1F, currentColor.blue / 255F * 1F,
                currentColor.alpha / 255F)
    }

    @JvmStatic
    fun rainbow(alpha: Float) = rainbow(400000L, alpha)

    @JvmStatic
    fun rainbow(alpha: Int) = rainbow(400000L, alpha / 255)

    @JvmStatic
    fun rainbow(offset: Long, alpha: Int) = rainbow(offset, alpha.toFloat() / 255)

    @JvmStatic
    fun rainbow(offset: Long, alpha: Float): Color {
        val currentColor = Color(Color.HSBtoRGB((System.nanoTime() + offset) / 10000000000F % 1, 1F, 1F))
        return Color(currentColor.red / 255F * 1F, currentColor.green / 255f * 1F, currentColor.blue / 255F * 1F, alpha)
    }
    @JvmStatic
    fun getOppositeColor(color: Color): Color = Color(255 - color.red, 255 - color.green, 255 - color.blue, color.alpha)
    @JvmStatic
    fun decodeColorJsonFormat(json: JsonObject):Color{
        return reAlpha(if(json.has("rainbow")){
            when(json.get("rainbow").asString.toLowerCase()){
                "normal" -> rainbow(400000000L * if(json.has("rainbow_index")){json.get("rainbow_index").asInt}else{1})
                "sky" -> RenderUtils.skyRainbow(if(json.has("rainbow_index")){json.get("rainbow_index").asInt}else{1},0.9f,1f,5.0)
                "rise" -> hslRainbow(if(json.has("rainbow_index")){json.get("rainbow_index").asInt}else{1}+1)
                else -> Color.WHITE
            }
        }else{
            Color(json.get("red").asInt,json.get("green").asInt,json.get("blue").asInt)
        },if(json.has("alpha")){json.get("alpha").asInt}else{160})
    }

    private val startTime=System.currentTimeMillis()

    fun hslRainbow(index: Int,lowest: Float=0.41f,bigest: Float=0.58f,indexOffset: Int=300,timeSplit: Int=1500):Color{
        return Color.getHSBColor((abs(((((System.currentTimeMillis()-startTime).toInt()+index*indexOffset)/timeSplit.toFloat())%2)-1)*(bigest-lowest))+lowest,0.7f,1f)
    }
}