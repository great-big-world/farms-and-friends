package dev.creoii.farmsandfriends.mixin;

import net.minecraft.world.inventory.RecipeBookType;
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

@Mixin(RecipeBookType.class)
public class RecipeBookTypeMixin {
    @SuppressWarnings("InvokerTarget")
    @Invoker("<init>")
    private static RecipeBookType create(String internalName, int internalId) {
        throw new AssertionError();
    }

    @Shadow @Final @Mutable private static RecipeBookType[] $VALUES;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC, target = "Lnet/minecraft/world/inventory/RecipeBookType;$VALUES:[Lnet/minecraft/world/inventory/RecipeBookType;", shift = At.Shift.AFTER))
    private static void addCustomRecipeBookGroup(CallbackInfo ci) {
        ArrayList<RecipeBookType> values = new ArrayList<>(Arrays.asList($VALUES));
        int last = values.size();

        RecipeBookType oven = create("GBW_OVEN", last);
        values.add(oven);

        $VALUES = values.toArray(new RecipeBookType[0]);
    }
}
