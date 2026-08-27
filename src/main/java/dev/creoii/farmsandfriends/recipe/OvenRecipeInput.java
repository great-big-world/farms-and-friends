package dev.creoii.farmsandfriends.recipe;

import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class OvenRecipeInput implements RecipeInput {
    private final List<ItemStack> items;
    private final StackedItemContents contents = new StackedItemContents();
    private final int ingredientCount;

    public OvenRecipeInput(List<ItemStack> items) {
        this.items = items;
        int count = 0;

        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                count++;
                contents.accountStack(stack, 1);
            }
        }

        this.ingredientCount = count;
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }

    public StackedItemContents stackedContents() {
        return contents;
    }

    public int ingredientCount() {
        return ingredientCount;
    }
}