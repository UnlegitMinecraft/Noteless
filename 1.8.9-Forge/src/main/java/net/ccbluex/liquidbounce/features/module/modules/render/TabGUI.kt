package net.ccbluex.liquidbounce.features.module.modules.render

import antiskidderobfuscator.NativeMethod
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Translate
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo(name = "TabGui", description = "Toggles visibility of the HUD.", category = ModuleCategory.RENDER, array = false)
class TabGui : Module() {

    private val x = 5f
    private val y = 50f
    private val width = 75f
    private val height = 78.5f

    private var openModuleGui = false;
    private var selectedCategory = 0
    private var selecteModuleindex = 0

    private var selecteModule = emptyList<Module>()
    private var Modulecategory = mutableListOf<AnimaitonCategory>()

    private val categoryAnimaiton = Translate(0f, 0f)
    private val moduleAnimaiton = Translate(0f, 0f)
    private var categoryPositonY = 0f
    private var ModulePositonY = 0f

    init {
        for(index in  0..ModuleCategory.values().lastIndex) {
            val animationcategory = AnimaitonCategory(ModuleCategory.values()[index].namee , Translate(0f , 0f))
            Modulecategory.add(animationcategory)
        }
    }
    @NativeMethod
    @EventTarget
    fun rendertabGui(evnet: Render2DEvent) {

        updateBackGound()
        enabler()
        enablerScissorBox()

        categoryAnimaiton.translate(0f , selectedCategory * 15f , 2.0)
        val categorysupery = if(categoryAnimaiton.y - 60f > 0) categoryAnimaiton.y - 60f else 0f

        RenderUtils.drawRect(x, y + categoryAnimaiton.y - categorysupery, width + 5, y + categoryAnimaiton.y + 17f - categorysupery, Color(55 , 55 , 55 , 50 ))

        categoryPositonY = 0f
        Modulecategory.forEachIndexed { index, category ->

            category.animation.translate(if(selectedCategory == index) 15f else 5f , 0f ,  2.0 )

            val font = Fonts.jello
            font.drawString(category.displayname, x + category.animation.x , y + 5f + categoryPositonY - categorysupery, -1)
            categoryPositonY += 15f
        }

        disablerScissorBox()
        disabler()


        enabler()
        moduleAnimaiton.translate(0f , selecteModuleindex * 15f , 2.0)

        val modulesupery = if(moduleAnimaiton.y - 150f > 0) moduleAnimaiton.y - 150f else 0f
        val positiony = (if(ModulePositonY >= 165) 165f else ModulePositonY)

        if(openModuleGui) {
            RenderUtils.drawShadow(x + width + 10, y,  width + 5,  positiony + 2f)
            BlurUtils.blurArea(x + width + 90f, y, width + 15, positiony + 52.5f, 10F)
            RenderUtils.drawRect(x + width + 10, y + moduleAnimaiton.y - modulesupery, x + width + width + 15, y + moduleAnimaiton.y - modulesupery + 17f, Color(55, 55, 55, 50))
        }

        RenderUtils.makeScissorBox(x + width + 10, y,  x + width + width + 15, y + positiony)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        ModulePositonY = 0f
        selecteModule.forEachIndexed{index , module ->
            module.tab.translate(if(selecteModuleindex == index) 15f else 5f , 0f , 2.0)
            val font = if(module.state) Fonts.jellom else Fonts.jello
            font.drawString(module.name, x + width + 10 + module.tab.x, y + 5f + ModulePositonY - modulesupery , -1)
            ModulePositonY += 15f
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        disabler()
    }


    private fun updateBackGound() {
        RenderUtils.drawShadow(x , y , width , height - 1)
        BlurUtils.blurArea(x, y, width+4.9f, height + 49.5f,10F)
    }

    private fun enablerScissorBox() {
        RenderUtils.makeScissorBox(x, y,x + width, y + height )
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
    }

    private fun disablerScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }

    private fun enabler() {
        GlStateManager.pushMatrix()
    }

    private fun disabler() {
        GlStateManager.popMatrix()
    }


    @EventTarget
    fun keyevent(event : KeyEvent) {
        val key = event.key
        handleKey(key)
    }

    private fun handleKey(keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_UP  -> { parseAction(Action.UP) }
            Keyboard.KEY_DOWN -> { parseAction(Action.DOWN) }
            Keyboard.KEY_LEFT -> { parseAction(Action.LEFT) }
            Keyboard.KEY_RIGHT -> { parseAction(Action.RIGHT) }
            Keyboard.KEY_RETURN-> { parseAction(Action.TOGGLE) }
        }
    }

    private fun parseAction(action: Action) {
        when (action) {
            Action.UP -> {
                if (selectedCategory > 0 && !openModuleGui) selectedCategory--
                if (selecteModuleindex > 0) selecteModuleindex--
            }
            Action.DOWN -> {
                if (selectedCategory < Modulecategory.lastIndex && !openModuleGui) selectedCategory++
                if (selecteModuleindex < selecteModule.lastIndex) selecteModuleindex++
            }
            Action.LEFT -> {
                if(openModuleGui) {
                    openModuleGui = false
                    selecteModuleindex = 0
                    selecteModule = listOf()
                }
            }
            Action.RIGHT -> {
                if(!openModuleGui) {
                    openModuleGui = true
                    selecteModule = LiquidBounce.moduleManager.modules.filter { it.category == ModuleCategory.values()[selectedCategory]}.sortedBy { 0 }
                }
            }
            Action.TOGGLE -> {
                if(openModuleGui) {
                    val selecetd = selecteModule[selecteModuleindex]
                    selecetd.toggle()
                }
            }
        }
    }

    /**
     * TabGUI Action
     */
    enum class Action { UP, DOWN, LEFT, RIGHT, TOGGLE }

    class AnimaitonCategory(var displayname : String , var animation : Translate) {}
}
