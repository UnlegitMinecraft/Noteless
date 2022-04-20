package core.Insane;


import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.AxisAlignedBB;


import static net.ccbluex.liquidbounce.utils.render.ColorUtil.interpolate;
import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "AsianHat", description = "Flux", category = ModuleCategory.RENDER)

public class RacistHat extends Module {


    private final BoolValue renderInFirstPerson = new BoolValue("ShowInFirstPerson", false);


    @EventTarget
    public void onRender3D(Render3DEvent evt) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.isInvisible() || mc.thePlayer.isDead) return;
        if (!renderInFirstPerson.get() && mc.gameSettings.thirdPersonView == 0) return;

        double posX = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                posY = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                posZ = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

        AxisAlignedBB axisalignedbb = mc.thePlayer.getEntityBoundingBox();
        double height = axisalignedbb.maxY - axisalignedbb.minY + 0.02,
                radius = axisalignedbb.maxX - axisalignedbb.minX;

        glPushMatrix();
        GlStateManager.disableCull();
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glDisable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glEnable(GL_BLEND);
        GlStateManager.disableLighting();
        GlStateManager.color(1, 1, 1, 1);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);

        float yaw = interpolate(mc.thePlayer.prevRotationYaw, mc.thePlayer.rotationYaw, mc.timer.renderPartialTicks).floatValue();
        float pitchInterpolate = interpolate(mc.thePlayer.prevRenderArmPitch, mc.thePlayer.renderArmPitch, mc.timer.renderPartialTicks).floatValue();

        glTranslated(posX, posY, posZ);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glRotated(yaw, 0, -1, 0);
        glRotated(pitchInterpolate / 3.0, 0, 0, 0);
        glTranslatef(0, 0, pitchInterpolate / 270.0F);
        glLineWidth(2);
        glBegin(GL_LINE_LOOP);

        // outline/border or whatever you call it
        for (int i = 0; i <= 180; i++) {
            int color1 = ColorUtil.rainbow(7, i * 4, 1, 1, .5f).getRGB();
            GlStateManager.color(1, 1, 1, 1);
            RenderUtils.color(color1);
            glVertex3d(
                    posX - Math.sin(i * Math.PI / 90) * radius,
                    posY + height - (mc.thePlayer.isSneaking() ? 0.23 : 0) - 0.002,
                    posZ + Math.cos(i * Math.PI / 90) * radius
            );
        }
        glEnd();

        glBegin(GL_TRIANGLE_FAN);
        int color12 = ColorUtil.rainbow(7, 4, 1, 1, .7f).getRGB();
        RenderUtils.color(color12);
        glVertex3d(posX, posY + height + 0.3 - (mc.thePlayer.isSneaking() ? 0.23 : 0), posZ);

        // draw hat
        for (int i = 0; i <= 180; i++) {
            int color1 = ColorUtil.rainbow(7, i * 4, 1, 1, .2f).getRGB();
            GlStateManager.color(1, 1, 1, 1);
            RenderUtils.color(color1);
            glVertex3d(posX - Math.sin(i * Math.PI / 90) * radius,
                    posY + height - (mc.thePlayer.isSneaking() ? 0.23F : 0),
                    posZ + Math.cos(i * Math.PI / 90) * radius
            );

        }
        glVertex3d(posX, posY + height + 0.3 - (mc.thePlayer.isSneaking() ? 0.23 : 0), posZ);
        glEnd();


        glPopMatrix();

        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_FLAT);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
    }
}
