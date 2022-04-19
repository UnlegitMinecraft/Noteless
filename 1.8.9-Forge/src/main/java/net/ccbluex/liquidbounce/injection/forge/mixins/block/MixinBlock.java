
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.BlockBBEvent;
import net.ccbluex.liquidbounce.event.BlockRenderSideEvent;
import net.ccbluex.liquidbounce.features.module.modules.render.NewXRay;
import net.ccbluex.liquidbounce.features.module.modules.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
@SideOnly(Side.CLIENT)
public abstract class MixinBlock {
    @Shadow
    @Final
    protected Material blockMaterial;
    @Shadow
    protected double minX;
    @Shadow
    protected double minY;
    @Shadow
    protected double minZ;
    @Shadow
    protected double maxX;
    @Shadow
    protected double maxY;
    @Shadow
    protected double maxZ;

    @Shadow
    public abstract AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state);
    @Shadow
    @Final
    protected BlockState blockState;

    @Shadow
    public abstract void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ);

    // Has to be implemented since a non-virtual call on an abstract method is illegal
    @Shadow
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return null;
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        AxisAlignedBB axisalignedbb = this.getCollisionBoundingBox(worldIn, pos, state);
        BlockBBEvent blockBBEvent = new BlockBBEvent(pos, blockState.getBlock(), axisalignedbb);
        LiquidBounce.eventManager.callEvent(blockBBEvent);
        axisalignedbb = blockBBEvent.getBoundingBox();
        if(axisalignedbb != null && mask.intersectsWith(axisalignedbb))
            list.add(axisalignedbb);
    }





    @Inject(method = "shouldSideBeRendered", at = @At("HEAD"), cancellable = true)
    private void shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final XRay xray = (XRay) LiquidBounce.moduleManager.getModule(XRay.class);
        final NewXRay newXRay = (NewXRay) LiquidBounce.moduleManager.getModule(NewXRay.class);

        if(xray.getState())
            callbackInfoReturnable.setReturnValue(xray.getXrayBlocks().contains(this));
        LiquidBounce.eventManager.callEvent(new BlockRenderSideEvent(worldIn, pos, side, maxX, minX, maxY, minY, maxZ, minZ));
    }
    /**
     * @author Xray fix 082321
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        int i = worldIn.getCombinedLight(pos, LiquidBounce.moduleManager.getModule(NewXRay.class).getState() ? 100000 : block.getLightValue());

        if (i == 0 && block instanceof BlockSlab)
        {
            pos = pos.down();
            block = worldIn.getBlockState(pos).getBlock();
            return worldIn.getCombinedLight(pos, block.getLightValue());
        }
        else
        {
            return i;
        }
    }





    @Inject(method = "getAmbientOcclusionLightValue", at = @At("HEAD"), cancellable = true)
    private void getAmbientOcclusionLightValue(final CallbackInfoReturnable<Float> floatCallbackInfoReturnable) {
        if (LiquidBounce.moduleManager.getModule(XRay.class).getState())
            floatCallbackInfoReturnable.setReturnValue(1F);
    }

    /**
     * @author Xray fix 082321
     */
    @SideOnly(Side.CLIENT)
    @Overwrite
    public EnumWorldBlockLayer getBlockLayer() {
        if (LiquidBounce.moduleManager.getModule(NewXRay.class).getState()) {
            if ((Object) this == Block.getBlockById(16)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(14)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(15)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(56)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(129)) {
                return EnumWorldBlockLayer.SOLID;
            }
            if ((Object) this == Block.getBlockById(73)) {
                return EnumWorldBlockLayer.SOLID;
            }
            return EnumWorldBlockLayer.TRANSLUCENT;
        }
        return EnumWorldBlockLayer.SOLID;
    }
    @Shadow
    public boolean isFullCube()
    {
        return true;
    }
    /**
     * @author  Xray fix 082321
     */
    @Overwrite
    public boolean isVisuallyOpaque()
    {
        if (LiquidBounce.moduleManager.getModule(NewXRay.class).getState()) {
            if ((Object) this == Block.getBlockById(16)) {
                return true;
            }
        }
        return this.blockMaterial.blocksMovement() && isFullCube();
    }
}