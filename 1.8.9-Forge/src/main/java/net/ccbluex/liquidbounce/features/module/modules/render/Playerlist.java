package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;


import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleInfo(name = "PlayerList", description = "Show the Player", category = ModuleCategory.RENDER)
public class Playerlist extends Module {
    private static class PlayerListObject {
        public String name;

        public int kills;

        public PlayerListObject(String name, int kills) {
            this.name = name;
            this.kills = kills;
        }
    }

    public final FloatValue xPos = new FloatValue("X", 2.0f, 0.0f, 2000.0f);
    public final FloatValue yPos = new FloatValue("Y",10.0f, 0.0f, 2000.0f);
    public PlayerListObject king;
    public List<PlayerListObject> players = new CopyOnWriteArrayList<>();
    private final String[] messages = new String[]{"was shot by","took the L to","was filled full of lead by","was crushed into moon dust by","was killed by ","was thrown into the void by ","be sent to Davy Jones' locker by","was turned to dust by","was thrown off a cliff by ","was deleted by ","was purified by ","was turned into space dust by","was given the cold shoulder by","was socked by","was oinked by"};


    @Override
    public void onDisable() {
        players.clear();
        king = null;
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof S02PacketChat) {
            boolean flag = false;
            String message = ((S02PacketChat) event.getPacket()).getChatComponent().getUnformattedText();
            label:
            {
                if (mc.thePlayer == null) {
                    return;
                }
                String playerName = null;
                try {
                    for (String s : messages) {
                        if (message.contains(s)) {
                            playerName = message.split(s)[1];
                            flag = true;
                        }
                    }
                    if(!flag) {
                        if(message.contains("死亡! 被 ") && message.contains(" 击杀")) {
                            String test = message.split("] ")[1];
                            playerName = test.split(" 击杀")[0];

                        }
                        if(message.contains("杀死了")) {
                            playerName = message.split("\\[")[0];
                        }
                        if(message.contains("击杀者")) {
                            String test = message.split("击杀者： ")[1];
                            playerName = test.split("\\[")[0];
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (playerName == null || playerName.isEmpty()) {
                    return;
                }

                if (!players.isEmpty()) {
                    for (PlayerListObject player : players) {
                        if (!player.name.equalsIgnoreCase(playerName)) continue;
                        ++player.kills;
                        break label;
                    }
                }
                players.add(new PlayerListObject(playerName, 1));
            }
        }
    }

    @EventTarget
    public void onWorld(WorldEvent event) {
        if(mc.thePlayer != null && !mc.thePlayer.isDead)
            players.clear();
    }

    @EventTarget
    public void onUpdate(UpdateEvent e) {
        if(mc.thePlayer.ticksExisted < 1) {
            players.clear();
            king = null;
        }
    }
    @EventTarget
    public void onRender2D(Render2DEvent e) {
        if (mc.thePlayer.isDead) {
            players.clear();
            king = null;
        }
        float textY = yPos.get();
        float x = xPos.get();
        float y = yPos.get();
        RenderUtils.drawRect(x, y, x + mc.fontRendererObj.getStringWidth("PlayerList") + 120, y + mc.fontRendererObj.FONT_HEIGHT + 3, new Color(21, 19, 23,255).getRGB());
        mc.fontRendererObj.drawString("Player List", (int)x + 3, (int)y +2, new Color(255, 255, 255).getRGB());
        players.sort((o1, o2) -> o2.kills - o1.kills);
        for (PlayerListObject player : players) {
            if (player == players.get(0)) {
                king = player;
            }
            RenderUtils.drawRect(x, textY + (float)mc.fontRendererObj.FONT_HEIGHT + 3.0f, x + mc.fontRendererObj.getStringWidth("PlayerList") + 120, textY + (float)mc.fontRendererObj.FONT_HEIGHT + 13.0f, new Color(30, 30, 35, 170).getRGB());
            if (player == king) {
                RenderUtils.drawImage(new ResourceLocation("liquidbounce/PlayerList.png"),(int) (x+1), (int)y+11, 10,10);
                mc.fontRendererObj.drawString(EnumChatFormatting.YELLOW + player.name, (int) (x + (player == king ? 14 : 3)), (int) (mc.fontRendererObj.FONT_HEIGHT + 2f + textY) + 1, -1);
            } else {
                mc.fontRendererObj.drawString(player.name, (int) (x + 3), (int) (mc.fontRendererObj.FONT_HEIGHT + 2f + textY) + 2, -1);
            }
            String killString;
            switch (player.kills) {
                case 0:
                case 1:
                case -1:
                    killString = "kill";
                    break;
                default:
                    killString = "kills";
                    break;
            }
            mc.fontRendererObj.drawString(player.kills + " " + killString, (int)(x + mc.fontRendererObj.getStringWidth("PlayerList") + 120 - mc.fontRendererObj.getStringWidth(player.kills + killString) - 2 - (killString.equalsIgnoreCase("kill") ? 3 : 1)) - 2, (int) (mc.fontRendererObj.FONT_HEIGHT + 3 + textY), -1);
            textY += 10.0f;
        }
    }
}
