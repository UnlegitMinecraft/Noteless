package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI.*
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Horizontal
import net.ccbluex.liquidbounce.ui.client.hud.element.Side.Vertical
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.AnimationUtils
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color

import org.lwjgl.opengl.GL11

/**
 * CustomHUD Arraylist element
 *
 * Shows a list of enabled modules
 */
@ElementInfo(name = "Arraylist", single = true)
class Arraylist(x: Double = 1.0, y: Double = 2.0, scale: Float = 1F,
                side: Side = Side(Horizontal.RIGHT, Vertical.UP)) : Element(x, y, scale, side) {
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Random", "Sky", "CRainbow", "LiquidSlowly", "Fade"), "Custom")
    private val blurValue = BoolValue("Blur", false)
    private val blurStrength = FloatValue("Blur-Strength", 0F, 0F, 30F)
    val colorAlphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val skyDistanceValue = IntegerValue("Sky-Distance", 2, 0, 4)
    private val cRainbowSecValue = IntegerValue("CRainbow-Seconds", 2, 1, 10)
    private val cRainbowDistValue = IntegerValue("CRainbow-Distance", 2, 1, 6)
    private val liquidSlowlyDistanceValue = IntegerValue("LiquidSlowly-Distance", 90, 1, 90)
    private val fadeDistanceValue = IntegerValue("Fade-Distance", 50, 1, 100)
    private val hAnimation = ListValue("HorizontalAnimation", arrayOf("Default", "None", "Slide", "Astolfo"), "Default")
    private val vAnimation = ListValue("VerticalAnimation", arrayOf("None", "LiquidSense", "Slide", "Rise", "Astolfo"), "None")
    private val animationSpeed = FloatValue("Animation-Speed", 0.25F, 0.01F, 1F)
    private val nameBreak = BoolValue("NameBreak", true)
    private val abcOrder = BoolValue("Alphabetical-Order", false)
    private val tags = BoolValue("Tags", true)
    private val tagsStyleValue = ListValue("TagsStyle", arrayOf("-", "|", "()", "[]", "<>", "Default"), "-")
    private val shadow = BoolValue("ShadowText", true)
    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 0, 0, 255)
    private val rectRightValue = ListValue("Rect-Right", arrayOf("None", "Left", "Right", "Outline", "Special", "Top"), "None")
    private val rectLeftValue = ListValue("Rect-Left", arrayOf("None", "Left", "Right"), "None")
    private val lowerCaseValue = BoolValue("LowerCase", false)
    private val spaceValue = FloatValue("Space", 0F, 0F, 5F)
    private val textHeightValue = FloatValue("TextHeight", 11F, 1F, 20F)
    private val textYValue = FloatValue("TextY", 1F, 0F, 20F)
    private val tagsArrayColor = BoolValue("TagsArrayColor", false)
    private val fontValue = FontValue("Font", Fonts.font40)

    private var x2 = 0
    private var y2 = 0F

    private var modules = emptyList<Module>()
    private var sortedModules = emptyList<Module>()

    override fun drawElement(): Border? {
        val fontRenderer = fontValue.get()
        val counter = intArrayOf(0)

        AWTFontRenderer.assumeNonVolatile = true

        // Slide animation - update every render
        val delta = RenderUtils.deltaTime

        // Draw arraylist
        val colorMode = colorModeValue.get()
        val rectColorMode = colorModeValue.get()
        val customColor = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get()).rgb
        val rectCustomColor = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get()).rgb
        val space = spaceValue.get()
        val textHeight = textHeightValue.get()
        val textY = textYValue.get()
        val backgroundCustomColor = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
                backgroundColorBlueValue.get(), backgroundColorAlphaValue.get()).rgb
        val textShadow = shadow.get()
        val textSpacer = textHeight + space
        val saturation = saturationValue.get()
        val brightness = brightnessValue.get()

        var inx = 0
        for (module in sortedModules) {
            val shouldAdd = module.array && module.slide > 0F
            // update slide y
            var yPos = (if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer) *
                    if (side.vertical == Vertical.DOWN) inx + 1 else inx

            if (shouldAdd) {
                if (vAnimation.get().equals("Rise", ignoreCase = true) && !module.state)
                    yPos = -fontRenderer.FONT_HEIGHT - textY

                val size = modules.size * 2.0E-2f

                when (vAnimation.get()) {
                    "LiquidSense" -> {
                        if (module.state) {
                            if (module.arrayY < yPos) {
                                module.arrayY += (size -
                                        Math.min(module.arrayY * 0.002f
                                                , size - (module.arrayY * 0.0001f) )) * delta
                                module.arrayY = Math.min(yPos, module.arrayY)
                            } else {
                                module.arrayY -= (size -
                                        Math.min(module.arrayY * 0.002f
                                                , size - (module.arrayY * 0.0001f) )) * delta
                                module.arrayY = Math.max(module.arrayY, yPos)
                            }
                        }
                    }
                    "Slide", "Rise" -> module.arrayY = AnimationUtils.animate(yPos.toDouble(), module.arrayY.toDouble(), animationSpeed.get().toDouble() * 0.025 * delta.toDouble()).toFloat()
                    "Astolfo" -> {
                        if (module.arrayY < yPos) {
                            module.arrayY += animationSpeed.get() / 2F * delta
                            module.arrayY = Math.min(yPos, module.arrayY)
                        } else {
                            module.arrayY -= animationSpeed.get() / 2F * delta
                            module.arrayY = Math.max(module.arrayY, yPos)
                        }
                    }
                    else -> module.arrayY = yPos
                }
                inx++
            } else if (!vAnimation.get().equals("rise", true)) //instant update
                module.arrayY = yPos
        }

        for (module in LiquidBounce.moduleManager.modules) {
            // update slide x
            if (!module.array || (!module.state && module.slide == 0F)) continue

            var displayString = getModName(module)

            val width = fontRenderer.getStringWidth(displayString)

            when (hAnimation.get()) {
                "Astolfo" -> {
                    if (module.state) {
                        if (module.slide < width) {
                            module.slide += animationSpeed.get() * delta
                            module.slideStep = delta / 1F
                        }
                    } else if (module.slide > 0) {
                        module.slide -= animationSpeed.get() * delta
                        module.slideStep = 0F
                    }

                    if (module.slide > width) module.slide = width.toFloat()
                }
                "Slide" -> {
                    if (module.state) {
                        if (module.slide < width) {
                            module.slide = AnimationUtils.animate(width.toDouble(), module.slide.toDouble(), animationSpeed.get().toDouble() * 0.025 * delta.toDouble()).toFloat()
                            module.slideStep = delta / 1F
                        }
                    } else if (module.slide > 0) {
                        module.slide = AnimationUtils.animate(-width.toDouble(), module.slide.toDouble(), animationSpeed.get().toDouble() * 0.025 * delta.toDouble()).toFloat()
                        module.slideStep = 0F
                    }
                }
                "Default" -> {
                    if (module.state) {
                        if (module.slide < width) {
                            module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                            module.slideStep += delta / 4F
                        }
                    } else if (module.slide > 0) {
                        module.slide = AnimationUtils.easeOut(module.slideStep, width.toFloat()) * width
                        module.slideStep -= delta / 4F
                    }
                }
                else -> {
                    module.slide = if (module.state) width.toFloat() else 0f
                    module.slideStep += (if (module.state) delta else -delta).toFloat()
                }
            }

            module.slide = module.slide.coerceIn(0F, width.toFloat())
            module.slideStep = module.slideStep.coerceIn(0F, width.toFloat())
        }

        when (side.horizontal) {
            Horizontal.RIGHT, Horizontal.MIDDLE -> {
                if (blurValue.get()) {
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    GL11.glPushMatrix()
                    val floatX = renderX.toFloat()
                    val floatY = renderY.toFloat()
                    var yP = 0F
                    var xP = 0F
                    modules.forEachIndexed { index, module ->
                        val dString = getModName(module)
                        val wid = fontRenderer.getStringWidth(dString) + 2F
                        val yPos = if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer *
                                if (side.vertical == Vertical.DOWN) index + 1 else index
                        yP += yPos
                        xP = Math.min(xP, -wid)
                    }

                    BlurUtils.preCustomBlur(blurStrength.get(), floatX, floatY, floatX + xP, floatY + yP, false)
                    modules.forEachIndexed { index, module ->
                        val xPos = -module.slide - 2
                        RenderUtils.quickDrawRect(
                                floatX + xPos - if (rectRightValue.get().equals("right", true)) 3 else 2,
                                floatY + module.arrayY,
                                floatX + if (rectRightValue.get().equals("right", true)) -1F else 0F,
                                floatY + module.arrayY + textHeight
                        )
                    }
                    BlurUtils.postCustomBlur()
                    GL11.glPopMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                }
/*
                if (shadowValue.get()) {
                    val boostedColor = Color(backgroundColorRedValue.get().toFloat() / 255.0F, backgroundColorGreenValue.get().toFloat() / 255.0F,
                            backgroundColorBlueValue.get().toFloat() / 255.0F, (backgroundColorAlphaValue.get().toFloat() / 255.0F * 1.85F).coerceIn(0F, 1F)).rgb
                    val floatX = renderX.toFloat()
                    val floatY = renderY.toFloat()
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    GL11.glPushMatrix()
                    GlStateManager.pushAttrib()
                    BlurUtils.downscale(true, shadowStrength.get())
                    modules.forEachIndexed { index, module ->
                        val xPos = -module.slide - 2
                        RenderUtils.newDrawRect(
                                floatX + xPos - if (rectRightValue.get().equals("right", true)) 3 else 2,
                                floatY + module.arrayY,
                                floatX + if (rectRightValue.get().equals("right", true)) -1F else 0F,
                                floatY + module.arrayY + textHeight,
                                boostedColor
                        )
                    }
                    BlurUtils.downscale(false, shadowStrength.get())
                    GlStateManager.popAttrib()
                    GL11.glPopMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                }
*/
                modules.forEachIndexed { index, module ->
                    var displayString = getModName(module)

                    val width = fontRenderer.getStringWidth(displayString)
                    val xPos = -module.slide - 2

                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

                    var Sky: Int
                    Sky = RenderUtils.SkyRainbow(counter[0] * (skyDistanceValue.get() * 50), saturationValue.get(), brightnessValue.get())
                    var CRainbow: Int
                    CRainbow = RenderUtils.getRainbowOpaque(cRainbowSecValue.get(), saturationValue.get(), brightnessValue.get(), counter[0] * (50 * cRainbowDistValue.get()))
                    var FadeColor: Int = ColorUtils.fade(Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get()), index * fadeDistanceValue.get(), 100).rgb
                    counter[0] = counter[0] - 1

                    val test = ColorUtils.LiquidSlowly(System.nanoTime(), index * liquidSlowlyDistanceValue.get(), saturationValue.get(), brightnessValue.get())?.rgb
                    var LiquidSlowly : Int = test!!


                    RenderUtils.drawRect(
                            xPos - if (rectRightValue.get().equals("right", true)) 3 else 2,
                            module.arrayY,
                            if (rectRightValue.get().equals("right", true)) -1F else 0F,
                            module.arrayY + textHeight, backgroundCustomColor
                    )

                    fontRenderer.drawString(displayString, xPos - if (rectRightValue.get().equals("right", true)) 1 else 0, module.arrayY + textY, when {
                        colorMode.equals("Random", ignoreCase = true) -> moduleColor
                        colorMode.equals("Sky", ignoreCase = true) -> Sky
                        colorMode.equals("CRainbow", ignoreCase = true) -> CRainbow
                        colorMode.equals("LiquidSlowly", ignoreCase = true) -> LiquidSlowly
                        colorMode.equals("Fade", ignoreCase = true) -> FadeColor
                        else -> customColor
                    }, textShadow)


                    if (!rectRightValue.get().equals("none", true)) {
                        val rectColor = when {
                            rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            rectColorMode.equals("Sky", ignoreCase = true) -> Sky
                            rectColorMode.equals("CRainbow", ignoreCase = true) -> CRainbow
                            rectColorMode.equals("LiquidSlowly", ignoreCase = true) -> LiquidSlowly
                            rectColorMode.equals("Fade", ignoreCase = true) -> FadeColor
                            else -> rectCustomColor
                        }

                        when {
                            rectRightValue.get().equals("left", true) -> RenderUtils.drawRect(xPos - 3, module.arrayY, xPos - 2, module.arrayY + textHeight,
                                    rectColor)
                            rectRightValue.get().equals("right", true) -> RenderUtils.drawRect(-1F, module.arrayY, 0F,
                                    module.arrayY + textHeight, rectColor)
                            rectRightValue.get().equals("outline", true) -> {
                                RenderUtils.drawRect(-1F, module.arrayY - 1F, 0F,
                                        module.arrayY + textHeight, rectColor)
                                RenderUtils.drawRect(xPos - 3, module.arrayY, xPos - 2, module.arrayY + textHeight,
                                        rectColor)
                                if (module != modules[0]) {
                                    var displayStrings = getModName(modules[index - 1])

                                    RenderUtils.drawRect(xPos - 3 - (fontRenderer.getStringWidth(displayStrings) - fontRenderer.getStringWidth(displayString)), module.arrayY, xPos - 2, module.arrayY + 1,
                                            rectColor)
                                    if (module == modules[modules.size - 1]) {
                                        RenderUtils.drawRect(xPos - 3, module.arrayY + textHeight, 0.0F, module.arrayY + textHeight + 1,
                                                rectColor)
                                    }
                                } else {
                                    RenderUtils.drawRect(xPos - 3, module.arrayY, 0F, module.arrayY - 1, rectColor)
                                }
                            }
                            rectRightValue.get().equals("special", true) -> {
                                if (module == modules[0]) {
                                    RenderUtils.drawRect(xPos - 2, module.arrayY, 0F, module.arrayY - 1, rectColor)
                                }
                                if (module == modules[modules.size - 1]) {
                                    RenderUtils.drawRect(xPos - 2, module.arrayY + textHeight, 0F, module.arrayY + textHeight + 1, rectColor)
                                }
                            }
                            rectRightValue.get().equals("top", true) -> {
                                if (module == modules[0]) {
                                    RenderUtils.drawRect(xPos - 2, module.arrayY, 0F, module.arrayY - 1, rectColor)
                                }
                            }
                        }
                    }
                }
            }

            Horizontal.LEFT -> {
                if (blurValue.get()) {
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    GL11.glPushMatrix()
                    val floatX = renderX.toFloat()
                    val floatY = renderY.toFloat()
                    var yP = 0F
                    var xP = 0F
                    modules.forEachIndexed { index, module ->
                        val dString = getModName(module)
                        val wid = fontRenderer.getStringWidth(dString) + 2F
                        val yPos = if (side.vertical == Vertical.DOWN) -textSpacer else textSpacer *
                                if (side.vertical == Vertical.DOWN) index + 1 else index
                        yP += yPos
                        xP = Math.max(xP, wid)
                    }

                    BlurUtils.preCustomBlur(blurStrength.get(), floatX, floatY, floatX + xP, floatY + yP, false)
                    modules.forEachIndexed { index, module ->
                        var displayString = getModName(module)
                        val width = fontRenderer.getStringWidth(displayString)
                        val xPos = -(width - module.slide) + if (rectLeftValue.get().equals("left", true)) 3 else 2

                        RenderUtils.quickDrawRect(
                                floatX,
                                floatY + module.arrayY,
                                floatX + xPos + width + if (rectLeftValue.get().equals("right", true)) 3 else 2,
                                floatY + module.arrayY + textHeight
                        )

                    }
                    BlurUtils.postCustomBlur()
                    GL11.glPopMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                }
/*
                if (shadowValue.get()) {
                    val boostedColor = Color(backgroundColorRedValue.get().toFloat() / 255.0F, backgroundColorGreenValue.get().toFloat() / 255.0F,
                            backgroundColorBlueValue.get().toFloat() / 255.0F, (backgroundColorAlphaValue.get().toFloat() / 255.0F * 1.85F).coerceIn(0F, 1F)).rgb
                    val floatX = renderX.toFloat()
                    val floatY = renderY.toFloat()
                    GL11.glTranslated(-renderX, -renderY, 0.0)
                    GL11.glPushMatrix()
                    GlStateManager.pushAttrib()
                    BlurUtils.downscale(true, shadowStrength.get())
                    modules.forEachIndexed { index, module ->
                        var displayString = getModName(module)
                        val width = fontRenderer.getStringWidth(displayString)
                        val xPos = -(width - module.slide) + if (rectLeftValue.get().equals("left", true)) 3 else 2

                        RenderUtils.newDrawRect(
                                floatX,
                                floatY + module.arrayY,
                                floatX + xPos + width + if (rectLeftValue.get().equals("right", true)) 3 else 2,
                                floatY + module.arrayY + textHeight,
                                boostedColor
                        )
                    }
                    BlurUtils.downscale(false, shadowStrength.get())
                    GlStateManager.popAttrib()
                    GL11.glPopMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                }
*/
                modules.forEachIndexed { index, module ->
                    var displayString = getModName(module)

                    val width = fontRenderer.getStringWidth(displayString)
                    val xPos = -(width - module.slide) + if (rectLeftValue.get().equals("left", true)) 3 else 2

                    val moduleColor = Color.getHSBColor(module.hue, saturation, brightness).rgb

                    var Sky: Int
                    Sky = RenderUtils.SkyRainbow(counter[0] * (skyDistanceValue.get() * 50), saturationValue.get(), brightnessValue.get())
                    var CRainbow: Int
                    CRainbow = RenderUtils.getRainbowOpaque(cRainbowSecValue.get(), saturationValue.get(), brightnessValue.get(), counter[0] * (50 * cRainbowDistValue.get()))
                    var FadeColor: Int = ColorUtils.fade(Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get(), colorAlphaValue.get()), index * fadeDistanceValue.get(), 100).rgb
                    counter[0] = counter[0] - 1
                    val test = ColorUtils.LiquidSlowly(System.nanoTime(), index * liquidSlowlyDistanceValue.get(), saturationValue.get(), brightnessValue.get())?.rgb
                    var LiquidSlowly : Int = test!!


                    RenderUtils.drawRect(
                            0F,
                            module.arrayY,
                            xPos + width + if (rectLeftValue.get().equals("right", true)) 3 else 2,
                            module.arrayY + textHeight, backgroundCustomColor
                    )

                    fontRenderer.drawString(displayString, xPos, module.arrayY + textY, when {
                        colorMode.equals("Random", ignoreCase = true) -> moduleColor
                        colorMode.equals("Sky", ignoreCase = true) -> Sky
                        colorMode.equals("CRainbow", ignoreCase = true) -> CRainbow
                        colorMode.equals("LiquidSlowly", ignoreCase = true) -> LiquidSlowly
                        colorMode.equals("Fade", ignoreCase = true) -> FadeColor
                        else -> customColor
                    }, textShadow)

                    if (!rectLeftValue.get().equals("none", true)) {
                        val rectColor = when {
                            rectColorMode.equals("Random", ignoreCase = true) -> moduleColor
                            rectColorMode.equals("Sky", ignoreCase = true) -> Sky
                            rectColorMode.equals("CRainbow", ignoreCase = true) -> CRainbow
                            rectColorMode.equals("LiquidSlowly", ignoreCase = true) -> LiquidSlowly
                            rectColorMode.equals("Fade", ignoreCase = true) -> FadeColor
                            else -> rectCustomColor
                        }

                        when {
                            rectLeftValue.get().equals("left", true) -> RenderUtils.drawRect(0F,
                                    module.arrayY - 1, 1F, module.arrayY + textHeight, rectColor)
                            rectLeftValue.get().equals("right", true) ->
                                RenderUtils.drawRect(xPos + width + 2, module.arrayY, xPos + width + 2 + 1,
                                        module.arrayY + textHeight, rectColor)
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
        modules = if (abcOrder.get()) LiquidBounce.moduleManager.modules
                .filter { it.array && (if (hAnimation.get().equals("none", ignoreCase = true)) it.state else it.slide > 0) }
        else LiquidBounce.moduleManager.modules
                .filter { it.array && (if (hAnimation.get().equals("none", ignoreCase = true)) it.state else it.slide > 0) }
                .sortedBy { -fontValue.get().getStringWidth(getModName(it)) }
        sortedModules = if (abcOrder.get()) LiquidBounce.moduleManager.modules.toList()
        else LiquidBounce.moduleManager.modules.sortedBy { -fontValue.get().getStringWidth(getModName(it)) }.toList()
    }

    fun getModName(mod: Module): String {
        var modTag : String = ""
        if (tags.get() && mod.tag != null) {
            // add space
            modTag += " "

            // check and add gray prefix if possible
            if (!tagsArrayColor.get())
                modTag += "§7"

            // tag prefix, ignore default value
            if (!tagsStyleValue.get().equals("default", true))
                modTag += tagsStyleValue.get().get(0).toString() + if (tagsStyleValue.get().equals("-", true) || tagsStyleValue.get().equals("|", true)) " " else ""

            // main tag value
            modTag += mod.tag

            // tag suffix, ignore default, -, | values
            if (!tagsStyleValue.get().equals("default", true)
                    && !tagsStyleValue.get().equals("-", true)
                    && !tagsStyleValue.get().equals("|", true))
                modTag += tagsStyleValue.get().get(1).toString()

        }

        var displayName : String = (if (nameBreak.get()) mod.spacedName else mod.name) + modTag

        if (lowerCaseValue.get())
            displayName = displayName.toLowerCase()

        return displayName
    }
}