package dev.creoii.farmsandfriends;

import dev.creoii.farmsandfriends.registry.*;
import net.fabricmc.api.ModInitializer;

public class FarmsAndFriends implements ModInitializer {
    @Override
    public void onInitialize() {
        FarmsAndFriendsBlocks.register();
        FarmsAndFriendsBlockEntities.register();
        FarmsAndFriendsItems.register();
        FarmsAndFriendsRecipes.register();
        FarmsAndFriendsScreens.register();
    }
}
