/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.minecraft.entity.item.*;
import net.minecraft.client.renderer.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.ccbluex.liquidbounce.features.module.modules.render.*;
import net.ccbluex.liquidbounce.*;
import org.lwjgl.opengl.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.*;

@Mixin(RenderEntityItem.class)
public abstract class MixinRenderEntityItem extends Render<EntityItem> {
    protected MixinRenderEntityItem(final RenderManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Shadow
    protected abstract int func_177078_a(final ItemStack p0);

    @Shadow
    protected abstract boolean shouldBob();

    @Inject(method = "doRender", at = @At("HEAD"))
    private void injectChamsPre(CallbackInfo callbackInfo) {
        final Chams chams = (Chams) LiquidBounce.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0F, -1000000F);
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void injectChamsPost(CallbackInfo callbackInfo) {
        final Chams chams = (Chams) LiquidBounce.moduleManager.getModule(Chams.class);

        if (chams.getState() && chams.getItemsValue().get()) {
            GL11.glPolygonOffset(1.0F, 1000000F);
            GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        }
    }

    @Overwrite
    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
    {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null)
        {
            return 0;
        }
        else
        {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.func_177078_a(itemstack);
            float f = 0.25F;
            float f1 = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
            if (LiquidBounce.moduleManager.getModule(ItemPhysics.class).getState()) {
                f1 = 0.0f;
            }
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);

            if (flag || this.renderManager.options != null)
            {
                float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                if (LiquidBounce.moduleManager.getModule(ItemPhysics.class).getState()) {
                    if (itemIn.onGround) {
                        GL11.glRotatef(itemIn.rotationYaw, 0.0f, 1.0f, 0.0f);
                        GL11.glRotatef(itemIn.rotationPitch + 90.0f, 1.0f, 0.0f, 0.0f);
                    } else {
                        for (int a = 0; a < 10; ++a) {
                            GL11.glRotatef(f3, 0.5f, 0.5f, 0.0f);
                        }
                    }
                } else {
                    GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
                }
            }

            if (!flag)
            {
                float f6 = -0.0F * (float)(i - 1) * 0.5F;
                float f4 = -0.0F * (float)(i - 1) * 0.5F;
                float f5 = -0.046875F * (float)(i - 1) * 0.5F;
                GlStateManager.translate(f6, f4, f5);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }
}
