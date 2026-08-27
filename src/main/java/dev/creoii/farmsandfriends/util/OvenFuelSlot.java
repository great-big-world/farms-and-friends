package dev.creoii.farmsandfriends.util;

import dev.creoii.farmsandfriends.menu.OvenMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class OvenFuelSlot extends Slot {
    private final OvenMenu menu;

    public OvenFuelSlot(OvenMenu menu, Container container, int i, int j, int k) {
        super(container, i, j, k);
        this.menu = menu;
    }

    public boolean mayPlace(ItemStack itemStack) {
        return menu.isFuel(itemStack) || isBucket(itemStack);
    }

    public int getMaxStackSize(ItemStack itemStack) {
        return isBucket(itemStack) ? 1 : super.getMaxStackSize(itemStack);
    }

    public static boolean isBucket(ItemStack itemStack) {
        return itemStack.is(Items.BUCKET);
    }
}
