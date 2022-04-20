/*
 Copyright Alan Wood 2021
 None of this code to be reused without my written permission
 Intellectual Rights owned by Alan Wood
 */
package net.ccbluex.liquidbounce.features.module.modules.render;


import net.ccbluex.liquidbounce.event.AttackEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "AttackEffects", description = "Rise.", category = ModuleCategory.RENDER)
public final class AttackEffects extends Module {
    public static final ListValue mode = new ListValue("Mode", new String[]{
            "Blood",
            "Criticals",
            "Magic"
    }, "Blood");
    public final IntegerValue amount = new IntegerValue("Amount", 5, 1, 10);
    private final BoolValue sound = new BoolValue("Sound", true);
    private EntityLivingBase target;

    @EventTarget
    public void onAttackEvent(final AttackEvent event) {
        if (event.getTargetEntity() instanceof EntityLivingBase)
            target = (EntityLivingBase) event.getTargetEntity();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
      if(event.isPre()){
        if (target != null && target.hurtTime >= 9 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
            if (mc.thePlayer.ticksExisted > 3) {
                switch (mode.get().toLowerCase()) {
                    case "blood":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.theWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK, target.posX, target.posY + target.height - 0.75, target.posZ, 0, 0, 0, Block.getStateId(Blocks.redstone_block.getDefaultState()));

                        if (sound.get())
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("dig.stone"), ((float) target.posX), ((float) target.posY), ((float) target.posZ)));
                        break;

                    case "criticals":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                        break;

                    case "magic":
                        for (int i = 0; i < amount.getValue(); i++)
                            mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC);
                        break;
                }
            }

            target = null;
        }
    }
}
}
