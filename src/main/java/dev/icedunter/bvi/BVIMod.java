package dev.icedunter.bvi;

import dev.icedunter.bvi.item.EnchantedGlisteringMelonItem;
import dev.icedunter.bvi.screen.FletcherTableScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class BVIMod implements ModInitializer {

    public static final ScreenHandlerType<FletcherTableScreenHandler> FLETCHER_SCREEN =
            new ExtendedScreenHandlerType<>(FletcherTableScreenHandler::new);

    public static final Item ENCHANTED_GLISTERING_MELON = new EnchantedGlisteringMelonItem(
            new Item.Settings()
                    .food(new net.minecraft.item.FoodComponent.Builder()
                            .hunger(2)
                            .saturationModifier(1.2f)
                            .alwaysEdible()
                            .build())
                    .maxCount(64)
    );

    public static final Potion POISON_POTATO = new Potion(
            new StatusEffectInstance(StatusEffects.POISON, 100));
    public static final Potion POISON_POTATO_LONG = new Potion(
            new StatusEffectInstance(StatusEffects.POISON, 200));
    public static final Potion POISON_POTATO_STRONG = new Potion(
            new StatusEffectInstance(StatusEffects.POISON, 60, 1));

    @Override
    public void onInitialize() {
        // Стол лучника
        Registry.register(Registries.SCREEN_HANDLER, new Identifier("bvi", "fletcher_screen"), FLETCHER_SCREEN);

        // Переписанный сверкающий ломатик арбуза
        Registry.register(Registries.ITEM, new Identifier("bvi", "enchanted_glistering_melon"), ENCHANTED_GLISTERING_MELON);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.add(ENCHANTED_GLISTERING_MELON);
        });

        // Зелья
        Registry.register(Registries.POTION, new Identifier("bvi", "poison_potato"), POISON_POTATO);
        Registry.register(Registries.POTION, new Identifier("bvi", "poison_potato_long"), POISON_POTATO_LONG);
        Registry.register(Registries.POTION, new Identifier("bvi", "poison_potato_strong"), POISON_POTATO_STRONG);
    }
}