package net.ccbluex.liquidbounce.utils.item;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.util.Set;


public class ItemAx
        extends ItemTool {
    private static final Set EFFECTIVE_ON = Sets.newHashSet((Object[])new Block[]{Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Blocks.melon_block, Blocks.ladder});
    private final Item.ToolMaterial material;

    protected ItemAx(Item.ToolMaterial material) {
        super(3.0f, material, EFFECTIVE_ON);
        this.material = material;
    }

    public float getStrVsBlock(ItemStack stack, Block block) {
        return block.getMaterial() != Material.wood && block.getMaterial() != Material.plants && block.getMaterial() != Material.vine ? super.getStrVsBlock(stack, block) : this.efficiencyOnProperMaterial;
    }

    public float getDamageVsEntity() {
        return this.material.getDamageVsEntity();
    }
}
