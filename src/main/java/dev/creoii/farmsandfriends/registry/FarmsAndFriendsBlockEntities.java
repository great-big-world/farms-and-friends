package dev.creoii.farmsandfriends.registry;

import dev.creoii.farmsandfriends.block.entity.OvenBlockEntity;
import dev.creoii.greatbigworld.GreatBigWorld;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class FarmsAndFriendsBlockEntities {
    public static BlockEntityType<OvenBlockEntity> OVEN;

    public static void register() {
        OVEN = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), FabricBlockEntityTypeBuilder.create(OvenBlockEntity::new, FarmsAndFriendsBlocks.OVEN).build());
    }
}
