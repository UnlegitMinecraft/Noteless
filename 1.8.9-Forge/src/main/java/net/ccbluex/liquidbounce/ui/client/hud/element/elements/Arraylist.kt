
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Horizontal
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Vertical
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ColorManager
import net.ccbluex.liquidbounce.utils.Colors
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowFontShader
import net.ccbluex.liquidbounce.utils.render.shader.shaders.RainbowShader
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.sin


/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "Arraylist", single = true)
class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                side: Side = Side(Horizontal.RIGHT, Vertical.UP)) : Element(x, y, scale, side) {
    private val RianbowspeedValue = IntegerValue("BRainbowSpeed", 90, 1, 90)
    private val RianbowbValue = FloatValue("BRainbow-Saturation", 1f, 0f, 1f)
    private val RianbowsValue = FloatValue("BRainbow-Brightness", 1f, 0f, 1f)
    private val Rianbowr = IntegerValue("BRainbow-R", 0, 0, 255)
    private val Rianbowb = IntegerValue("BRainbow-B", 50, 0, 64)
    private val Rianbowg = IntegerValue("BRainbow-G", 50, 0, 64)
    private val outLineRectWidth = IntegerValue("OutLineRectWidth", 3, 3, 6)
    private val fakeName = BoolValue("NameBreak", true)
    private val animationMode = ListValue("Animation", arrayOf("Slide","Normal"), "Slide")
    private val rainbow3Offset = IntegerValue("RainbowOffset", 16, 1, 30)
    private val customRainbowSpeed = FloatValue("RainbowSpeed", 0.6F, 0.1F, 1F)
    private val astolfoRainbowOffset = IntegerValue("AstolfoRainbowOffset", 5, 1, 20)
    private val astolfoRainbowIndex = IntegerValue("AstolfoRainbowIndex", 109, 1, 300)
    private val animationSpeed = FloatValue("AnimationSpeed", 0.08F, 0.01F, 0.5F)
    private val rainbowX = FloatValue("Rainbow-X", -1000F, -2000F, 2000F)
    private val rainbowY = FloatValue("Rainbow-Y", -1000F, -2000F, 2000F)
    private val colorModeValue = ListValue("Text-Color", arrayOf("Custom", "Random", "Rainbow","CustomRainbow", "Bainbow" ,"riserainbow","OtherRainbow","Rainbow2", "Rainbow3","Astolfo"), "Astolfo")
    private val rectColorModeValue = ListValue("Rect-Color", arrayOf("Custom", "Random","CustomRainbow","Rainbow", "Bainbow" ,"OtherRainbow","Rainbow2", "Rainbow3","Astolfo"), "Astolfo")
    private val rectColorRedValue = IntegerValue("Rect-R", 255, 0, 255)
    private val rectColorGreenValue = IntegerValue("Rect-G", 255, 0, 255)
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255)
    private val rectColorAlphaValue = IntegerValue("Rect-Alpha", 255, 0, 255)
    private val saturationValue = FloatValue("Random-Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Random-Brightness", 1f, 0f, 1f)
    private val tags = BoolValue("Tags", true)
    private val shadow = BoolValue("ShadowText", true)
    private val shadowTextMode = ListValue("ShadowTextMode", arrayOf("Minecraft", "New"), "New")
    private val backgroundColorModeValue = ListValue("Background-Color", arrayOf("Custom", "Random","Rainbow"), "Custom")
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 0, 0, 255)
    private val rectValue = ListValue("Rect", arrayOf("None", "Left", "Right", "OutLine"), "None")
    private val upperCaseValue = BoolValue("UpperCase", false)
    private val spaceValue = FloatValue("Space", 0F, 0F, 5F)
    private val textHeightValue = FloatValue("TextHeight", 11F, 1F, 20F)
    private val textYValue = FloatValue("TextY", 1F, 0F, 20F)
    private val tagsArrayColor = BoolValue("TagsArrayColor", false)
    private val fontValue = FontValue("Font", Fonts.test)
    private val rectWidth = IntegerValue("LeftRectWidth", 1, 1, 3);
    private var x2 = 0
    private var y2 = 0F
    private var rainbowTick = 0
    private var counter = 0
    private var hue : Float = 0F
    private var modules = emptyList<Module>()
    override fun drawElement(): Border? {
        val delay = intArrayOf(0)
        val stack = Color(Color.HSBtoRGB((mc.thePlayer.ticksExisted / 50.0 + sin(rainbowTick / 50 * 1.6)).toFloat() % 1.0f, 0.5f, 1.0f))
        val rainboW: Int = Color(0, stack.red, stack.blue).rgb
        if (hue > 255.0f) {
            hue = 0.0f
        }
        var h = hue
        var h2 = hue + 85.0f
        var h3 = hue + 170.0f
        if (h > 255.0f) {
            h = 0.0f
        }
        if (h2 > 255.0f) {
            h2 -= 255.0f
        }
        if (h3 > 255.0f) {
            h3 -= 255.0f
        }
        val color33 = Color.getHSBColor((h / 255.0f), 0.9f, 1.0f)
        val color332 = Color.getHSBColor((h2 / 255.0f), 0.9f, 1.0f)
        val color333 = Color.getHSBColor((h3 / 255.0f), 0.9f, 1.0f)
        val color1 = color33.rgb
        val color2 = color332.rgb
        val color3 = color333.rgb
        hue += 1
        if(++rainbowTick >= 100)
            rainbowTick = 0
        val scaledResolution = ScaledResolution(mc)
        val fontRenderer = fontValue.get()

        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        for (module in LiquidBounce.moduleManager.modules) {
            if (!module.array || (!module.state && module.slide == 0F)) continue

            var displayString = if (!tags.get())
                module.fakeName(fakeName.get())
            else if (tagsArrayColor.get())
                module.colorlessTagName(fakeName.get())
            else module.tagName(fakeName.get())

            if (upperCaseValue.get())
                displayString = displayString.toUpperCase()

            val width = fontRenderer.getStringWidth(displayString)
            if (module.state) {
                if (module.slide < width) {
                    module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                    module.slideStep += delta / 4F
                }
            } else if (module.slide > 0) {
                module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                module.slideStep -= delta / 4F
            }
            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        // Draw arraylist
        val colorMode = colorModeValue.get()
        val rectColorMode = rectColorModeValue.get()
        val backgroundColorMode = backgroundColorModeValue.get()
        val customColor = Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get(), 1).rgb
        val rectCustomColor = Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
            rectColorAlphaValue.get()).rgb
        val space = spaceValue.get()
        val textHeight = textHeightValue.get()
        val textY = textYValue.get()
        val rectMode = rectValue.get()
        val backgroundCustomColor = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
            backgroundColorBlueValue.get(), backgroundColorAlphaValue.get()).rgb
        val textShadow = shadow.get()
        val textSpacer = textHeight + space
        val saturation = saturationValue.get()
        val brightness = brightnessValue.get()
        val Rsaturation = RianbowbValue.get()
        val Rbrightness = RianbowsValue.get()
        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags.get())
                        module.fakeName(fakeName.get())
                    else if (tagsArrayColor.get())
                        module.colorlessTagName(fakeName.get())
                    else module.tagName(fakeName.get())

                    if (upperCaseValue.get())
                        displayString = displayString.toUpperCase()

                    //var xPos: Float = 0F
                    val xPos = -module.slide - 2
                    //val yPos = module.slideStep
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val customRainbowColour = Palette.fade2(Color(customColor),modules.indexOf(module),fontRenderer.FONT_HEIGHT)
                    val customRectRainbowColor = Palette.fade2(Color(customColor),modules.indexOf(module),fontRenderer.FONT_HEIGHT)
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb
                    if(module.state) {
                        when(animationMode.get()) {
                            "Slide" -> module.translate.interpolate(xPos, yPos, animationSpeed.get().toDouble())
                            "Normal" -> module.translate.interpolate2(xPos,yPos, animationSpeed.get().toDouble())
                        }
                    } else
                        when(animationMode.get()) {
                            "Slide" -> module.translate.interpolate(xPos, yPos, animationSpeed.get().toDouble())
                            "Normal" -> module.translate.interpolate2(xPos, yPos, animationSpeed.get().toDouble())
                        }
                    val backgroundRectRainbow = backgroundColorMode.equals("Rainbow", ignoreCase = true)
                    val translationFactor = 14.4F / Minecraft.getDebugFPS()
                    var hue = 0F
                    hue += translationFactor / 100.0f
                    if (hue > 1.0F) {
                        hue = 0.0F
                    }
                    delay[0] = delay[0] + 1
                    counter++
                    if (textShadow && shadowTextMode.get() == "New") {
                        GL11.glPushMatrix()
                        GL11.glTranslated(0.5, 0.5, 0.0)
                        fontRenderer.drawString(displayString, module.translate.x - if (rectValue.get().equals("right", true)) 3 else 0, module.translate.y + textY, Color(0, 0, 0, 180).rgb, false)
                        GL11.glTranslated(-0.5, -0.5, 0.0)
                        GL11.glPopMatrix()
                    }

                    RainbowShader.begin(backgroundRectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        RenderUtils.drawRect(
                            module.translate.x - when (rectMode) {
                                "Right" -> {
                                    5
                                }
                                "OutLine" -> {
                                    outLineRectWidth.get()
                                }
                                else -> {
                                    2
                                }
                            },
                            module.translate.y,
                            if (rectMode.equals("right", true)) -3F else 0F,
                            module.translate.y + textHeight, when {
                                backgroundRectRainbow -> 0xFF shl 24
                                backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                else -> backgroundCustomColor
                            }
                        )
                    }
                    val LiquidSlowly = ColorUtils1.LiquidSlowly(System.nanoTime(), index * RianbowspeedValue.get(), Rsaturation, Rbrightness)?.rgb
                    val c: Int = LiquidSlowly!!
                    val col = Color(c)
                    val braibow = Color(Rianbowr.get(), col.getGreen() / 2 + Rianbowb.get(), col.getGreen() / 2 + Rianbowb.get() + Rianbowg.get()).rgb
                    val rainbow = colorMode.equals("Rainbow", ignoreCase = true)
                    val rainbowSpeed = IntegerValue("RainbowSpeed",1,1,10)
                    RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        fontRenderer.drawString(displayString, /*xPos - if (rectMode.equals("right", true)) 3 else 0*/ module.translate.x - if (rectValue.get().equals("right", true)) 3 else 0, /**yPos + textY*/
                            module.translate.y + textY, when {
                                rainbow -> 0
                                colorMode.equals("Random", ignoreCase = true) -> moduleColor
                                colorMode.equals("CustomRainbow", ignoreCase = true) -> customRainbowColour.rgb
                                colorMode.equals("OtherRainbow", ignoreCase = true) -> Colors.getRainbow(-2000, (yPos * 8.toFloat()).toInt())
                                colorMode.equals("VanillaRainbow", ignoreCase = true) -> ColorUtils.rainbow(700000000L * index).rgb
                                colorMode.equals("TestRainbow", ignoreCase = true) -> ColorUtils.rainbowW(400000000L * index).rgb
                                colorMode.equals("Bainbow", ignoreCase = true) -> braibow
                                colorMode.equals("Rainbow2", true) -> ColorUtils.rainbow3(400000000L * index, customRainbowSpeed.get(), 1F).rgb
                                colorMode.equals("RedRainbow", ignoreCase = true) -> ColorUtils.redRainbow(400000000L * index).rgb
                                colorMode.equals("BlueRainbow", ignoreCase = true) -> ColorUtils.blueRainbow(400000000L * index).rgb
                                colorMode.equals("GreenRainbow", ignoreCase = true) -> ColorUtils.greenRainbow(400000000L * index).rgb
                                colorMode.equals("Rainbow3", ignoreCase = true) -> ColorManager.getRainbow2(2000, -(yPos * rainbow3Offset.get().toFloat()).toInt())
                                colorMode.equals("Astolfo", ignoreCase = true) -> ColorManager.astolfoRainbow(delay[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                                colorMode.equals("riserainbow", ignoreCase = true) -> ColorUtils.hslRainbow(index+1,indexOffset=100*rainbowSpeed.get()).rgb
                                else -> customColor
                            }, textShadow)
                    }

                    if (!rectMode.equals("none", true)) {
                        val rectRainbow = rectColorMode.equals("Rainbow", ignoreCase = true)
                        RainbowShader.begin(rectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                            val rectColor = when {
                                rectRainbow -> 0
                                rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                rectColorMode.equals("CustomRainbow", ignoreCase = true) -> customRectRainbowColor.rgb
                                rectColorMode.equals("OtherRainbow", ignoreCase = true) -> Colors.getRainbow(-2000, (yPos * 8.toFloat()).toInt())
                                rectColorMode.equals("Rainbow2", true) -> ColorUtils.rainbow3(400000000L * index, customRainbowSpeed.get(), 1F).rgb
                                rectColorMode.equals("Bainbow", ignoreCase = true) -> braibow
                                rectColorMode.equals("Rainbow3", ignoreCase = true) -> ColorManager.getRainbow2(2000, -(yPos * rainbow3Offset.get().toFloat()).toInt())
                                rectColorMode.equals("Astolfo", ignoreCase = true) -> ColorManager.astolfoRainbow(delay[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                                else -> rectCustomColor
                            }

                            when {
                                rectMode.equals("left", true) -> RenderUtils.drawRect(module.translate.x - (2 + rectWidth.get()), module.translate.y, module.translate.x - 2, module.translate.y + textHeight,
                                    rectColor)
                                rectMode.equals("right", true) -> RenderUtils.drawRect(-3F, module.translate.y, 0F,
                                    module.translate.y + textHeight, rectColor)
                                rectMode.equals("OutLine", true) -> {
                                    //RenderUtils.drawRect(-1F, module.translate.y - 1F, 0F,module.translate.y + textHeight, rectColor)//右条
                                    RenderUtils.drawRect(module.translate.x - outLineRectWidth.get(), module.translate.y ,module.translate.x - outLineRectWidth.get() + 1, module.translate.y + textHeight,
                                        rectColor)//左条
                                    if (module == modules[0]) {
                                        Gui.drawRect((xPos - outLineRectWidth.get()).toInt(), yPos.toInt(), 0, yPos.toInt() - 1, rectColor) //上条
                                    }
                                    if (module != modules[0]) {
                                        var displayStrings = if (!tags.get())
                                            modules[index - 1].fakeName(fakeName.get())
                                        else if (tagsArrayColor.get())
                                            modules[index - 1].colorlessTagName(fakeName.get())
                                        else modules[index - 1].tagName(fakeName.get())

                                        if (upperCaseValue.get())
                                            displayStrings = displayStrings.toUpperCase()

                                        RenderUtils.drawRect(module.translate.x - outLineRectWidth.get() - (fontRenderer.getStringWidth(displayStrings) - fontRenderer.getStringWidth(displayString)), module.translate.y, module.translate.x - outLineRectWidth.get() + 1, module.translate.y + 2,
                                            rectColor) //功能左条和下条间隔
                                        if (module == modules[modules.size - 1]) {
                                            RenderUtils.drawRect(xPos - outLineRectWidth.get(), yPos + textHeight, 0.0F, yPos + textHeight + 1,
                                                rectColor) //下条
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Horizontal.LEFT -> {
                modules.forEachIndexed { index, module ->
                    var displayString = if (!tags.get())
                        module.fakeName(fakeName.get())
                    else if (tagsArrayColor.get())
                        module.colorlessTagName(fakeName.get())
                    else module.tagName(fakeName.get())

                    if (upperCaseValue.get())
                        displayString = displayString.toUpperCase()

                    val width = fontRenderer.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + if (rectMode.equals("left", true)) 5 else 2
                    val yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                            if (side.vertical == Vertical.DOWN) index + 1 else index
                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

                    val backgroundRectRainbow = backgroundColorMode.equals("Rainbow", ignoreCase = true)
                    val customRainbowColour = Palette.fade2(Color(customColor),modules.indexOf(module),fontRenderer.FONT_HEIGHT)
                    val customRectRainbowColor = Palette.fade2(Color(customColor),modules.indexOf(module),fontRenderer.FONT_HEIGHT)
                    val LiquidSlowly = ColorUtils1.LiquidSlowly(System.nanoTime(), index * RianbowspeedValue.get(), Rsaturation, Rbrightness)?.rgb
                    val c: Int = LiquidSlowly!!
                    val col = Color(c)
                    val braibow = Color(Rianbowr.get(), col.getGreen() / 2 + Rianbowb.get(), col.getGreen() / 2 + Rianbowb.get() + Rianbowg.get()).rgb
                    RainbowShader.begin(backgroundRectRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        RenderUtils.drawRect(
                            0F,
                            yPos,
                            xPos + width + when (rectMode) {
                                "Right" -> {
                                    5
                                }
                                "OutLine" -> {
                                    outLineRectWidth.get() - width
                                }
                                else -> {
                                    2
                                }
                            },
                            yPos + textHeight, when {
                                backgroundRectRainbow -> 0
                                backgroundColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                else -> backgroundCustomColor
                            }
                        )
                    }

                    val rainbow = colorMode.equals("Rainbow", ignoreCase = true)

                    RainbowFontShader.begin(rainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        fontRenderer.drawString(displayString, xPos, yPos + textY, when {
                            rainbow -> 0
                            colorMode.equals("Random", ignoreCase = true) -> moduleColor
                            colorMode.equals("CustomRainbow", ignoreCase = true) -> customRainbowColour.rgb
                            colorMode.equals("OtherRainbow", ignoreCase = true) -> Palette.fade(Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get()), 100, LiquidBounce.moduleManager.modules.indexOf(module) * 2 + 10, 2F).rgb
                            colorMode.equals("Bainbow", ignoreCase = true) -> braibow
                            colorMode.equals("Rainbow2", true) -> ColorUtils.rainbow3(400000000L * index, customRainbowSpeed.get(), 1F).rgb
                            colorMode.equals("Rainbow3", ignoreCase = true) -> ColorManager.getRainbow2(2000, -(yPos * rainbow3Offset.get().toFloat()).toInt())
                            colorMode.equals("Astolfo", ignoreCase = true) -> ColorManager.astolfoRainbow(delay[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                            else -> customColor
                        }, textShadow)
                    }

                    val rectColorRainbow = rectColorMode.equals("Rainbow", ignoreCase = true)

                    RainbowShader.begin(rectColorRainbow, if (rainbowX.get() == 0.0F) 0.0F else 1.0F / rainbowX.get(), if (rainbowY.get() == 0.0F) 0.0F else 1.0F / rainbowY.get(), System.currentTimeMillis() % 10000 / 10000F).use {
                        if (!rectMode.equals("none", true)) {
                            val rectColor = when {
                                rectColorRainbow -> 0
                                rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                                rectColorMode.equals("CustomRainbow", ignoreCase = true) -> customRectRainbowColor.rgb
                                rectColorMode.equals("OtherRainbow", ignoreCase = true) -> Palette.fade(Color(rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(), rectColorAlphaValue.get()), 100, LiquidBounce.moduleManager.modules.indexOf(module) * 2 + 10, 2F).rgb
                                rectColorMode.equals("Bainbow", ignoreCase = true) -> braibow
                                rectColorMode.equals("Rainbow2", true) -> ColorUtils.rainbow3(400000000L * index, customRainbowSpeed.get(), 1F).rgb
                                rectColorMode.equals("Rainbow3", ignoreCase = true) -> ColorManager.getRainbow2(2000, -(yPos * rainbow3Offset.get().toFloat()).toInt())
                                rectColorMode.equals("Astolfo", ignoreCase = true) -> ColorManager.astolfoRainbow(delay[0] * 100, astolfoRainbowOffset.get(), astolfoRainbowIndex.get())
                                else -> rectCustomColor
                            }

                            when {
                                rectMode.equals("left", true) -> RenderUtils.drawRect(0F,
                                    yPos - 1, 3F, yPos + textHeight, rectColor)
                                rectMode.equals("right", true) ->
                                    RenderUtils.drawRect(xPos + width + 2, yPos, xPos + width + 2 + 3,
                                        yPos + textHeight, rectColor)
                            }
                        }
                    }
                }
            }
        }

        // Draw border
        if (mc.currentScreen is GuiHudDesigner) {
            x2 = Int.MIN_VALUE

            if (modules.isEmpty()) {
                return if (side.horizontal == Horizontal.LEFT)
                    Border(0F, -1F, 20F, 20F)
                else
                    Border(0F, -1F, -20F, 20F)
            }

            for (module in modules) {
                when (side.horizontal) {
                    Horizontal.RIGHT, Horizontal.MIDDLE -> {
                        val xPos = -module.slide.toInt() - 2
                        if (x2 == Int.MIN_VALUE || xPos < x2) x2 = xPos
                    }
                    Horizontal.LEFT -> {
                        val xPos = module.slide.toInt() + 14
                        if (x2 == Int.MIN_VALUE || xPos > x2) x2 = xPos
                    }
                }
            }
            y2 = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) * modules.size

            return Border(0F, 0F, x2 - 7F, y2 - if (side.vertical == Vertical.DOWN) 1F else 0F)
        }

        AWTFontRenderer.assumeNonVolatile = false
        GlStateManager.resetColor()
        return null
    }

    override fun updateElement() {
        modules = LiquidBounce.moduleManager.modules
            .filter { it.array && it.slide > 0 }
            .sortedBy { -fontValue.get().getStringWidth(if (upperCaseValue.get()) (if (!tags.get()) it.fakeName(fakeName.get()) else if (tagsArrayColor.get()) it.colorlessTagName(fakeName.get()) else it.tagName(fakeName.get())).toUpperCase() else if (!tags.get()) it.fakeName(fakeName.get()) else if (tagsArrayColor.get()) it.colorlessTagName(fakeName.get()) else it.tagName(fakeName.get())) }
    }
}