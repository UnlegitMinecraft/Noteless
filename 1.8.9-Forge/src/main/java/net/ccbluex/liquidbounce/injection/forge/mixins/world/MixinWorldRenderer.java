package net.ccbluex.liquidbounce.injection.forge.mixins.world;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.NewXRay;
import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow
    private boolean noColor;
    @Shadow
    public IntBuffer rawIntBuffer;
    @Shadow
    public int getColorIndex(int p_78909_1_) {
        return 0;
    }

    /**
     * @author
     */
    @Overwrite
    public void putColorMultiplier(float red, float green, float blue, int p_178978_4_) {
        NewXRay newXRay = (NewXRay)LiquidBounce.moduleManager.getModule(NewXRay.class);
        int i = this.getColorIndex(p_178978_4_);
        int j = -1;


        final int xraycolor = new Color(0, 0, 0,newXRay.getOpacity()).getRGB();
        if (!this.noColor) {
            j = this.rawIntBuffer.get(i);

            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int k = (int) ((float) (j & 255) * red);
                int l = (int) ((float) (j >> 8 & 255) * green);
                int i1 = (int) ((float) (j >> 16 & 255) * blue);
                if(newXRay.getState()) {
                    j &= xraycolor;
                    j = j | i1 << 16 | l << 8 | k;
                }
                else {
                    j = j & -16777216;
                    j = j | i1 << 16 | l << 8 | k;
                }
            } else {
                int j1 = (int) ((float) (j >> 24 & 255) * red);
                int k1 = (int) ((float) (j >> 16 & 255) * green);
                int l1 = (int) ((float) (j >> 8 & 255) * blue);
                j = j & 255;
                j = j | j1 << 24 | k1 << 16 | l1 << 8;
            }
        }

        this.rawIntBuffer.put(i, j);
    }
}
