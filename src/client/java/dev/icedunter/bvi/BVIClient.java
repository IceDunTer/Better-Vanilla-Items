package dev.icedunter.bvi;

import dev.icedunter.bvi.screen.FletcherTableScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class BVIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(BVIMod.FLETCHER_SCREEN, FletcherTableScreen::new);
    }
}