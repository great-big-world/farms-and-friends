package dev.creoii.farmsandfriends.registry;

import dev.creoii.farmsandfriends.client.OvenScreen;
import dev.creoii.farmsandfriends.menu.OvenMenu;
import dev.creoii.greatbigworld.GreatBigWorld;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class FarmsAndFriendsScreens {
    public static MenuType<OvenMenu> OVEN;

    public static void register() {
        OVEN = Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), new MenuType<>(OvenMenu::new, FeatureFlags.VANILLA_SET));
    }

    public static void registerClient() {
        MenuScreens.register(OVEN, OvenScreen::new);
    }
}
