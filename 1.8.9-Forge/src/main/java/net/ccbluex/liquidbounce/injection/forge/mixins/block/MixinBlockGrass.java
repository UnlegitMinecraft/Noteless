package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.NewXRay;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;

@SideOnly(Side.CLIENT)
@Mixin(BlockGrass.class)
public abstract class MixinBlockGrass extends MixinBlock{

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        if (LiquidBounce.moduleManager.getModule(NewXRay.class).getState()) {
            return EnumWorldBlockLayer.TRANSLUCENT;
        }else {
            return EnumWorldBlockLayer.CUTOUT_MIPPED;
        }
    }

}
