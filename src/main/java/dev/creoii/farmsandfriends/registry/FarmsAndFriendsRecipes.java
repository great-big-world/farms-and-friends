package dev.creoii.farmsandfriends.registry;

import dev.creoii.farmsandfriends.recipe.OvenRecipe;
import dev.creoii.greatbigworld.GreatBigWorld;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public final class FarmsAndFriendsRecipes {
    public static RecipeSerializer<OvenRecipe> OVEN;
    public static RecipeType<OvenRecipe> OVEN_TYPE;
    public static final ResourceKey<RecipePropertySet> OVEN_INPUT = ResourceKey.create(RecipePropertySet.TYPE_KEY, Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven_input"));
    public static final RecipeBookCategory OVEN_CATEGORY = new RecipeBookCategory();

    public static void register() {
        OVEN = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), new OvenRecipe.Serializer(OvenRecipe::new, 200));

        OVEN_TYPE = Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), new RecipeType<>() {
            public String toString() {
                return "oven";
            }
        });
        Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, Identifier.fromNamespaceAndPath(GreatBigWorld.NAMESPACE, "oven"), OVEN_CATEGORY);
    }
}
