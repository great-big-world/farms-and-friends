package dev.creoii.farmsandfriends.registry;

import dev.creoii.farmsandfriends.block.OvenBlock;
import dev.creoii.greatbigworld.GreatBigWorld;
import dev.creoii.greatbigworld.util.RegistryHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class FarmsAndFriendsBlocks {
    public static Block OVEN;

    public static void register() {
        OVEN = RegistryHelper.registerBlock(Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), OvenBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BLAST_FURNACE));
    }
}
