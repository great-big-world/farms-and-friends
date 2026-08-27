package dev.creoii.farmsandfriends.mixin.client;

import dev.creoii.farmsandfriends.registry.FarmsAndFriendsRecipes;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(SearchRecipeBookCategory.class)
public class SearchRecipeBookCategoryMixin {
    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static SearchRecipeBookCategory create(String internalName, int internalId, RecipeBookCategory... categories) {
        throw new AssertionError();
    }

    @Shadow @Final @Mutable private static SearchRecipeBookCategory[] $VALUES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/client/gui/screens/recipebook/SearchRecipeBookCategory;$VALUES:[Lnet/minecraft/client/gui/screens/recipebook/SearchRecipeBookCategory;", shift = At.Shift.AFTER))
    private static void addCustomRecipeBookGroup(CallbackInfo ci) {
        ArrayList<SearchRecipeBookCategory> values = new ArrayList<>(Arrays.asList($VALUES));
        int last = values.size();

        SearchRecipeBookCategory kiln = create("GBW_OVEN", last, FarmsAndFriendsRecipes.OVEN_CATEGORY);
        values.add(kiln);

        $VALUES = values.toArray(new SearchRecipeBookCategory[0]);
    }
}
