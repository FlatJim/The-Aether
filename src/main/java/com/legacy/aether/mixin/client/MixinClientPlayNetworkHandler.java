package com.legacy.aether.mixin.client;

import com.legacy.aether.entities.EntityTypesAether;
import com.legacy.aether.entities.block.EntityFloatingBlock;
import com.legacy.aether.entities.projectile.EntityEnchantedDart;
import com.legacy.aether.entities.projectile.EntityGoldenDart;
import com.legacy.aether.entities.projectile.EntityPoisonDart;
import com.legacy.aether.entities.projectile.EntityPoisonNeedle;
import com.legacy.aether.util.MapDimensionData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Shadow
    private ClientWorld world;

    @Shadow
    private MinecraftClient client;

    @Inject(method = "onEntitySpawn", at = @At("RETURN"))
    public void onAetherEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        double d0 = packet.getX();
        double d1 = packet.getY();
        double d2 = packet.getZ();
        int entityData = packet.getEntityData();

        Entity aetherEntity = null;
        EntityType<?> entityType = packet.getEntityTypeId();

        if (entityType == EntityTypesAether.FLOATING_BLOCK) {
            aetherEntity = new EntityFloatingBlock(this.world, d0, d1, d2, Block.getStateFromRawId(entityData));
            entityData = 0;
        } else if (entityType == EntityTypesAether.GOLDEN_DART) {
            aetherEntity = new EntityGoldenDart(d0, d1, d2, this.world);
        } else if (entityType == EntityTypesAether.ENCHANTED_DART) {
            aetherEntity = new EntityEnchantedDart(d0, d1, d2, this.world);
        } else if (entityType == EntityTypesAether.POISON_DART) {
            aetherEntity = new EntityPoisonDart(d0, d1, d2, this.world);
        } else if (entityType == EntityTypesAether.POISON_NEEDLE) {
            aetherEntity = new EntityPoisonNeedle(d0, d1, d2, this.world);
        }
		/*else if (packet.getType() == 588)
		{
			aetherEntity = new EntityMiniCloud(this.world, (EntityPlayer) this.world.getEntityByID(packet.getData()), 0);
			packet.setData(0);
		}
		else if (packet.getType() == 589)
		{
			aetherEntity = new EntityMiniCloud(this.world, (EntityPlayer) this.world.getEntityByID(packet.getData()), 1);
			packet.setData(0);
		}
		else if (packet.getType() == 590)
		{
			aetherEntity = new EntityCrystal(this.world, d0, d1, d2, EnumCrystalType.FIRE);
			packet.setData(0);
		}
		else if (packet.getType() == 591)
		{
			aetherEntity = new EntityCrystal(this.world, d0, d1, d2, EnumCrystalType.ICE);
			packet.setData(0);
		}
		else if (packet.getType() == 592)
		{
			aetherEntity = new EntityCrystal(this.world, d0, d1, d2, (EntityPlayer) this.world.getEntityByID(packet.getData()));
			packet.setData(0);
		}
		else if (packet.getType() == 593)
		{
			aetherEntity = new EntityPhoenixArrow(this.world, d0, d1, d2);
		}*/

        if (aetherEntity != null) {
            aetherEntity.updateTrackedPosition(d0, d1, d2);

            aetherEntity.pitch = (float) (packet.getPitch() * 360) / 256.0F;
            aetherEntity.yaw = (float) (packet.getYaw() * 360) / 256.0F;

            aetherEntity.setEntityId(packet.getId());
            aetherEntity.setUuid(packet.getUuid());

            this.world.addEntity(packet.getId(), aetherEntity);

            if (entityData > 0) {
                if (aetherEntity instanceof PersistentProjectileEntity) {
                    Entity entity3 = this.world.getEntityById(entityData - 1);

                    if (entity3 instanceof LivingEntity) {
                        PersistentProjectileEntity projectileEntity_1 = (PersistentProjectileEntity) aetherEntity;

                        projectileEntity_1.setOwner(entity3);

                        if (entity3 instanceof PlayerEntity) {
                            projectileEntity_1.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;

                            if (((PlayerEntity) entity3).abilities.creativeMode) {
                                projectileEntity_1.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }
                        }
                    }
                }

                aetherEntity.setVelocityClient(packet.getVelocityX() / 8000.0D, packet.getVelocityY() / 8000.0D, packet.getVelocityZ() / 8000.0D);
            }
        }
    }

    @Inject(method = "onMapUpdate", at = @At("RETURN"))
    public void onMapUpdateDimension(MapUpdateS2CPacket packet, CallbackInfo ci) {
        MapRenderer mapRenderer = this.client.gameRenderer.getMapRenderer();
        String string_1 = FilledMapItem.getMapName(packet.getId());
        MapState mapState = this.client.world.getMapState(string_1);//FilledMapItem.getMapState(this.client.world, string_1);

        mapState.dimension = ((MapDimensionData) packet).getDimension();

        mapRenderer.updateTexture(mapState);
    }

}