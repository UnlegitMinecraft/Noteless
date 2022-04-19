package net.ccbluex.liquidbounce.utils.block;


import net.ccbluex.liquidbounce.event.BlockRenderEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.block.BlockBed;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class BlockUtil extends MinecraftInstance implements Listenable {
    public static final ArrayList<BlockPos> list = new ArrayList<>();
    @EventTarget
    public void onRenderBlock(BlockRenderEvent e) {
        BlockPos pos = new BlockPos(e.getX(), e.getY(), e.getZ());
        if (!list.contains(pos) && e.getBlock() instanceof BlockBed) {
            list.add(pos);
        }
    }
    @EventTarget
    public void onUpdate(UpdateEvent e) {
        list.removeIf(pos -> !(mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed));
    }

    @Override
    public boolean handleEvents() {
        return true;
    }
}
