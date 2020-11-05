package com.legacy.aether.item.armor;

import com.legacy.aether.item.AetherItemGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Rarity;

public class ItemAetherArmor extends ArmorItem {

    private final AetherArmorType typeIn;
    private String armorName = "iron";

    public ItemAetherArmor(AetherArmorType typeIn, EquipmentSlot slotIn) {
        super(typeIn.getMaterial(), slotIn, new Settings().group(AetherItemGroup.AETHER_ARMOR));

        this.typeIn = typeIn;
    }

    public ItemAetherArmor(AetherArmorType typeIn, Rarity rarityIn, EquipmentSlot slotIn) {
        super(typeIn.getMaterial(), slotIn, new Settings().group(AetherItemGroup.AETHER_ARMOR).rarity(rarityIn));

        this.typeIn = typeIn;
    }

    public ItemAetherArmor(String nameIn, AetherArmorType typeIn, EquipmentSlot slotIn) {
        this(typeIn, slotIn);

        this.armorName = nameIn;
    }

    public ItemAetherArmor(String nameIn, AetherArmorType typeIn, Rarity rarityIn, EquipmentSlot slotIn) {
        this(typeIn, rarityIn, slotIn);

        this.armorName = nameIn;
    }

    public AetherArmorType getType() {
        return this.typeIn;
    }

    public String getArmorName() {
        return this.armorName;
    }

}