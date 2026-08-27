package dev.creoii.farmsandfriends.client;

import dev.creoii.farmsandfriends.registry.FarmsAndFriendsBlocks;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class FarmsAndFriendsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FarmsAndFriendsScreens.registerClient();

        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT, FarmsAndFriendsBlocks.ROSE, FarmsAndFriendsBlocks.CYAN_ROSE, FarmsAndFriendsBlocks.MARIGOLD);
    }
}
