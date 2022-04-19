package net.ccbluex.liquidbounce.features.module.modules.render;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.BlockObject;
import net.ccbluex.liquidbounce.utils.render.ColorManager;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.TimeHelper;
import net.ccbluex.liquidbounce.value.FloatValue;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.renderer.GlStateManager;
@ModuleInfo(name = "PacketGraph", description = "Show the Packet", category = ModuleCategory.RENDER)
public class PacketGraph extends Module {
    public final FloatValue xValue = new FloatValue("X", 2.0f, 0.0f, 2000.0f);
    public final FloatValue yValue = new FloatValue("Y",10.0f, 0.0f, 2000.0f);

    private List<BlockObject> clientBlocks = new CopyOnWriteArrayList<>();
    private List<BlockObject> serverBlocks = new CopyOnWriteArrayList<>();
    private TimeHelper timerUtil = new TimeHelper(), secTimerUtil = new TimeHelper();

    private static int clientPackets;
    private static int serverPackets;
    private static int secClientPackets;
    private static int secServerPackets;
    private int renderSecClientPackets;
    private int renderSecServerPackets;


    public void clear() {
        clientPackets = 0;
        serverPackets = 0;
        secClientPackets = 0;
        secServerPackets = 0;
        renderSecClientPackets = 0;
        renderSecServerPackets = 0;
        clientBlocks.clear();
        serverBlocks.clear();
        timerUtil.reset();
        secTimerUtil.reset();
    }

    @EventTarget
    public void onPacket(PacketEvent e) {
        if (e.getPacket().getClass().getSimpleName().toLowerCase().startsWith("c")) {
            clientPackets++;
            secClientPackets++;
        } else if (e.getPacket().getClass().getSimpleName().toLowerCase().startsWith("s")) {
            serverPackets++;
            secServerPackets++;
        }
    }

    @EventTarget
    public void onRender(Render2DEvent event) {
        int x = (int) xValue.get().intValue();
        int y = (int) yValue.get().intValue();
        RenderUtils.drawBorderedRect(x - 3,y - 68,x + 153,y + 74,1.0f, new Color(0, 0, 255).getRGB(), new Color(0, 0, 0, 0).getRGB());

        if (timerUtil.hasReached(50)) {
            clientBlocks.forEach(blockObject -> blockObject.x--);
            clientBlocks.add(new BlockObject(x + 150, Math.min(clientPackets, 55)));
            clientPackets = 0;

            serverBlocks.forEach(blockObject -> blockObject.x--);
            serverBlocks.add(new BlockObject(x + 150, Math.min(serverPackets, 55)));
            serverPackets = 0;
            timerUtil.reset();
        }

        if (secTimerUtil.hasReached(1000)) {
            renderSecClientPackets = secClientPackets;
            renderSecServerPackets = secServerPackets;
            secClientPackets = 0;
            secServerPackets = 0;
            secTimerUtil.reset();
        }

        int graphY = y;
        for (int i = 0; i < 2; i++) {
            drawGraph(i, x, graphY);
            graphY += 68;
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        if (!clientBlocks.isEmpty()) {
            BlockObject firstBlock = clientBlocks.get(clientBlocks.size() - 1);
            mc.fontRendererObj.drawString("< avg: " + firstBlock.height, x * 2 + 301, y * 2 - firstBlock.height * 2 - 4,
                    -1);
        }
        if (!serverBlocks.isEmpty()) {
            BlockObject firstBlock = serverBlocks.get(serverBlocks.size() - 1);
            mc.fontRendererObj.drawString("< avg: " + firstBlock.height, x * 2 + 301, (y + 68) * 2 - firstBlock.height * 2 - 4, -1);
        }
        GL11.glPopMatrix();

        clientBlocks.removeIf(block -> block.x < x);
        serverBlocks.removeIf(block -> block.x < x);
    }

    private void drawGraph(int mode,int x,int y) {
        boolean isClient = mode == 0;
        RenderUtils.drawRect(x,y + 0.5,x + 150,y - 60,new Color(0,0,0,80).getRGB());
        GL11.glPushMatrix();
        GL11.glScalef(0.5f,0.5f,0.5f);
        String secString = isClient ? renderSecClientPackets + " packets/sec" : renderSecServerPackets + " packets/sec";
        mc.fontRendererObj.drawString(isClient ? "Outgoing packets": "Incoming packets",x * 2,y * 2 - 129,-1);
        mc.fontRendererObj.drawString(secString,(x * 2) + 300 - (mc.fontRendererObj.getStringWidth(secString)),y * 2 - 129,-1);
        GL11.glPopMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(1.5f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBegin(GL11.GL_LINES);
        int rainbowTicks = 0;
        List<BlockObject> list = isClient ? clientBlocks : serverBlocks;
        for (BlockObject block : list) {
            RenderUtils.glColor(new Color (ColorManager.astolfoRainbow(1,100,rainbowTicks)));
            GL11.glVertex2d(block.x, y - block.height);
            try {
                BlockObject lastBlock = list.get(list.indexOf(block) + 1);
                GL11.glVertex2d(block.x + 1, y - lastBlock.height);
            } catch (Exception ignored) {}
            rainbowTicks += 300;
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.resetColor();
    }
    }
