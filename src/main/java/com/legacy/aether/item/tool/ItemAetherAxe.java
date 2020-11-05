package com.legacy.aether.item.tool;

import com.legacy.aether.entities.block.EntityFloatingBlock;
import com.legacy.aether.item.AetherItemGroup;
import com.legacy.aether.item.ItemsAether;
import com.legacy.aether.item.util.AetherTier;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAetherAxe extends AxeItem implements IAetherTool {

    public float[] zaniteHarvestLevels = new float[]{2F, 4F, 6F, 8F, 12F};
    private AetherTier material;

    public ItemAetherAxe(AetherTier material, float damageVsEntity, float attackSpeed) {
        super(material.getDefaultTier(), damageVsEntity, attackSpeed, new Settings().group(AetherItemGroup.AETHER_TOOLS));

        this.material = material;
    }

    public ItemAetherAxe(AetherTier material, Rarity rarity, float damageVsEntity, float attackSpeed) {
        super(material.getDefaultTier(), damageVsEntity, attackSpeed, new Settings().group(AetherItemGroup.AETHER_TOOLS).rarity(rarity));

        this.material = material;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        float original = super.getMiningSpeedMultiplier(stack, state);

        if (this.getItemMaterial() == AetherTier.Zanite) {
            return this.calculateIncrease(stack, original);
        }

        return original;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getBlockPos();
        BlockState iblockstate = world.getBlockState(blockpos);

        if (this.getItemMaterial() == AetherTier.Gravitite && this.getMiningSpeedMultiplier(context.getStack(), iblockstate) == this.miningSpeed) {
            if (world.isAir(blockpos.up()) && !world.isClient) {
                world.spawnEntity(new EntityFloatingBlock(world, blockpos.getX() + 0.5D, blockpos.getY(), blockpos.getZ() + 0.5D, world.getBlockState(blockpos)));
            } else {
                return ActionResult.PASS;
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public boolean postMine(ItemStack stackIn, World worldIn, BlockState stateIn, BlockPos posIn, LivingEntity entityIn) {
        if (!worldIn.isClient && this.getItemMaterial() == AetherTier.Holystone && worldIn.getRandom().nextInt(100) <= 5) {
            worldIn.spawnEntity(new ItemEntity(worldIn, posIn.getX(), posIn.getY(), posIn.getZ(), new ItemStack(ItemsAether.ambrosium_shard)));
        }

        return super.postMine(stackIn, worldIn, stateIn, posIn, entityIn);
    }

    @Override
    public AetherTier getItemMaterial() {
        return this.material;
    }

    private float calculateIncrease(ItemStack tool, float original) {
        boolean AllowedCalculations = !(original != 4.0F);
        int current = tool.getDamage();

        if (AllowedCalculations) {
            if (this.isBetween(tool.getDamage(), current, tool.getDamage() - 50)) {
                return this.zaniteHarvestLevels[4];
            } else if (this.isBetween(tool.getDamage() - 51, current, tool.getDamage() - 110)) {
                return this.zaniteHarvestLevels[3];
            } else if (this.isBetween(tool.getDamage() - 111, current, tool.getDamage() - 200)) {
                return this.zaniteHarvestLevels[2];
            } else if (this.isBetween(tool.getDamage() - 201, current, tool.getDamage() - 239)) {
                return this.zaniteHarvestLevels[1];
            }

            return this.zaniteHarvestLevels[0];
        }

        return 1.0F;
    }

    private boolean isBetween(int max, int origin, int min) {
        return origin <= max && origin >= min;
    }

}