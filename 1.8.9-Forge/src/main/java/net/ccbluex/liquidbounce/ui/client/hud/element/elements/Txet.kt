/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CPSCounter
import net.ccbluex.liquidbounce.utils.ColorManager
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatAllowedCharacters
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@ElementInfo(name = "Text")
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F, side: Side = Side.default()) : Element(x, y, scale, side) {

    companion object {

        val DATE_FORMAT = SimpleDateFormat("MMddyy")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")
        val Y_FORMAT = DecimalFormat("0.00")
        val DECIMAL_FORMAT = DecimalFormat("0.00")

        /**
         * Create default element
         */
        fun defaultClient(): Text {
            val text = Text(x = 5.0, y = 5.0, scale = 1.0F)
            text.displayString.set("Noteless | Fps:%fps% | Speed:%speed% | Time:%time% ")
            text.shadow.set(true)
            text.fontValue.set(Fonts.minecraftFont)
            text.setColor(Color(64, 255, 200))
            return text
        }

        fun defaultClient2(): Text {
            val text = Text(x = 2.0, y = 490.0, scale = 1.0F)
            text.displayString.set("%speed%")
            text.shadow.set(true)
            text.rect.set(true)
            text.fontValue.set(Fonts.minecraftFont)
            text.setColor(Color(100, 100, 225))
            return text
        }

        fun defaultClient3(): Text {
            val text = Text(x = 2.0, y = 501.0, scale = 1.0F)
            text.displayString.set("%7%Coords:%r% %x%  %y%  %z%")
            text.shadow.set(true)
            text.rect.set(true)
            text.fontValue.set(Fonts.minecraftFont)
            text.setColor(Color(100, 100, 225))
            return text
        }
    }
    private val displayString = TextValue("DisplayText", "")
    private val redValue = IntegerValue("Red", 110, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 88, 0, 255)
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom" , "Fade", "Astolfo"), "Fade")
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val animation = BoolValue("Animation", false)
    private val animationDelay = FloatValue("AnimationDelay", 0.33F, 0.1F, 2F)
    private val astolfoRainbowOffset = IntegerValue("AstolfoRainbowOffset", 5, 1, 20)
    private val astolfoRainbowIndex = IntegerValue("AstolfoRainbowIndex", 109, 1, 300)
    private val slide = BoolValue("Slide", false)
    private val slidedelay = IntegerValue("SlideDelay", 100, 0, 1000)
    private val shadow = BoolValue("Shadow", true)
    private val rect = BoolValue("Rect", true)
    private val balpha = IntegerValue("BordAlpha", 255, 0, 255)
    private val Mode = ListValue("Border-Mode", arrayOf("Slide", "Skeet","GameSense","Bordered"), "Slide")
    private val og = BoolValue("orangeText", false)
    private var fontValue = FontValue("Font", Fonts.minecraftFont)
    private val ubordredValue = IntegerValue("UpBorderRed", 80, 0, 255)
    private val ubordgreenValue = IntegerValue("UpBorderGreen", 207, 0, 255)
    private val ubordblueValue = IntegerValue("UpBorderBlue", 110, 0, 255)
    private val ualpha = IntegerValue("UpBorderAlpha", 60, 0, 255)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display
    val counter1 = intArrayOf(50)
    val counter2 = intArrayOf(100)
    private var lastTick = -1
    private var displaySpeed = 0.0

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode)
                "Noteless | Fps:%fps% | Speed:%speed% | Time:%time%"
            else
                displayString.get()


            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        val ka = LiquidBounce.moduleManager.getModule(KillAura::class.java)
        if (mc.thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer.posX)
                "y" -> return Y_FORMAT.format(mc.thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer.posZ)
                "xdp" -> return mc.thePlayer.posX.toString()
                "ydp" -> return mc.thePlayer.posY.toString()
                "zdp" -> return mc.thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) * 20)
                "speed" -> return String.format("%.2f", displaySpeed)
                "ping" -> return EntityUtils.getPing(mc.thePlayer).toString()
                "0" -> return "§0"
                "1" -> return "§1"
                "2" -> return "§2"
                "3" -> return "§3"
                "4" -> return "§4"
                "5" -> return "§5"
                "6" -> return "§6"
                "7" -> return "§7"
                "8" -> return "§8"
                "9" -> return "§9"
                "a" -> return "§a"
                "b" -> return "§b"
                "c" -> return "§c"
                "d" -> return "§d"
                "e" -> return "§e"
                "f" -> return "§f"
                "n" -> return "§n"
                "m" -> return "§m"
                "l" -> return "§l"
                "k" -> return "§k"
                "o" -> return "§o"
                "r" -> return "§r"
            }
        }

        return when (str) {
            "username" -> mc.getSession().username
            "clientname" -> LiquidBounce.CLIENT_NAME
            "clientversion" -> "b${LiquidBounce.CLIENT_VERSION}"
            "clientcreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverip" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            "LiquidSense" -> return "L§fiquidSense §7|| [§fFPS ${Minecraft.getDebugFPS()}§7] || ${ServerUtils.getRemoteIp()} || [§fTime ${HOUR_FORMAT.format(System.currentTimeMillis())}§7] || §f${mc.getSession().username}"

            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }

    /**
     * Draw element
     */
    var slidetext: Int = 0
    var slidetimer: MSTimer = MSTimer()
    var doslide = true
    override fun drawElement(): Border? {
        if (mc.thePlayer != null && lastTick != mc.thePlayer!!.ticksExisted) {
            lastTick = mc.thePlayer!!.ticksExisted
            val z2 = mc.thePlayer!!.posZ
            val z1 = mc.thePlayer!!.prevPosZ
            val x2 = mc.thePlayer!!.posX
            val x1 = mc.thePlayer!!.prevPosX
            var speed = sqrt((z2 - z1) * (z2 - z1) + (x2 - x1) * (x2 - x1))
            if (speed < 0)
                speed = -speed
            displaySpeed = speed * 20
        }
        if (slide.get() && mc.currentScreen != GuiHudDesigner()) {
            if (slidetimer.hasTimePassed(slidedelay.get().toLong())) {
                if (slidetext <= display.length && doslide) {
                    slidetext += 1
                    slidetimer.reset()
                } else {
                    if (!doslide && slidetext >= 0) {
                        slidetext -= 1
                        slidetimer.reset()
                    }
                }
            }
            if (slidetext == display.length && doslide) {
                doslide = false
            } else {
                if (slidetext == 0 && !doslide) {
                    doslide = true
                }
            }
            displayText = display.substring(0, slidetext)
        } else {
            displayText = display
        }
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        val color2 = Color(ubordredValue.get(), ubordgreenValue.get(), ubordblueValue.get(), ualpha.get())
        val fontRenderer = fontValue.get()
        if (this.rect.get()) {
            if (Mode.get() == "Skeet") {
                RenderUtils.autoExhibition(-4.0, -4.2, ((fontRenderer.getStringWidth(displayText) + 3.0).toDouble()), (fontRenderer.FONT_HEIGHT + 2.5).toDouble(),1.0)
            }
            if (Mode.get() == "Slide") {
                counter1[0] += 1
                counter2[0] += 1
                counter1[0] = counter1[0].coerceIn(0, 50)
                counter2[0] = counter2[0].coerceIn(0, 100)
                RenderUtils.drawRect(-4.0f, -4.5f, (fontRenderer.getStringWidth(displayText) + 3.0).toFloat(), fontRenderer.FONT_HEIGHT.toFloat(), Color(0, 0, 0, balpha.get()).rgb)
                RenderUtils.drawGradientSideways(-3.0, -4.0, fontRenderer.getStringWidth(displayText) + 2.0, -3.0, Palette.fade2(color2,counter1[0], fontRenderer.FONT_HEIGHT).rgb,Palette.fade2(color2, counter2[0], fontRenderer.FONT_HEIGHT).rgb)
            }
            if (Mode.get() == "GameSense") {
                val color2G = Color(ubordredValue.get(), ubordgreenValue.get(), ubordblueValue.get(), 255).rgb
                val addColorG = color2G + Color(0, 0, 0, 50).rgb
                RenderUtils.drawRect(-4.0f, -3.8f, (fontRenderer.getStringWidth(displayText) + 3.0).toFloat(), fontRenderer.FONT_HEIGHT.toFloat() + 1.8f,Color(0, 0, 0, balpha.get()).rgb)
                RenderUtils.drawRect(-3.8f, -3.8f, -2.9f, fontRenderer.FONT_HEIGHT.toFloat() + 1.8f, Color(ubordredValue.get(), ubordgreenValue.get(), ubordblueValue.get(), 255).rgb)
                RenderUtils.drawRect(2.1f + fontRenderer.getStringWidth(displayText), -3.8f, 3.0f + fontRenderer.getStringWidth(displayText), fontRenderer.FONT_HEIGHT.toFloat() + 1.8f, Color(ubordredValue.get(), ubordgreenValue.get(), ubordblueValue.get(), 255).rgb)
                RenderUtils.drawGradientSideways(-3.1 + (fontRenderer.getStringWidth(displayText) + 5.0) / 2, -4.0, fontRenderer.getStringWidth(displayText) + 2.0, -2.8, addColorG, color2G)
                RenderUtils.drawGradientSideways(-3.1 + (fontRenderer.getStringWidth(displayText) + 5.0) / 2, 0.5 + fontRenderer.FONT_HEIGHT.toFloat(), fontRenderer.getStringWidth(displayText) + 2.0, 1.5 + fontRenderer.FONT_HEIGHT.toFloat(), addColorG, color2G)
                RenderUtils.drawGradientSideways(-3.0, -4.0, fontRenderer.getStringWidth(displayText) + 2.21 - (fontRenderer.getStringWidth(displayText) + 5.0) / 2.0, -2.8, color2G, addColorG)
                RenderUtils.drawGradientSideways(-3.0, 0.5 + fontRenderer.FONT_HEIGHT.toFloat(), fontRenderer.getStringWidth(displayText) + 2.21 - (fontRenderer.getStringWidth(displayText) + 5.0) / 2.0, 1.5 + fontRenderer.FONT_HEIGHT.toFloat(), color2G, addColorG)
            }
            if (Mode.get() == "Bordered") {
                RenderUtils.drawBorderedRect(
                    -5.0f,
                    -5.0f,
                    (fontRenderer.getStringWidth(
                        displayText
                    ) + 4).toFloat(),
                    fontRenderer.FONT_HEIGHT.toFloat(),
                    3.0f,
                    Color(59, 59, 59).rgb,
                    Color(50, 50, 50).rgb
                )
            }
        }
        if (og.get()) {
            fontRenderer.drawString(displayText, -0.5f, 0.0f, Color(0, 111, 255).rgb, false)
            fontRenderer.drawString(displayText, 0.0f, 0.0f, Color.white.rgb, false)
        }
        val charArray = displayText.toCharArray()
        var length = 0
        val delay = intArrayOf(0)
        val counter = intArrayOf(0)
        delay[0] = delay[0] + 1
        for (charIndex in charArray) {
            val rainbow = colorMode.equals("Rainbow", ignoreCase = true)
            RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F ).use {
                fontRenderer.drawString(charIndex.toString(), length.toFloat(), 0F, when {
                    rainbow -> 0
                    colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                    colorMode.equals("Astolfo", ignoreCase = true) ->  ColorManager.astolfoRainbow(delay[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                    else -> color
                }, shadow.get())
                if (editMode && mc.currentScreen is GuiHudDesigner && editTicks <= 40)
                    fontRenderer.drawString("_", fontRenderer.getStringWidth(displayText) + 2F,
                        0F, when {
                            rainbow -> 0
                            colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(Color(redValue.get(), greenValue.get(), blueValue.get()), counter[0] * 100, displayText.length * 200).rgb
                            colorMode.equals("Astolfo", ignoreCase = true) ->  ColorManager.astolfoRainbow(delay[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                            else -> color
                        }, shadow.get())
            }
            counter[0] += 1
            counter[0] = counter[0].coerceIn(0, displayText.length)
            length += fontRenderer.getCharWidth(charIndex)
        }
        if (editMode && mc.currentScreen !is GuiHudDesigner) {
            editMode = false
            updateElement()
        }


        return Border(
            -2F,
            -2F,
            fontRenderer.getStringWidth(displayText) + 2F,
            fontRenderer.FONT_HEIGHT.toFloat()
        )
    }

    private var count = 0
    private var reverse = false

    private var timer = MSTimer()

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        if (count < 0 || count > display.length) {
            count = 0
            reverse = false
        }

        if (!editMode && animation.get() && timer.hasTimePassed((animationDelay.get() * 1000).toLong())) {
            if (reverse) count -= 1 else count += 1
            if (count == display.length || count == 0) reverse = !reverse
            timer.reset()
        }

        val tempDisplayText = if (animation.get()) display.substring(0, count) else display

        displayText = if (editMode) displayString.get() else tempDisplayText
    }


    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L)
                editMode = true

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && mc.currentScreen is GuiHudDesigner) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty())
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§')
                displayString.set(displayString.get() + c)

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }

}