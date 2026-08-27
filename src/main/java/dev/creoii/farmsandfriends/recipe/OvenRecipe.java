package dev.creoii.farmsandfriends.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsItems;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class OvenRecipe implements Recipe<OvenRecipeInput> {
    private final List<Ingredient> ingredients;
    private final ItemStack result;
    private final String group;
    private final CookingBookCategory category;
    private final float experience;
    private final int cookingTime;
    private @Nullable PlacementInfo placementInfo;

    public OvenRecipe(String group, CookingBookCategory category, List<Ingredient> ingredients, ItemStack result, float experience, int cookingTime) {
        this.group = group;
        this.category = category;
        this.ingredients = ingredients;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    public ItemStack result() {
        return result;
    }

    public List<Ingredient> ingredients() {
        return ingredients;
    }

    public float experience() {
        return this.experience;
    }

    public int cookingTime() {
        return this.cookingTime;
    }

    public CookingBookCategory category() {
        return this.category;
    }

    @Override
    public String group() {
        return group;
    }

    public List<RecipeDisplay> display() {
        return List.of(new FurnaceRecipeDisplay(ingredients.getFirst().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.ItemStackSlotDisplay(result), new SlotDisplay.ItemSlotDisplay(FarmsAndFriendsItems.OVEN), cookingTime, experience));
    }

    public RecipeSerializer<OvenRecipe> getSerializer() {
        return FarmsAndFriendsRecipes.OVEN;
    }

    public RecipeType<OvenRecipe> getType() {
        return FarmsAndFriendsRecipes.OVEN_TYPE;
    }

    @Override
    public boolean matches(OvenRecipeInput recipeInput, Level level) {
        if (recipeInput.ingredientCount() != ingredients.size()) {
            return false;
        }
        return recipeInput.stackedContents().canCraft(this, null);
    }

    @Override
    public ItemStack assemble(OvenRecipeInput recipeInput, HolderLookup.Provider provider) {
        return result.copy();
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.create(ingredients);
        }
        return placementInfo;
    }

    public RecipeBookCategory recipeBookCategory() {
        return FarmsAndFriendsRecipes.OVEN_CATEGORY;
    }

    public static class Serializer implements RecipeSerializer<dev.creoii.farmsandfriends.recipe.OvenRecipe> {
        private final MapCodec<OvenRecipe> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> streamCodec;

        public Serializer(OvenRecipe.Factory<OvenRecipe> factory, int i) {
            this.codec = RecordCodecBuilder.mapCodec(instance -> {
                return instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(OvenRecipe::group),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(OvenRecipe::category),
                        Ingredient.CODEC.listOf(1, 6).fieldOf("ingredients").forGetter(OvenRecipe::ingredients),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(OvenRecipe::result),
                        Codec.FLOAT.fieldOf("experience").orElse(0f).forGetter(OvenRecipe::experience),
                        Codec.INT.fieldOf("cookingtime").orElse(i).forGetter(OvenRecipe::cookingTime)
                ).apply(instance, factory::create);
            });

            streamCodec = StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, OvenRecipe::group,
                    CookingBookCategory.STREAM_CODEC, OvenRecipe::category,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), OvenRecipe::ingredients,
                    ItemStack.STREAM_CODEC, OvenRecipe::result,
                    ByteBufCodecs.FLOAT, OvenRecipe::experience,
                    ByteBufCodecs.INT, OvenRecipe::cookingTime,
                    factory::create);
        }

        public MapCodec<OvenRecipe> codec() {
            return codec;
        }

        public StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> streamCodec() {
            return streamCodec;
        }
    }

    @FunctionalInterface
    public interface Factory<T extends OvenRecipe> {
        T create(String group, CookingBookCategory category, List<Ingredient> ingredients, ItemStack itemStack, float experience, int cookingTime);
    }
}
