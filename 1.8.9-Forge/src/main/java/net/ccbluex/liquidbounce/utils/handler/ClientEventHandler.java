package net.ccbluex.liquidbounce.utils.handler;

public class ClientEventHandler {
   private boolean state;

   public boolean canExcecute(boolean eventBool) {
      if(eventBool) {
         if(!this.state) {
            this.state = true;
            return true;
         }
      } else {
         this.state = false;
      }

      return false;
   }
}
