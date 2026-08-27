package dev.creoii.farmsandfriends.registry;

import dev.creoii.greatbigworld.GreatBigWorld;
import dev.creoii.greatbigworld.util.RegistryHelper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class FarmsAndFriendsItems {
    public static Item OVEN;
    //public static Item BAKED_CARROT;
    //public static Item BAKED_BREAD;
    //public static Item CARROT_CAKE;
    //public static Item GOLDEN_APPLE_PIE;
    //public static Item GOLDEN_CARROT_CAKE;
    public static Item APPLE_PIE;
    public static Item SWEET_BERRY_PIE;
    //public static Item PUMPKIN_BREAD;

    public static void register() {
        OVEN = RegistryHelper.registerBlockItem(Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), FarmsAndFriendsBlocks.OVEN);

        APPLE_PIE = RegistryHelper.registerItem(Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "apple_pie"), new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(.3f).build()));
        SWEET_BERRY_PIE = RegistryHelper.registerItem(Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "sweet_berry_pie"), new Item.Properties().stacksTo(16).food(new FoodProperties.Builder().nutrition(8).saturationModifier(.3f).build()));

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.addAfter(Items.PUMPKIN_PIE, APPLE_PIE, SWEET_BERRY_PIE);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.addAfter(Items.SOUL_CAMPFIRE, OVEN);
        });
    }
}
