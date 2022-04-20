package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInfo(
        name = "AntiFall",
        description = ":/",
        category = ModuleCategory.MOVEMENT,
        fakeName = "Anti Fall"
)
public class AntiFall extends Module {
    private ListValue modeValue = new ListValue("Mode", new String[]{"OldHypixel","Hypixel"},"Hypixel");
    private final IntegerValue MinFallenBlocks = new IntegerValue("MinFallenBlocks", 10, 5, 30);
    private final BoolValue VoidOnly =new BoolValue("VoidOnly", true);
    private double mario = 0;
    private boolean AAAA = false;


    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 0, 0).expand(0, 0, 0)).isEmpty() && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -10002.25, 0).expand(0, -10003.75, 0)).isEmpty()){
            //chat.print("Void!")
        }
    }
    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof C03PacketPlayer && mc.thePlayer.fallDistance >= MinFallenBlocks.get()){
            final C03PacketPlayer playerPacket = (C03PacketPlayer) packet;
            switch (modeValue.get()) {
                case "Hypixel":
                    if (VoidOnly.get() && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 0, 0).expand(0, 0, 0)).isEmpty() && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -10002.25, 0).expand(0, -10003.75, 0)).isEmpty()){
                        playerPacket.y += 11;
                    }else{
                        if (!VoidOnly.get()){
                            playerPacket.y += 11;
                        }
                    }
                    break;
            }
        }
//luigi = (mario * 0.5)
//chat.print("Times it spoofed ground = " + luigi)
    }
    @EventTarget
    public void onMove(final MoveEvent event) {
        switch (modeValue.get()) {
            case "OldHypixel":
                if (VoidOnly.get() && mc.thePlayer.fallDistance >= MinFallenBlocks.get() && mc.thePlayer.motionY <= 0 && (AAAA == false || mc.thePlayer.posY <= mario) && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 0, 0).expand(0, 0, 0)).isEmpty() && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -10002.25, 0).expand(0, -10003.75, 0)).isEmpty()){
                    mc.thePlayer.motionY = 1.85;
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                    event.setX(0);
                    event.setZ(0);
                    mario = mc.thePlayer.posY;
                    AAAA = true;

                }else{
                    if (!VoidOnly.get() && mc.thePlayer.fallDistance >= MinFallenBlocks.get() && mc.thePlayer.motionY <= 0 && (AAAA == false || mc.thePlayer.posY <= mario)){
                        mc.thePlayer.motionY = 1.85;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        event.setX(0);
                        event.setZ(0);
                        mario = mc.thePlayer.posY;
                        AAAA = true;
                    }
                }
                if (mc.thePlayer.onGround){
                    mario = 0;
                    AAAA = false;
                }
                break;
        }
    }

    @Override
    public void onDisable() {
        mario = 0;
        AAAA = false;
    }

    public String getTag() {
        return modeValue.get();
    }
}

