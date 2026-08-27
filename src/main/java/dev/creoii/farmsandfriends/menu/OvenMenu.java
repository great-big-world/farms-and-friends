package dev.creoii.farmsandfriends.menu;

import dev.creoii.farmsandfriends.recipe.OvenRecipe;
import dev.creoii.farmsandfriends.recipe.OvenRecipeInput;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsRecipes;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsScreens;
import dev.creoii.farmsandfriends.util.FarmsAndFriendsRecipeBookTypes;
import dev.creoii.farmsandfriends.util.OvenFuelSlot;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class OvenMenu extends RecipeBookMenu {
    final Container container;
    private final ContainerData data;
    protected final Level level;
    private final RecipePropertySet acceptedInputs;

    public OvenMenu(int i, Inventory inventory) {
        this(i, inventory, new SimpleContainer(8), new SimpleContainerData(4));
    }

    public OvenMenu(int i, Inventory inventory, Container container, ContainerData containerData) {
        super(FarmsAndFriendsScreens.OVEN, i);
        checkContainerSize(container, 8);
        checkContainerDataCount(containerData, 4);
        this.container = container;
        data = containerData;
        level = inventory.player.level();
        acceptedInputs = level.recipeAccess().propertySet(FarmsAndFriendsRecipes.OVEN_INPUT);
        addSlot(new Slot(container, 0, 30, 16));
        addSlot(new Slot(container, 1, 48, 16));
        addSlot(new Slot(container, 2, 66, 16));
        addSlot(new Slot(container, 3, 30, 34));
        addSlot(new Slot(container, 4, 48, 34));
        addSlot(new Slot(container, 5, 66, 34));
        addSlot(new OvenFuelSlot(this, container, 6, 48, 55));
        addSlot(new FurnaceResultSlot(inventory.player, container, 7, 124, 34));
        addStandardInventorySlots(inventory, 8, 84);
        addDataSlots(containerData);
    }

    public void fillCraftSlotsStackedContents(StackedItemContents stackedItemContents) {
        if (container instanceof StackedContentsCompatible compatible) {
            compatible.fillStackedContents(stackedItemContents);
        }
    }

    public Slot getResultSlot() {
        return slots.get(7);
    }

    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemStack = stack.copy();

            if (i == 7) {
                if (!moveItemStackTo(stack, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemStack);
            } else if (i >= 8) {
                if (canSmelt(stack)) {
                    if (!moveItemStackTo(stack, 0, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (isFuel(stack)) {
                    if (!moveItemStackTo(stack, 6, 7, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (i < 35) { // inventory → hotbar
                    if (!moveItemStackTo(stack, 35, 44, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(stack, 8, 35, false)) { // hotbar → inventory
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(stack, 8, 44, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemStack;
    }

    protected boolean canSmelt(ItemStack itemStack) {
        return acceptedInputs.test(itemStack);
    }

    public boolean isFuel(ItemStack itemStack) {
        return level.fuelValues().isFuel(itemStack);
    }

    public float getBurnProgress() {
        int i = data.get(2);
        int j = data.get(3);
        return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0f, 1f) : 0f;
    }

    public float getLitProgress() {
        int i = data.get(1);
        if (i == 0) {
            i = 200;
        }

        return Mth.clamp((float)data.get(0) / (float)i, 0f, 1f);
    }

    public boolean isLit() {
        return data.get(0) > 0;
    }

    public RecipeBookType getRecipeBookType() {
        return FarmsAndFriendsRecipeBookTypes.OVEN;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PostPlaceAction handlePlacement(boolean bl, boolean bl2, RecipeHolder<?> recipeHolder, ServerLevel serverLevel, Inventory inventory) {
        final List<Slot> inputs = List.of(getSlot(0), getSlot(1), getSlot(2), getSlot(3), getSlot(4), getSlot(5));
        final List<Slot> slots = List.of(getSlot(0), getSlot(1), getSlot(2), getSlot(3), getSlot(4), getSlot(5), getSlot(7));
        return ServerPlaceRecipe.placeRecipe(new OvenMenuAccess(inputs), 3, 2, slots, inputs, inventory, (RecipeHolder<OvenRecipe>) recipeHolder, bl, bl2);
    }

    public class OvenMenuAccess implements ServerPlaceRecipe.CraftingMenuAccess<OvenRecipe> {
        private final List<Slot> list;

        public OvenMenuAccess(List<Slot> list) {
            this.list = list;
        }

        @Override
        public void fillCraftSlotsStackedContents(StackedItemContents stackedItemContents) {
            OvenMenu.this.fillCraftSlotsStackedContents(stackedItemContents);
        }

        @Override
        public void clearCraftingContent() {
            list.forEach(slot -> slot.set(ItemStack.EMPTY));
        }

        @Override
        public boolean recipeMatches(RecipeHolder<OvenRecipe> recipeHolder) {
            List<ItemStack> itemStacks = new ArrayList<>();
            for (int i = 0; i < 6; ++i) {
                itemStacks.add(OvenMenu.this.container.getItem(i));
            }
            return recipeHolder.value().matches(new OvenRecipeInput(itemStacks), OvenMenu.this.level);
        }
    }
}
