/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module


import Insane.modules.render.PointerESP
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.render.ColorMixer
import skidunion.destiny.module.impl.player.ChatBypass
import java.util.*


class ModuleManager : Listenable {
    var pendingBindModule: Module? = null

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
            Criticals::class.java,
            AutoArmor::class.java,
            ChatBypass::class.java,
            AutoLogin::class.java,
            AutoL::class.java,
            ResetVL::class.java,
            Particles::class.java,
            KickWarn::class.java,
            AutoKit::class.java,
            AutoPit::class.java,
            PacketFixer::class.java,
            TabGui::class.java,
            ItemPhysics::class.java,
            TargetHUD::class.java,
            SessionInfo::class.java,
            ChestESP::class.java,
            TPAura::class.java,
            NoRender::class.java,
            AutoPit::class.java,
            AntiVoid::class.java,
            PacketGraph::class.java,
            AutoHead::class.java,
            PointerESP::class.java,
            Dab::class.java,
            ColorMixer::class.java,
            Playerlist::class.java,
            Radar::class.java,
            AntiFireBall::class.java,
            AutoPot::class.java,
            AutoSoup::class.java,
            AutoWeapon::class.java,
            BowAimbot::class.java,
            KillAura::class.java,
            Velocity::class.java,
            Fly::class.java,
            ClickGUI::class.java,
            HighJump::class.java,
                InvMove::class.java,
            NoSlow::class.java,
            LiquidWalk::class.java,
            SafeWalk::class.java,
            WallClimb::class.java,
            Strafe::class.java,
            Sprint::class.java,
            Teams::class.java,
            NoRotateSet::class.java,
            AntiBot::class.java,
            ChestStealer::class.java,
            Scaffold::class.java,
            CivBreak::class.java,
            Tower::class.java,
            FPSHurtCam::class.java,
            GetName::class.java,
            SpeedMine::class.java,
            AttackEffects::class.java,
            FastPlace::class.java,
            ESP::class.java,
            Speed::class.java,
            Tracers::class.java,
            NameTags::class.java,
            DMGParticles::class.java,
            FastUse::class.java,
            Fullbright::class.java,
            ItemESP::class.java,
            StorageESP::class.java,
            Projectiles::class.java,
            NoClip::class.java,
            Nuker::class.java,
            PingSpoof::class.java,
            FastClimb::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            NoWeb::class.java,
            Spammer::class.java,
            IceSpeed::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            NameProtect::class.java,
            NoHurtCam::class.java,
            MidClick::class.java,
            XRay::class.java,
            Timer::class.java,
            Sneak::class.java,
            GhostHand::class.java,
            AutoWalk::class.java,
            AutoBreak::class.java,
            FreeCam::class.java,
            Aimbot::class.java,
            Eagle::class.java,
            HitBox::class.java,
            AntiCactus::class.java,
            Plugins::class.java,
            LongJump::class.java,
            Parkour::class.java,
            Ambience::class.java,
            MultiActions::class.java,
            AirJump::class.java,
            AutoClicker::class.java,
            NoBob::class.java,
            BlockOverlay::class.java,
            BlockESP::class.java,
            Chams::class.java,
            Phase::class.java,
            ServerCrasher::class.java,
            NoFOV::class.java,
            FastStairs::class.java,
            SwingAnimation::class.java,
            VClip::class.java,
            InvCleaner::class.java,
            TrueSight::class.java,
            AntiBlind::class.java,
            NoSwing::class.java,
            GlowESP::class.java,
            Breadcrumbs::class.java,
            AbortBreaking::class.java,
            PotionSaver::class.java,
            CameraClip::class.java,
            WaterSpeed::class.java,
            SlimeJump::class.java,
            MoreCarry::class.java,
            NoPitchLimit::class.java,
            Kick::class.java,
            Liquids::class.java,
            AtAllProvider::class.java,
            SuperKnockback::class.java,
            ProphuntESP::class.java,
            AutoFish::class.java,
            Damage::class.java,
            Freeze::class.java,
            VehicleOneHit::class.java,
            Reach::class.java,
            NoJumpDelay::class.java,
            BlockWalk::class.java,
            AntiAFK::class.java,
            HUD::class.java,
            TNTESP::class.java,
            ComponentOnHover::class.java,
            SessionInfo::class.java,
            AntiAim::class.java,
            Teleport::class.java,
            ResourcePackSpoof::class.java,
            NoSlowBreak::class.java,
            AutoPlay::class.java,
            PortalMenu::class.java,
            AntiStaff::class.java,
            BlockAnimations::class.java,
            PlayerEdit::class.java,
            Disabler::class.java,
            CapeManager::class.java,
            EnchantEffect::class.java,
            NewXRay::class.java,
            TargetStrafe::class.java,
            Wings::class.java
        )
        registerModule(Rotations)
        registerModule(NoScoreboard)
        registerModule(Fucker)
        registerModule(ChestAura)
        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]Mix
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger()
                .error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    fun getModuleInCategory(Category: ModuleCategory): ArrayList<Module> {
        val moduleInCategory = ArrayList<Module>()
        for (i in this.modules) {
            if (i.category != Category)
                continue
            moduleInCategory.add(i)
        }
        return moduleInCategory
    }
    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
