/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.item;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.modules.render.BlockAnimations;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.SwingAnimation;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
@SideOnly(Side.CLIENT)
public abstract class MixinItemRenderer {
    private float lastSwingProgress=0;
    private long hSwing=-1;

    @Shadow
    private float prevEquippedProgress;

    @Shadow
    private float equippedProgress;

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    protected abstract void rotateArroundXAndY(float angle, float angleY);

    @Shadow
    protected abstract void setLightMapFromPlayer(AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void rotateWithPlayerRotations(EntityPlayerSP entityPlayerSP, float partialTicks);

    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress);

    @Shadow
    protected abstract void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks);

    @Shadow
    protected abstract void doBlockTransformations();

    @Shadow
    protected abstract void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer);

    @Shadow
    protected abstract void doItemUsedTransformations(float swingProgress);

    @Shadow
    public abstract void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform);

    @Shadow
    protected abstract void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress);

    private BlockAnimations animations;

    /**
     * @author Liuli
     */
    @Overwrite
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    /**
     * @author Liuli
     */
    @Overwrite
    public void renderItemInFirstPerson(float partialTicks) {
        if(animations==null){
            animations = (BlockAnimations) LiquidBounce.moduleManager.getModule(BlockAnimations.class);
        }

        float f = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        AbstractClientPlayer abstractclientplayer = mc.thePlayer;
        float f1 = abstractclientplayer.getSwingProgress(partialTicks);
        float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        this.rotateArroundXAndY(f2, f3);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP) abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();

        if(this.itemToRender != null) {
            final KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);

            if(this.itemToRender.getItem() instanceof net.minecraft.item.ItemMap) {
                this.renderItemMap(abstractclientplayer, f2, f, f1);
            } else if (abstractclientplayer.getItemInUseCount() > 0 || (itemToRender.getItem() instanceof ItemSword && killAura.getBlockingStatus())) {
                EnumAction enumaction = killAura.getBlockingStatus() ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();
                switch(enumaction) {
                    case NONE:
                        this.transformFirstPersonItem(f, 0.0F);
                        break;
                    case EAT:
                    case DRINK:
                        this.performDrinking(abstractclientplayer, partialTicks);
                        this.transformFirstPersonItem(f, f1);
                        break;
                    case BLOCK:
                        if(animations.getState()){
                            GL11.glTranslated(animations.getTranslateX().get(), animations.getTranslateY().get(), animations.getTranslateZ().get());
                            switch (animations.getPresetValue().get()) {
                                case "Akrien": {
                                    transformFirstPersonItem(f1, 0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Stella":
                                    this.transformFirstPersonItem(-0.1F, f1);
                                    GlStateManager.translate(-0.5F, 0.4F, -0.2F);
                                    GlStateManager.rotate(32, 0, 1, 0);
                                    GlStateManager.rotate(-70, 1, 0, 0);
                                    GlStateManager.rotate(40, 0, 1, 0);
                                    break;
                                case "Fathum":
                                    GlStateManager.popMatrix();
                                    GL11.glRotated(25, 0,0.2,0);
                                    this.transformFirstPersonItem(0.0f, f1);
                                    GlStateManager.scale(0.9F, 0.9F, 0.9F);
                                    this.doBlockTransformations();
                                    GlStateManager.pushMatrix();
                                    break;
                                case "1.7":
                                    this.transformFirstPersonItem(f, f1);
                                    GlStateManager.translate(0, 0.3, 0);
                                    this.doBlockTransformations();
                                    break;
                                case "Smooth":
                                    this.transformFirstPersonItem(f1 / 5, f1);
                                    GlStateManager.translate(0, 0.2, 0);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F), 4, -0.8F, -1F);
                                    this.doBlockTransformations();
                                    break;
                                case "Exhi":
                                    this.transformFirstPersonItem(f / 2, 0);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F / 2.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, 1.0F, MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                                    this.doBlockTransformations();
                                    GL11.glTranslatef(-0.05F, this.mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
                                    break;
                                case "Exhi 2":
                                    this.transformFirstPersonItem(f / 2, f1);
                                    GlStateManager.rotate(MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F, -MathHelper.sin(f1 * f1 * 3.1415927F), -0.0F, 9.0F);
                                    GlStateManager.rotate(MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F, 1.0F, -MathHelper.sin(f1 * f1 * 3.1415927F), -0.0F);
                                    this.doBlockTransformations();
                                    GL11.glTranslatef(-0.05F, this.mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
                                    break;
                                case "Shred":
                                    this.transformFirstPersonItem(f / 2, f1);
                                    GlStateManager.rotate(MathHelper.sin(f1 * f1 * 3.1415927F) * 30.0F / 2.0F, -MathHelper.sin(f1 * f1 * 3.1415927F), -0.0F, 9.0F);
                                    GlStateManager.rotate(MathHelper.sin(f1 * f1 * 3.1415927F) * 40.0F, 1.0F, -MathHelper.sin(f1 * f1 * 3.1415927F) / 2.0F, -0.0F);
                                    this.doBlockTransformations();
                                    GL11.glTranslatef(-0.05F, this.mc.thePlayer.isSneaking() ? -0.2F : 0.0F, 0.1F);
                                    break;
                                case "Avatar": {
                                    avatar(f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Light":
                                    float var5 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    transformFirstPersonItem(f, 0.0F);
                                    doBlockTransformations();
                                    GlStateManager.scale(0.54F, 0.54F, 0.54F);
                                    GlStateManager.translate(-0.4F, 1F, 0.4F);
                                    GlStateManager.rotate(-var5 * 90.0F, -15.0F, -15.0F, 19.0F);
                                    break;
                                case "ETB": {
                                    etb(f, f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Exhibition": {
                                    transformFirstPersonItem(f, 0.83F);
                                    float f4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.83F);
                                    GlStateManager.translate(-0.5F, 0.2F, 0.2F);
                                    GlStateManager.rotate(-f4 * 0.0F, 0.0F, 0.0F, 0.0F);
                                    GlStateManager.rotate(-f4 * 43.0F, 58.0F, 23.0F, 45.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Remix":
                                    this.func_178096_b(f, 0.83f);
                                    this.func_178103_d();
                                    float f5 = MathHelper.sin((MathHelper.sqrt_float(f1) * 3.83f));
                                    GlStateManager.translate(-0.5f,0.2f, 0.2f);
                                    GlStateManager.rotate((-f5 * 0.0f), 0.0f, 0.0f, 0.0f);
                                    GlStateManager.rotate((-f5 * 43.0f), 58.0f, 23.0f, 45.0f);
                                    break;
                                case "Push": {
                                    push(f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Reverse": {
                                    transformFirstPersonItem(f1, f1);
                                    doBlockTransformations();
                                    GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
                                    break;
                                }
                                case "Shield": {
                                    jello(f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "SigmaNew": {
                                    sigmaNew(0.2F, f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "SigmaOld": {
                                    sigmaOld(f);
                                    float var15 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F);
                                    GlStateManager.rotate(-var15 * 55.0F / 2.0F, -8.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-var15 * 45.0F, 1.0F, var15 / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    GL11.glTranslated(1.2D, 0.3D, 0.5D);
                                    GL11.glTranslatef(-1.0F, mc.thePlayer.isSneaking() ? -0.1F : -0.2F, 0.2F);
                                    GlStateManager.scale(1.2F, 1.2F, 1.2F);
                                    break;
                                }
                                case "Slide": {
                                    slide(f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "SlideDown": {
                                    transformFirstPersonItem(0.2F, f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swong": {
                                    transformFirstPersonItem(f / 2.0F, 0.0F);
                                    GlStateManager.rotate(-MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927F) * 40.0F / 2.0F, MathHelper.sqrt_float(f1) / 2.0F, -0.0F, 9.0F);
                                    GlStateManager.rotate(-MathHelper.sqrt_float(f1) * 30.0F, 1.0F, MathHelper.sqrt_float(f1) / 2.0F, -0.0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "VisionFX": {
                                    continuity(f1);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Swank":{
                                    GL11.glTranslated(-0.1, 0.15, 0.0);
                                    this.transformFirstPersonItem(f / 0.15f, f1);
                                    final float rot = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                                    GlStateManager.rotate(rot * 30.0f, 2.0f, -rot, 9.0f);
                                    GlStateManager.rotate(rot * 35.0f, 1.0f, -rot, -0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "Jello":{
                                    this.transformFirstPersonItem(0.0f, 0.0f);
                                    this.doBlockTransformations();
                                    final int alpha = (int)Math.min(255L, ((System.currentTimeMillis() % 255L > 127L) ? Math.abs(Math.abs(System.currentTimeMillis()) % 255L - 255L) : (System.currentTimeMillis() % 255L)) * 2L);
                                    GlStateManager.translate(0.3f, -0.0f, 0.4f);
                                    GlStateManager.rotate(0.0f, 0.0f, 0.0f, 1.0f);
                                    GlStateManager.translate(0.0f, 0.5f, 0.0f);
                                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.translate(0.6f, 0.5f, 0.0f);
                                    GlStateManager.rotate(-90.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.rotate(-10.0f, 1.0f, 0.0f, -1.0f);
                                    GlStateManager.rotate(abstractclientplayer.isSwingInProgress ? (-alpha / 5.0f) : 1.0f, 1.0f, -0.0f, 1.0f);
                                    break;
                                }
                                case "HSlide":{
                                    transformFirstPersonItem(f1!=0?Math.max(1-(f1*2),0)*0.7F:0, 1F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "None":{
                                    transformFirstPersonItem(0F,0F);
                                    doBlockTransformations();
                                    break;
                                }
                                case "Rotate":{
                                    rotateSword(f1);
                                    break;
                                }
                            }
                        }else{
                            this.transformFirstPersonItem(f + 0.1F, f1);
                            this.doBlockTransformations();
                            GlStateManager.translate(-0.5F, 0.2F, 0.0F);
                        }
                        break;
                    case BOW:
                        this.transformFirstPersonItem(f, f1);
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                }
            }else{
                if (!(animations.getState()&&animations.getSwingAnim().get()))
                    this.doItemUsedTransformations(f1);
                this.transformFirstPersonItem(f, f1);
            }

            this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        }else if(!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f, f1);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    private void doItemRenderGLTranslate(){
        if(animations.getState()) {
            GlStateManager.translate(animations.getItemPosX().get(), animations.getItemPosY().get(), animations.getItemPosZ().get());
        }else{
            GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        }
    }

    private void doItemRenderGLScale(){
        if(animations.getState()) {
            GlStateManager.scale(animations.getItemScale().get(), animations.getItemScale().get(), animations.getItemScale().get());
        }else{
            GlStateManager.scale(0.4F, 0.4F, 0.4F);
        }
    }
    private void sigmaOld(float f) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, f * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(0F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(0F, 1.3F, 0.1F, 0.2F);
        doItemRenderGLScale();
    }

    //methods in LiquidBounce b73 Animation-No-Cross
    private void avatar(float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f2 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f2 * -40.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void slide(float var9) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var11 = MathHelper.sin(var9 * var9 * 3.1415927F);
        float var12 = MathHelper.sin(MathHelper.sqrt_float(var9) * 3.1415927F);
        GlStateManager.rotate(var11 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var12 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var12 * -40.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }

    private void rotateSword(float f1){
        genCustom(0.0F, 0.0F);
        doBlockTransformations();
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(MathHelper.sqrt_float(f1) * 10.0F * 40.0F, 1.0F, -0.0F, 2.0F);
    }

    private void genCustom(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178096_1_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(p_178096_2_ * p_178096_2_ * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }


    private void jello(float var12) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(48.57F, 0.0F, 0.24F, 0.14F);
        float var13 = MathHelper.sin(var12 * var12 * 3.1415927F);
        float var14 = MathHelper.sin(MathHelper.sqrt_float(var12) * 3.1415927F);
        GlStateManager.rotate(var13 * -35.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var14 * 20.0F, 1.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    private void continuity(float var10) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var12 = -MathHelper.sin(var10 * var10 * 3.1415927F);
        float var13 = MathHelper.cos(MathHelper.sqrt_float(var10) * 3.1415927F);
        float var14 = MathHelper.abs(MathHelper.sqrt_float((float) 0.1) * 3.1415927F);
        GlStateManager.rotate(var12 * var14 * 30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var13 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var13 * 20.0F, 1.0F, 0.0F, 0.0F);
        doItemRenderGLScale();
    }
    private void func_178096_b(float p_178096_1_, float p_178096_2_) {
        GlStateManager.translate((float) 0.56f, (float) -0.52f, (float) -0.71999997f);
        GlStateManager.translate((float) 0.0f, (float) (p_178096_1_ * -0.6f), (float) 0.0f);
        GlStateManager.rotate((float) 45.0f, (float) 0.0f, (float) 1.0f, (float) 0.0f);
        float var3 = MathHelper.sin((float) (p_178096_2_ * p_178096_2_ * 3.1415927f));
        float var4 = MathHelper.sin((float) (MathHelper.sqrt_float((float) p_178096_2_) * 3.1415927f));
        GlStateManager.rotate((float) (var3 * -20.0f), (float) 0.0f, (float) 1.0f, (float) 0.0f);
        GlStateManager.rotate((float) (var4 * -20.0f), (float) 0.0f, (float) 0.0f, (float) 1.0f);
        GlStateManager.rotate((float) (var4 * -80.0f), (float) 1.0f, (float) 0.0f, (float) 0.0f);
        GlStateManager.scale((float) 0.4f, (float) 0.4f, (float) 0.4f);
    }
    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }
    public void sigmaNew(float var22, float var23) {
        doItemRenderGLTranslate();
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var24 = MathHelper.sin(var23 * MathHelper.sqrt_float(var22) * 3.1415927F);
        float var25 = MathHelper.abs(MathHelper.sqrt_double(var22) * 3.1415927F);
        GlStateManager.rotate(var24 * 20.0F * var25, 0.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    private void etb(float equipProgress, float swingProgress) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927F);
        GlStateManager.rotate(var3 * -34.0F, 0.0F, 1.0F, 0.2F);
        GlStateManager.rotate(var4 * -20.7F, 0.2F, 0.1F, 1.0F);
        GlStateManager.rotate(var4 * -68.6F, 1.3F, 0.1F, 0.2F);
        doItemRenderGLScale();
    }

    private void push(float idc) {
        doItemRenderGLTranslate();
        GlStateManager.translate(0.0F, (float) 0.1 * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(idc * idc * 3.1415927F);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(idc) * 3.1415927F);
        GlStateManager.rotate(var3 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(var4 * -10.0F, 1.0F, 1.0F, 1.0F);
        doItemRenderGLScale();
    }

    /**
     * @author Liuli
     */
    @Overwrite
    private void renderFireInFirstPerson(float partialTicks) {
        final AntiBlind antiBlind = (AntiBlind) LiquidBounce.moduleManager.getModule(AntiBlind.class);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 1.0F;
        if(antiBlind.getState() && antiBlind.getFireEffect().get()){
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
            f = 0.7F;
        }else{
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        }
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        for(int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0F - f) / 2.0F;
            float f6 = f5 + f;
            float f7 = 0.0F - f / 2.0F;
            float f8 = f7 + f;
            float f9 = -0.5F;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(f5, f7, f9).tex(f2, f4).endVertex();
            worldrenderer.pos(f6, f7, f9).tex(f1, f4).endVertex();
            worldrenderer.pos(f6, f8, f9).tex(f1, f3).endVertex();
            worldrenderer.pos(f5, f8, f9).tex(f2, f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }
}