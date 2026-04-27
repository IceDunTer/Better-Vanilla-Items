package dev.icedunter.bvi.mixin;

import dev.icedunter.bvi.BVIMod;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Inject(method = "registerDefaults", at = @At("RETURN"))
    private static void registerPoisonPotatoRecipes(CallbackInfo ci) {
        BrewingRecipeRegistry.registerPotionRecipe(
                Potions.AWKWARD,
                Items.POISONOUS_POTATO,
                BVIMod.POISON_POTATO);

        BrewingRecipeRegistry.registerPotionRecipe(
                BVIMod.POISON_POTATO,
                Items.REDSTONE,
                BVIMod.POISON_POTATO_LONG);

        BrewingRecipeRegistry.registerPotionRecipe(
                BVIMod.POISON_POTATO,
                Items.GLOWSTONE_DUST,
                BVIMod.POISON_POTATO_STRONG);
    }
}