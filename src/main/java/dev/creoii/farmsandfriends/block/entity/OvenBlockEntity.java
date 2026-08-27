package dev.creoii.farmsandfriends.block.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import dev.creoii.farmsandfriends.menu.OvenMenu;
import dev.creoii.farmsandfriends.recipe.OvenRecipe;
import dev.creoii.farmsandfriends.recipe.OvenRecipeInput;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsBlockEntities;
import dev.creoii.farmsandfriends.registry.FarmsAndFriendsRecipes;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class OvenBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> RECIPES_USED_CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2, 3, 4, 5};
    private static final int[] SLOTS_FOR_DOWN = new int[]{7, 6};
    private static final int[] SLOTS_FOR_SIDES = new int[]{6};
    private static final Component DEFAULT_NAME = Component.translatable("container.oven");
    private NonNullList<ItemStack> items;
    private int litTimeRemaining;
    private int litTotalTime;
    private int cookingTimer;
    private int cookingTotalTime;
    private  final ContainerData dataAccess;
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed;
    private final RecipeManager.CachedCheck<OvenRecipeInput, OvenRecipe> quickCheck;

    public OvenBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(FarmsAndFriendsBlockEntities.OVEN, blockPos, blockState);
        items = NonNullList.withSize(8, ItemStack.EMPTY);
        dataAccess = new ContainerData() {
            public int get(int i) {
                return switch (i) {
                    case 0 -> OvenBlockEntity.this.litTimeRemaining;
                    case 1 -> OvenBlockEntity.this.litTotalTime;
                    case 2 -> OvenBlockEntity.this.cookingTimer;
                    case 3 -> OvenBlockEntity.this.cookingTotalTime;
                    default -> 0;
                };
            }

            public void set(int i, int j) {
                switch (i) {
                    case 0 -> OvenBlockEntity.this.litTimeRemaining = j;
                    case 1 -> OvenBlockEntity.this.litTotalTime = j;
                    case 2 -> OvenBlockEntity.this.cookingTimer = j;
                    case 3 -> OvenBlockEntity.this.cookingTotalTime = j;
                }
            }

            public int getCount() {
                return 4;
            }
        };
        recipesUsed = new Reference2IntOpenHashMap<>();
        quickCheck = RecipeManager.createCheck(FarmsAndFriendsRecipes.OVEN_TYPE);
    }

    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new OvenMenu(i, inventory, this, dataAccess);
    }

    private boolean isLit() {
        return litTimeRemaining > 0;
    }

    protected void loadAdditional(ValueInput valueInput) {
        super.loadAdditional(valueInput);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(valueInput, items);
        cookingTimer = valueInput.getShortOr("cooking_time_spent", (short)0);
        cookingTotalTime = valueInput.getShortOr("cooking_total_time", (short)0);
        litTimeRemaining = valueInput.getShortOr("lit_time_remaining", (short)0);
        litTotalTime = valueInput.getShortOr("lit_total_time", (short)0);
        recipesUsed.clear();
        recipesUsed.putAll(valueInput.read("RecipesUsed", RECIPES_USED_CODEC).orElse(Map.of()));
    }

    protected void saveAdditional(ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putShort("cooking_time_spent", (short)cookingTimer);
        valueOutput.putShort("cooking_total_time", (short)cookingTotalTime);
        valueOutput.putShort("lit_time_remaining", (short)litTimeRemaining);
        valueOutput.putShort("lit_total_time", (short)litTotalTime);
        ContainerHelper.saveAllItems(valueOutput, items);
        valueOutput.store("RecipesUsed", RECIPES_USED_CODEC, recipesUsed);
    }

    public static void serverTick(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, OvenBlockEntity blockEntity) {
        boolean bl = blockEntity.isLit();
        boolean bl2 = false;
        if (blockEntity.isLit()) {
            --blockEntity.litTimeRemaining;
        }

        ItemStack fuelStack = blockEntity.items.get(6);
        ItemStack inputStack = blockEntity.items.get(0);
        boolean bl3 = !inputStack.isEmpty();
        boolean bl4 = !fuelStack.isEmpty();
        if (blockEntity.isLit() || bl4 && bl3) {
            OvenRecipeInput input = new OvenRecipeInput(blockEntity.items.subList(0, 6));
            RecipeHolder<OvenRecipe> recipeHolder;
            if (bl3) {
                recipeHolder = blockEntity.quickCheck.getRecipeFor(input, serverLevel).orElse(null);
            } else {
                recipeHolder = null;
            }

            int i = blockEntity.getMaxStackSize();
            if (!blockEntity.isLit() && canBurn(serverLevel.registryAccess(), recipeHolder, input, blockEntity.items, i)) {
                blockEntity.litTimeRemaining = blockEntity.getBurnDuration(serverLevel.fuelValues(), fuelStack);
                blockEntity.litTotalTime = blockEntity.litTimeRemaining;
                if (blockEntity.isLit()) {
                    bl2 = true;
                    if (bl4) {
                        Item item = fuelStack.getItem();
                        fuelStack.shrink(1);
                        if (fuelStack.isEmpty()) {
                            blockEntity.items.set(6, item.getCraftingRemainder());
                        }
                    }
                }
            }

            if (blockEntity.isLit() && canBurn(serverLevel.registryAccess(), recipeHolder, input, blockEntity.items, i)) {
                ++blockEntity.cookingTimer;
                if (blockEntity.cookingTimer == blockEntity.cookingTotalTime) {
                    blockEntity.cookingTimer = 0;
                    blockEntity.cookingTotalTime = getTotalCookTime(serverLevel, blockEntity);
                    if (burn(serverLevel.registryAccess(), recipeHolder, input, blockEntity.items, i)) {
                        blockEntity.setRecipeUsed(recipeHolder);
                    }

                    bl2 = true;
                }
            } else {
                blockEntity.cookingTimer = 0;
            }
        } else if (!blockEntity.isLit() && blockEntity.cookingTimer > 0) {
            blockEntity.cookingTimer = Mth.clamp(blockEntity.cookingTimer - 2, 0, blockEntity.cookingTotalTime);
        }

        if (bl != blockEntity.isLit()) {
            bl2 = true;
            blockState = blockState.setValue(AbstractFurnaceBlock.LIT, blockEntity.isLit());
            serverLevel.setBlock(blockPos, blockState, 3);
        }

        if (bl2) {
            setChanged(serverLevel, blockPos, blockState);
        }
    }

    private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<OvenRecipe> recipeHolder, OvenRecipeInput input, NonNullList<ItemStack> list, int i) {
        if (!input.isEmpty() && recipeHolder != null) {
            ItemStack result = recipeHolder.value().assemble(input, registryAccess);
            if (result.isEmpty()) {
                return false;
            } else {
                ItemStack output = list.get(7);
                if (output.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(output, result)) {
                    return false;
                } else if (output.getCount() < i && output.getCount() < output.getMaxStackSize()) {
                    return true;
                } else {
                    return output.getCount() < result.getMaxStackSize();
                }
            }
        } else return false;
    }

    private static boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<OvenRecipe> recipeHolder, OvenRecipeInput input, NonNullList<ItemStack> list, int i) {
        if (recipeHolder != null && canBurn(registryAccess, recipeHolder, input, list, i)) {
            ItemStack result = recipeHolder.value().assemble(input, registryAccess);
            ItemStack output = list.get(7);
            if (output.isEmpty()) {
                list.set(7, result.copy());
            } else if (ItemStack.isSameItemSameComponents(output, result)) {
                output.grow(1);
            }

            if (list.subList(0, 6).stream().anyMatch(stack -> stack.is(Blocks.WET_SPONGE.asItem())) && !list.get(6).isEmpty() && list.get(6).is(Items.BUCKET)) {
                list.set(6, new ItemStack(Items.WATER_BUCKET));
            }

            for (int j = 0; j < 6; ++j) {
                list.get(j).shrink(1);
            }
            return true;
        } else {
            return false;
        }
    }

    protected int getBurnDuration(FuelValues fuelValues, ItemStack itemStack) {
        return fuelValues.burnDuration(itemStack);
    }

    private static int getTotalCookTime(ServerLevel serverLevel, OvenBlockEntity ovenBlockEntity) {
        OvenRecipeInput input = new OvenRecipeInput(ovenBlockEntity.items.subList(0, 6));
        return ovenBlockEntity.quickCheck.getRecipeFor(input, serverLevel).map((recipeHolder) -> recipeHolder.value().cookingTime()).orElse(200);
    }

    public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        } else {
            return direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemStack, @Nullable Direction direction) {
        return canPlaceItem(i, itemStack);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemStack, Direction direction) {
        if (direction == Direction.DOWN && i == 6) {
            return itemStack.is(Items.WATER_BUCKET) || itemStack.is(Items.BUCKET);
        } else {
            return true;
        }
    }

    public int getContainerSize() {
        return items.size();
    }

    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    protected void setItems(NonNullList<ItemStack> nonNullList) {
        items = nonNullList;
    }

    public void setItem(int i, ItemStack itemStack) {
        ItemStack itemStack2 = items.get(i);
        boolean bl = !itemStack.isEmpty() && ItemStack.isSameItemSameComponents(itemStack2, itemStack);
        items.set(i, itemStack);
        itemStack.limitSize(getMaxStackSize(itemStack));
        if (i >= 0 && i < 6 && !bl) {
            if (level instanceof ServerLevel serverLevel) {
                cookingTotalTime = getTotalCookTime(serverLevel, this);
                cookingTimer = 0;
                setChanged();
            }
        }
    }

    public boolean canPlaceItem(int i, ItemStack itemStack) {
        if (i == 7) {
            return false;
        } else if (i < 6 && i >= 0) {
            return true;
        } else {
            ItemStack itemStack2 = items.get(6);
            return level.fuelValues().isFuel(itemStack) || itemStack.is(Items.BUCKET) && !itemStack2.is(Items.BUCKET);
        }
    }

    public void setRecipeUsed(@Nullable RecipeHolder<?> recipeHolder) {
        if (recipeHolder != null) {
            ResourceKey<Recipe<?>> resourceKey = recipeHolder.id();
            recipesUsed.addTo(resourceKey, 1);
        }
    }

    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    public void awardUsedRecipes(Player player, List<ItemStack> list) {
    }

    public void awardUsedRecipesAndPopExperience(ServerPlayer serverPlayer) {
        List<RecipeHolder<?>> list = getRecipesToAwardAndPopExperience(serverPlayer.level(), serverPlayer.position());
        serverPlayer.awardRecipes(list);

        for(RecipeHolder<?> recipeHolder : list) {
            serverPlayer.triggerRecipeCrafted(recipeHolder, items);
        }

        recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel serverLevel, Vec3 vec3) {
        List<RecipeHolder<?>> list = Lists.newArrayList();

        for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> entry : recipesUsed.reference2IntEntrySet()) {
            serverLevel.recipeAccess().byKey(entry.getKey()).ifPresent((recipeHolder) -> {
                list.add(recipeHolder);
                createExperience(serverLevel, vec3, entry.getIntValue(), ((OvenRecipe) recipeHolder.value()).experience());
            });
        }

        return list;
    }

    private static void createExperience(ServerLevel serverLevel, Vec3 vec3, int i, float f) {
        int j = Mth.floor((float)i * f);
        float g = Mth.frac((float)i * f);
        if (g != 0f && serverLevel.random.nextFloat() < g) {
            ++j;
        }

        ExperienceOrb.award(serverLevel, vec3, j);
    }

    public void fillStackedContents(StackedItemContents stackedItemContents) {
        for(ItemStack itemStack : items) {
            stackedItemContents.accountStack(itemStack);
        }
    }

    public void preRemoveSideEffects(BlockPos blockPos, BlockState blockState) {
        super.preRemoveSideEffects(blockPos, blockState);
        if (level instanceof ServerLevel serverLevel) {
            getRecipesToAwardAndPopExperience(serverLevel, Vec3.atCenterOf(blockPos));
        }
    }
}
