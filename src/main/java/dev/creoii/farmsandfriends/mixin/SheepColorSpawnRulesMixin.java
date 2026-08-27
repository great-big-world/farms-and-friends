package dev.creoii.farmsandfriends.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.animal.sheep.SheepColorSpawnRules;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SheepColorSpawnRules.class)
public class SheepColorSpawnRulesMixin {
    @WrapOperation(method = "commonColors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/random/WeightedList$Builder;build()Lnet/minecraft/util/random/WeightedList;"))
    private static WeightedList<SheepColorSpawnRules.SheepColorProvider> gbw$addRareGreenSheep(WeightedList.Builder<SheepColorSpawnRules.SheepColorProvider> instance, Operation<WeightedList<SheepColorSpawnRules.SheepColorProvider>> original) {
        return original.call(instance.add(_ -> DyeColor.GREEN, 1));
    }
}
