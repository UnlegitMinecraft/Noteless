/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module



import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.script.api.ScriptModule
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.ccbluex.liquidbounce.utils.render.Translate
import net.ccbluex.liquidbounce.value.Value
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard

@SideOnly(Side.CLIENT)
open class Module : MinecraftInstance(), Listenable {
    val translate = Translate(0F,0F)
    val tab = Translate(0f , 0f)
    var expanded: Boolean = false
    var toggleButtonAnimation = 218f
    private var suffix: String? = null
    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    var fakeName: String
    var animation = 0F
    var name: String
    var spacedName: String
    var description: String
    var category: ModuleCategory
    var arrayY = 0F
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {arrayY
            field = keyBind

            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }
    private val canEnable: Boolean

    var slideStep = 0F

    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!
        fakeName = if(moduleInfo.fakeName.isEmpty() || this is ScriptModule) moduleInfo.name else moduleInfo.fakeName
        name = moduleInfo.name
        description = moduleInfo.description
        spacedName = if (moduleInfo.spacedName == "") name else moduleInfo.spacedName
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value) return

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!LiquidBounce.isStarting) {
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.click"), if(value) 1F else 0.5F))
            }

            // Call on enabled or disabled
            if (value) {
                onEnable()

                if (canEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            // Save module state
            if (!LiquidBounce.isStarting)
                LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.modulesConfig)
        }


    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F
    // Tag
    open val tag: String?
        get() = null

    open fun tagName(nameBreak: Boolean) : String {
        if(nameBreak && !name.contains("ScriptModule") && !fakeName.contains("ScriptModule")) {
            return "$fakeName${if (tag == null) "" else "§7 [$tag]"}"
        }
        return "$name${if (tag == null) "" else "§7 [$tag]"}"
    }

    fun colorlessTagName(nameBreak: Boolean) : String {
        if(nameBreak) {
            return "$fakeName${if (tag == null) "" else "[" + stripColor(tag)}"
        }
        return "$name${if (tag == null) "" else "§[" + stripColor(tag)}"
    }

    fun fakeName(nameBreak: Boolean) : String {
        if(nameBreak && !name.contains("ScriptModule") && !fakeName.contains("ScriptModule")) {
            return fakeName
        }
        return name
    }
    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = javaClass.declaredFields.map { valueField ->
        valueField.isAccessible = true
        valueField[this]
    }.filterIsInstance<Value<*>>().find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
    open fun onEnalbe() {}
}