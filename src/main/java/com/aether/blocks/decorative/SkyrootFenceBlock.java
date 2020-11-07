package com.aether.blocks.decorative;

import com.aether.blocks.AetherBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

public class SkyrootFenceBlock extends FenceBlock {
    public SkyrootFenceBlock() {
        super(FabricBlockSettings.copy(AetherBlocks.SKYROOT_PLANKS));
    }
}