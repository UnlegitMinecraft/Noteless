package net.ccbluex.liquidbounce.features.module.modules.render;

import com.google.common.collect.Lists;
import net.ccbluex.liquidbounce.RenderUtil;
import net.ccbluex.liquidbounce.event.BlockRenderSideEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@ModuleInfo(name = "NewXray", description = "Xray", category = ModuleCategory.RENDER)
public class NewXRay extends Module {
    private static final HashSet blockIDs = new HashSet();
    private int opacity = 160;
    float ULX2 = 2;
    static GameFontRenderer font = Fonts.font35;
    public List KEY_IDS = Lists.newArrayList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11),
            Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16),
            Integer.valueOf(21), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(46), Integer.valueOf(48),
            Integer.valueOf(52), 56, Integer.valueOf(57), Integer.valueOf(61), Integer.valueOf(62),
            Integer.valueOf(73), Integer.valueOf(74), Integer.valueOf(84), Integer.valueOf(89), Integer.valueOf(103),
            Integer.valueOf(116), Integer.valueOf(117), Integer.valueOf(118), Integer.valueOf(120),
            Integer.valueOf(129), Integer.valueOf(133), Integer.valueOf(137), Integer.valueOf(145),
            Integer.valueOf(152), Integer.valueOf(153), Integer.valueOf(154) });

    public static IntegerValue oPacity = new IntegerValue("Opacity", 50, 0, 255);
    public static IntegerValue Dis = new IntegerValue("Dis", 50, 0, 255);
    public static BoolValue CAVE = new BoolValue("Cave", true);
    public static BoolValue Tracers = new BoolValue("Tracers", true);
    public static BoolValue ESP = new BoolValue("ESP", true);
    private FloatValue thicknessValue = (FloatValue) new  FloatValue("Thickness", 0.5f, 0.11F, 1F);
    public static BoolValue CoalOre = (BoolValue) new BoolValue("Coal", false);
    public static BoolValue RedStoneOre = (BoolValue) new BoolValue("RedStone", false);
    public static BoolValue IronOre = (BoolValue) new BoolValue("Iron", false);
    public static BoolValue GoldOre = (BoolValue) new BoolValue("Gold", true);
    public static BoolValue DiamondOre = (BoolValue) new BoolValue("Diamond", true);
    public static BoolValue EmeraldOre = (BoolValue) new BoolValue("Emerald", false);
    public static BoolValue LapisOre = (BoolValue) new BoolValue("Lapis", false);
    public static CopyOnWriteArrayList list = new CopyOnWriteArrayList<>();
    public static ArrayList blocks = new ArrayList();
    public static final boolean[] ids = new boolean[4096];

    public static boolean contains(int id) {
        return ids[id];
    }

    public static void add(int id) {
        ids[id] = true;
    }

    public static void remove(int id) {
        ids[id] = false;
    }

    public static void clear() {
        for(int i = 0; i < ids.length; ++i) {
            ids[i] = false;
        }

    }

    public static boolean shouldRender(int id) {
        return ids[id];
    }
    public void onEnable() {
        blockIDs.clear();
        list.clear();
        this.opacity = oPacity.get().intValue();

        try {
            Iterator var1 = this.KEY_IDS.iterator();

            while (var1.hasNext()) {
                Integer o = (Integer) var1.next();
                blockIDs.add(o);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        mc.renderGlobal.loadRenderers();
    }
    @EventTarget
    private void on3DRender(Render3DEvent e) {
        Iterator x = list.iterator();
        if(Tracers.getValue()) {
            while (x.hasNext()) {

                XRayBlock x1 = (XRayBlock) x.next();
                double[] arrd = new double[3];
                double posX = x1.x - mc.getRenderManager().renderPosX;
                double posY = x1.y - mc.getRenderManager().renderPosY;
                double posZ = x1.z - mc.getRenderManager().renderPosZ;
                boolean old = this.mc.gameSettings.viewBobbing;
                RenderUtils.startDrawing();
                this.mc.gameSettings.viewBobbing = false;
                this.mc.entityRenderer.setupCameraTransform(this.mc.timer.renderPartialTicks, 2);
                this.mc.gameSettings.viewBobbing = old;

                if ((x1.type.contains("Diamond")||x1.type.contains("钻石")) && DiamondOre.getValue()) {
                    arrd[0] = 0.0;
                    arrd[1] = 128.0;
                    arrd[2] = 255.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }


                else if ((x1.type.contains("Gold")||x1.type.contains("金"))&&GoldOre.getValue()) {
                    arrd[0] = 255.0;
                    arrd[1] = 255.0;
                    arrd[2] = 0.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }


                else if ((x1.type.contains("Iron")||x1.type.contains("铁"))&&IronOre.getValue()) {
                    arrd[0] = 210.0;
                    arrd[1] = 170.0;
                    arrd[2] = 140.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }


                else if ((x1.type.contains("Redstone")||x1.type.contains("红石"))&&RedStoneOre.getValue()) {
                    arrd[0] = 255.0;
                    arrd[1] = 0.0;
                    arrd[2] = 0.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }

                else if ((x1.type.contains("Coal")||x1.type.contains("煤炭"))&&CoalOre.getValue()) {
                    arrd[0] = 0.0;
                    arrd[1] = 0.0;
                    arrd[2] = 0.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }


                else if ((x1.type.contains("Lapis")||x1.type.contains("青金石"))&&LapisOre.getValue()) {
                    arrd[0] = 27.0;
                    arrd[1] = 74.0;
                    arrd[2] = 161.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }


                else if ((x1.type.contains("Emerald")||x1.type.contains("绿宝石"))&&EmeraldOre.getValue()) {
                    arrd[0] = 23.0;
                    arrd[1] = 221.0;
                    arrd[2] = 98.0;
                    this.drawLine(arrd, posX, posY, posZ);
                }


                RenderUtils.stopDrawing();
            }
        }
    }
    @EventTarget
    public void onEvent(BlockRenderSideEvent e) {
        e.getState().getBlock();
        if (!CAVE.getValue() && containsID(Block.getIdFromBlock(e.getState().getBlock()))
                && !(e.getSide() == EnumFacing.DOWN && e.getMinY() > 0.0D ? true
                : (e.getSide() == EnumFacing.UP && e.getMaxY() < 1.0D ? true
                : (e.getSide() == EnumFacing.NORTH && e.getMinZ() > 0.0D ? true
                : (e.getSide() == EnumFacing.SOUTH && e.getMaxZ() < 1.0D ? true
                : (e.getSide() == EnumFacing.WEST && e.getMinX() > 0.0D ? true
                : (e.getSide() == EnumFacing.EAST && e.getMaxX() < 1.0D ? true
                : !e.getWorld().getBlockState(e.getPos()).getBlock()
                .isOpaqueCube()))))))) {
            e.setToRender(true);
        } else {
            if (!CAVE.getValue()) {
                e.cancelEvent();
            }
        }
        if (!ESP.getValue())
            return;

        if ((e.getSide() == EnumFacing.DOWN && e.getMinY() > 0.0D ? true
                : (e.getSide() == EnumFacing.UP && e.getMaxY() < 1.0D ? true
                : (e.getSide() == EnumFacing.NORTH && e.getMinZ() > 0.0D ? true
                : (e.getSide() == EnumFacing.SOUTH && e.getMaxZ() < 1.0D ? true
                : (e.getSide() == EnumFacing.WEST && e.getMinX() > 0.0D ? true
                : (e.getSide() == EnumFacing.EAST && e.getMaxX() < 1.0D ? true
                : !e.getWorld().getBlockState(e.getPos()).getBlock()
                .isOpaqueCube()))))))) {
            if (mc.theWorld.getBlockState(e.getPos().offset(e.getSide(), -1)).getBlock() instanceof BlockOre
                    || mc.theWorld.getBlockState(e.getPos().offset(e.getSide(), -1))
                    .getBlock() instanceof BlockRedstoneOre) {
                final float xDiff = (float) (mc.thePlayer.posX - e.getPos().getX());
                final float yDiff = 0;
                final float zDiff = (float) (mc.thePlayer.posZ - e.getPos().getZ());
                float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
                if (dis > Dis.getValue())
                    return;
                XRayBlock x = new XRayBlock(Math.round(e.getPos().offset(e.getSide(), -1).getZ()),
                        Math.round(e.getPos().offset(e.getSide(), -1).getY()),
                        Math.round(e.getPos().offset(e.getSide(), -1).getX()),
                        mc.theWorld.getBlockState(e.getPos().offset(e.getSide(), -1)).getBlock().getUnlocalizedName());
                if (!list.contains(x)) {
                    list.add(x);
                    for (EnumFacing e1 : EnumFacing.values()) {
                        XRayBlock x1 = new XRayBlock(Math.round(e.getPos().offset(e.getSide(), -1).offset(e1, 1).getZ()),
                                Math.round(e.getPos().offset(e.getSide(), -1).offset(e1, 1).getY()),
                                Math.round(e.getPos().offset(e.getSide(), -1).offset(e1, 1).getX()),
                                mc.theWorld.getBlockState(e.getPos().offset(e.getSide(), -1).offset(e1, 1)).getBlock()
                                        .getUnlocalizedName());
                        boolean b = false;
                        if (x.type.contains("Diamond") && x1.type.contains("Diamond")) {
                            b = true;
                        } else if (x.type.contains("Iron") && x1.type.contains("Iron")) {
                            b = true;
                        } else if (x.type.contains("Gold") && x1.type.contains("Gold")) {
                            b = true;
                        } else if (x.type.contains("Red") && x1.type.contains("Red")) {
                            b = true;
                        } else if (x.type.contains("Coal") && x1.type.contains("Coal")) {
                            b = true;
                        }
                        if (b) {
                            if (!list.contains(x1))
                                list.add(x1);
                        }
                    }

                }
            }
        }

    }
    @EventTarget
    private void DrawTags(Render3DEvent e) {
        Iterator x = list.iterator();
    }
    private void drawLine(double[] color, double x, double y, double z) {
        GL11.glEnable((int)2848);
        GL11.glColor3d(color[0], color[1], color[2]);
        //这个是线条ing
        GL11.glLineWidth(thicknessValue.get());
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)0.0, (double)this.mc.thePlayer.getEyeHeight(), (double)0.0);
        GL11.glVertex3d((double)x+0.5, (double)y+0.5, (double)z+0.5);
        GL11.glEnd();
        GL11.glDisable((int)2848);
    }
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
        list.clear();
    }

    public static boolean containsID(int id) {
        return blockIDs.contains(id);
    }

    @EventTarget
    private void renderHud(Render2DEvent event) {
        float last = 2;
        Iterator x = list.iterator();
    }

    private static float getSize(double x,double y,double z) {
        return Math.max((float) (mc.thePlayer.getDistance(x, y, z)) / 4.0f, 2.0f);
    }

    private static void startDrawing(double x, double y, double z, double StringX,double StringY,double StringZ) {
        float var10001 = mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        double size = (double) (getSize(StringX,StringY,StringZ) / 10.0f) * 4.8;
        GL11.glPushMatrix();
        RenderUtils.startDrawing();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glNormal3f((float) 0.0f, (float) 1.0f, (float) 0.0f);
        GL11.glRotatef((float) (-mc.getRenderManager().playerViewY), (float) 0.0f, (float) 1.0f, (float) 0.0f);
        GL11.glRotatef((float) mc.getRenderManager().playerViewX, (float) var10001, (float) 0.0f, (float) 0.0f);
        GL11.glScaled((double) (-0.01666666753590107 * size), (double) (-0.01666666753590107 * size),
                (double) (0.01666666753590107 * size));
    }

    private static void stopDrawing() {
        RenderUtils.stopDrawing();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    public static void rendertag(String Str, double x, double y, double z,double StringX,double StringY,double StringZ,int color) {
        y = (float) ((double) y + 1.55);
        startDrawing(x+0.5, y, z+0.5,  StringX, StringY, StringZ);
        drawNames(Str,color);
        GL11.glColor4d((double) 1.0, (double) 1.0, (double) 1.0, (double) 1.0);
        stopDrawing();
    }

    private static void drawNames(String Str,int Color) {
        float xP = 2.2f;
        float width = (float) getWidth(Str) / 2.0f + xP;
        float w = width = (float) ((double) width + 2.5);
        float nw = -width - xP;
        float offset = getWidth(Str) + 4;
        RenderUtils.drawBorderedRect(nw + 6.0f, -1.0f, width, 10.0f, 1.0f, new Color(20, 20, 20, 0).getRGB(),
                new Color(10, 10, 10, 200).getRGB());
        GlStateManager.disableDepth();
        drawString(Str, w - offset + 2, 0.0f, Color);
        GlStateManager.enableDepth();
    }
    private static void drawString(String text, float x, float y, int color) {
        font.drawStringWithShadow(text, x, y, color);
    }

    private static int getWidth(String text) {
        return font.getStringWidth(text);
    }

    @EventTarget
    public void RenderWall(Render3DEvent event) {
        Iterator x = blocks.iterator();
        if(ESP.getValue()) {
            while (x.hasNext()) {
                XRayBlock x1 = (XRayBlock) x.next();
                if ((x1.type.contains("Diamond")) && DiamondOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(228, 228, 65, 50),
                            new Color(0,128,255).getRGB(), thicknessValue.get());
                if ((x1.type.contains("Gold"))&&GoldOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(184, 134, 11),
                            new Color(255, 255, 0).getRGB(), thicknessValue.get());
                if ((x1.type.contains("Iron"))&&IronOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(184, 134, 11),
                            new Color(214,173,145).getRGB(), thicknessValue.get());
                if ((x1.type.contains("Redstone"))&&RedStoneOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(184, 134, 11),
                            new Color(255, 0, 0).getRGB(), thicknessValue.get());
                if ((x1.type.contains("Coal"))&&CoalOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(184, 134, 11),
                            new Color(0, 0, 0).getRGB(), thicknessValue.get());
                if ((x1.type.contains("Lapis"))&&LapisOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(184, 134, 11),
                            new Color(27,74,161).getRGB(), thicknessValue.get());
                if ((x1.type.contains("Emerald"))&&EmeraldOre.getValue())
                    RenderUtils.drawblock(x1.x - mc.getRenderManager().viewerPosX, x1.y - mc.getRenderManager().viewerPosY,
                            x1.z - mc.getRenderManager().viewerPosZ, getColor(184, 134, 11),
                            new Color(23,221,98).getRGB(), thicknessValue.get());

            }


        }
    }

    public static int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        color |= blue;
        return color;
    }

    public int getOpacity() {
        return this.opacity;
    }
}
