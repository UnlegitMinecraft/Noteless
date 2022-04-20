
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.EaseUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(GuiButton.class)
public abstract class MixinGuiButton extends Gui {
   @Shadow
   public int xPosition;

   @Shadow
   public int yPosition;

   @Shadow
   public int width;

   @Shadow
   public int height;

   @Shadow
   protected boolean hovered;

   @Shadow
   public boolean enabled;

   @Shadow
   public boolean visible;

   @Shadow
   protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

   @Shadow
   public String displayString;

   private double animation = 0.0;
   private long lastUpdate=System.currentTimeMillis();

   /**
    * @author liuli
    */
   @Overwrite
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if(!visible)
         return;

      this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
      this.mouseDragged(mc, mouseX, mouseY);
      long time=System.currentTimeMillis();
      double pct=(time-lastUpdate)/500D;

      if (this.hovered) {
         if(animation<1){
            animation+=pct;
         }
         if(animation>1){
            animation=1;
         }
      } else {
         if(animation>0){
            animation-=pct;
         }
         if(animation<0){
            animation=0;
         }
      }

      double percent = EaseUtils.easeInOutQuad(animation);
      RenderUtils.drawRect(this.xPosition,this.yPosition,this.xPosition + width,this.yPosition + height, new Color(31,31,31,150).getRGB());
      double half=this.width / 2.0;
      double center=this.xPosition + half;
      if(enabled)
         RenderUtils.drawRect(center - percent*(half), this.yPosition + this.height - 1, center + percent*(half), this.yPosition + this.height, Color.WHITE.getRGB());
      Fonts.font35.drawCenteredString(this.displayString,this.xPosition + this.width / 2F,this.yPosition+this.height/2F-Fonts.font40.getHeight()/2F+1,visible?Color.WHITE.getRGB():Color.GRAY.getRGB(),false);
      lastUpdate=time;
   }
}
