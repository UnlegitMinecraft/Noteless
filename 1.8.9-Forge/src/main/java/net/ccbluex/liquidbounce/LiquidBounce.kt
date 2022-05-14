/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce


import net.ccbluex.liquidbounce.cape.CapeAPI.registerCapeService
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.CombatManager
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.tabs.BlocksTab
import net.ccbluex.liquidbounce.tabs.ExploitsTab
import net.ccbluex.liquidbounce.tabs.HeadsTab
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClassUtils.hasForge
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.NativeLoader
import net.ccbluex.liquidbounce.utils.misc.QQUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.network.FMLEventChannel

object LiquidBounce {
    lateinit var Users: String
    // Client information
    //121132

    const val CLIENT_NAME = "Noteless"
    const val CLIENT_VERSION = "211221"
    const val IN_DEV = false
    const val CLIENT_CREATOR = "CCBlueX"
    const val MINECRAFT_VERSION = "1.8.9"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"
    val backGround = ResourceLocation("Insane/Background.png")
    var isStarting = false
    var Yaw = 0f
    var Pitch = 0f
    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var combatManager: CombatManager
    var channel: FMLEventChannel? = null
    // HUD & ClickGUI
    lateinit var hud: HUD
    lateinit var module: Module
    lateinit var clickGui: ClickGui
    // Update information
    var latestVersion = ""
    var displayedUpdateScreen=false
    // Menu Background
    var background: ResourceLocation? = null



    /**
     * Execute if client will be started
     */
    fun startClient() {
        ClientUtils.getLogger().info("Load DLL");

        NativeLoader.loadDLL("native/ucrtbased.dll","ucrtbased.dll")
        NativeLoader.loadDLL("native/vcruntime140d.dll","vcruntime140d.dll")
        NativeLoader.loadDLL("native/vcruntime140_1d.dll","vcruntime140_1d.dll")
        NativeLoader.loadDLL("native/msvcp140d.dll","msvcp140d.dll")
        NativeLoader.loadDLL("native/concrt140d.dll","concrt140d.dll")
        NativeLoader.loadDLL("load64.dll","Login.dll")
        ClientUtils.getLogger().info("Starting $CLIENT_NAME b$CLIENT_VERSION, by $CLIENT_CREATOR")

        isStarting = true
        //     Verify.sendWindowsMessageLogin()
        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Load client fonts
        Fonts.loadFonts()



        // Create command manager 指令列表
        commandManager = CommandManager()

        // ScriptManager
        scriptManager = ScriptManager()
        // Setup module manager and register modules 注册功能
        moduleManager = ModuleManager()

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }
        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }

        // Setup Discord RPC
//        try {
//            clientRichPresence = ClientRichPresence()
//            clientRichPresence.setup()
//        } catch (throwable: Throwable) {
//            ClientgetLogger().error("Failed to setup Discord RPC.", throwable)
//        }

        combatManager =CombatManager()
        eventManager.registerListener(LiquidBounce.combatManager);
        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()
        // Set is starting status
        isStarting = false
    }


    /**
     * Execute if client will be stopped
     */
    fun stopClient() {

        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

        // Shutdown discord rpc
    }

}