package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiPlayerTabOverlay.class)
public interface IGuiPlayerTabOverlay {
    @Invoker("drawPing")
     void drawPingInvoke(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn);
}
