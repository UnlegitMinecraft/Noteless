package net.ccbluex.liquidbounce.utils.handler;

import org.lwjgl.input.Mouse;

public class MouseClickReleaseHandler {
   public boolean clicked;
   private int button;

   public MouseClickReleaseHandler(int key) {
      this.button = key;
   }

   public boolean canExcecute() {
      if(!Mouse.isButtonDown(this.button)) {
         if(!this.clicked) {
            this.clicked = true;
            return true;
         }
      } else {
         this.clicked = false;
      }

      return false;
   }
}
