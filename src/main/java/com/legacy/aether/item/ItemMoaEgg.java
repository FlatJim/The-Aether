package com.legacy.aether.item;

import com.legacy.aether.api.AetherAPI;
import com.legacy.aether.api.moa.MoaType;
import com.legacy.aether.entities.passive.EntityMoa;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;

public class ItemMoaEgg extends Item {

    public ItemMoaEgg() {
        super(new Item.Settings().maxCount(1).group(AetherItemGroup.AETHER_MISC));
    }

    public static ItemStack getStack(MoaType type) {
        ItemStack stack = new ItemStack(ItemsAether.moa_egg);

        CompoundTag tag = new CompoundTag();

        tag.putInt("moaType", AetherAPI.instance().getMoaId(type));

        stack.setTag(tag);

        return stack;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext contextIn) {
        if (contextIn.getPlayer().abilities.creativeMode) {
            EntityMoa moa = new EntityMoa(contextIn.getWorld(), AetherAPI.instance().getMoa(contextIn.getStack().getTag().getInt("moaType")));

            moa.refreshPositionAndAngles(contextIn.getBlockPos().up(), 1.0F, 1.0F);
            moa.setPlayerGrown(true);

            if (!contextIn.getWorld().isClient) {
                contextIn.getWorld().spawnEntity(moa);
            }

            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(contextIn);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> subItems) {
        for (int moaTypeSize = 0; moaTypeSize < AetherAPI.instance().getMoaRegistrySize(); ++moaTypeSize) {
            ItemStack stack = new ItemStack(this);
            CompoundTag compound = new CompoundTag();
            MoaType moaType = AetherAPI.instance().getMoa(moaTypeSize);

            if (moaType.getItemGroup() == group || group == ItemGroup.SEARCH) {
                compound.putInt("moaType", moaTypeSize);
                stack.setTag(compound);

                subItems.add(stack);
            }
        }
    }

    @Override
    public boolean shouldSyncTagToClient() {
        return true;
    }

    public int getColor(ItemStack stack) {
        return this.getMoaType(stack).getMoaEggColor();
    }

    public MoaType getMoaType(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {

            return AetherAPI.instance().getMoa(tag.getInt("moaType"));
        }

        return AetherAPI.instance().getMoa(0);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null && stack.getTag().contains("moaType")) {
            MoaType moaType = AetherAPI.instance().getMoa(tag.getInt("moaType"));

            return "item." + moaType.getRegistryName().getNamespace() + "." + moaType.getRegistryName().getPath().replace(" ", "_").toLowerCase() + "_moa_egg";
        }

        return super.getTranslationKey(stack);
    }

}