package net.ccbluex.liquidbounce.features.module.modules.player;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "HackerDetect",description = "Check the cheater.", category = ModuleCategory.PLAYER,fakeName = "Cheater Check")
public class HackerDetect extends Module {
    private int bufferNoFall;
    private int bufferFlight;
    private int bufferSpeed;
    private final BoolValue checkNoFall = new BoolValue("CheckNoFall", true);
    private final BoolValue checkFly = new BoolValue("CheckFly", true);
    private final BoolValue checkSpeed = new BoolValue("CheckSpeed", true);
    public float getDistanceToGround(Entity e) {
        float a = (float) e.posY;
        while (a > 0.0f) {
            int[] stairs = new int[] { 53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180 };
            int[] exemptIds = new int[] { 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70,
                    72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157,
                    171, 175, 176, 177 };
            Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(e.posX, a - 1.0f, e.posZ))
                    .getBlock();
            if (!(block instanceof BlockAir)) {
                if (Block.getIdFromBlock(block) == 44 || Block.getIdFromBlock(block) == 126) {
                    return Math.max((float) (e.posY - (double) a - 0.5), 0.0f);
                }
                int length = stairs.length;
                int i = 0;
                while (i < length) {
                    int id = stairs[i];
                    if (Block.getIdFromBlock(block) == id) {
                        return Math.max((float) (e.posY - (double) a - 1.0), 0.0f);
                    }
                    ++i;
                }
                int length2 = exemptIds.length;
                int j = 0;
                while (j < length2) {
                    int id = exemptIds[j];
                    if (Block.getIdFromBlock(block) == id) {
                        return Math.max((float) (e.posY - (double) a), 0.0f);
                    }
                    ++j;
                }
                return (float) (e.posY - (double) a - 1.0);
            }
            a -= 1.0f;
        }
        return 0.0f;
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if(event.getEventState() == EventState.PRE) {
            mc.theWorld.loadedEntityList.forEach(o -> {
                if (o != mc.thePlayer && !o.isDead && o instanceof EntityPlayer) {
                    if (this.getDistanceToGround(o) > 4.0f && o.onGround && o.posY < o.prevPosY && !o.isSilent()) {
                        ++this.bufferNoFall;
                    }
                    if (this.bufferNoFall >= 10 && checkNoFall.get()) {
                        LiquidBounce.hud.addNotification(new Notification("HackerDetect", "Detected  "+o.getName()+"is using Nofall", NotifyType.WARNING, 5000,1000));
                        this.bufferNoFall = 0;
                    }
                    if (o.lastTickPosY < o.posY - 0.4 && !o.isSilent()) {
                        ++this.bufferFlight;
                    }
                    if (this.bufferFlight >= 50 && checkFly.get()) {
                        LiquidBounce.hud.addNotification(new Notification("HackerDetect", "Detected  "+o.getName()+"is using Fly", NotifyType.WARNING, 5000,1000));
                        this.bufferFlight = 0;
                    }
                    if (o.lastTickPosX < o.posX - 0.7) {
                        ++this.bufferSpeed;
                    }
                    if (o.posX > o.lastTickPosX + 0.7) {
                        ++this.bufferSpeed;
                    }
                    if (o.lastTickPosZ < o.posZ - 0.7) {
                        ++this.bufferSpeed;
                    }
                    if (o.posX > o.lastTickPosX + 0.7) {
                        ++this.bufferSpeed;
                    }
                    if (this.bufferSpeed > 14 && checkSpeed.get()) {
                        LiquidBounce.hud.addNotification(new Notification("HackerDetect", "Detected  "+o.getName()+"is using Speed", NotifyType.WARNING, 5000,1000));
                        this.bufferSpeed = 0;
                    }
                }
            });
        }
    }
}
