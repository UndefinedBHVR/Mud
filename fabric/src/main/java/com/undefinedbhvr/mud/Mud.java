package com.undefinedbhvr.mud;

import com.mojang.blaze3d.platform.InputConstants;
import com.undefinedbhvr.mud.layout.Alignment;
import com.undefinedbhvr.mud.layout.Direction;
import com.undefinedbhvr.mud.layout.Layout;
import com.undefinedbhvr.mud.layout.Sizing;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Mud implements ModInitializer, ClientModInitializer {
    private static KeyMapping toggleUiKey;
    private static Layout testLayout;
    
    @Override
    public void onInitialize() {
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Create a test layout
        testLayout = createTestLayout();
        
        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
    
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("Registering Keybindings");
        // Register keybinding to toggle the UI
        toggleUiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.mud.toggleui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U,
            "category.mud.general"
        ));
        
        // Register tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleUiKey.consumeClick()) {
                Minecraft.getInstance().setScreen(
                        Minecraft.getInstance().screen instanceof MudRenderer
                        ? null
                        : new MudRenderer(testLayout)
                );
            }
        });
        
        Constants.LOG.info("Mud client initialized with test UI");
    }
    
    private Layout createTestLayout() {
        // Create context with root element - use current Minecraft window size
        Minecraft client = Minecraft.getInstance();
        int width = 1920;
        int height = 1080;
        
        Layout layout = new Layout(width, height);

        layout.openElement("sidebar", sidebar -> {
            sidebar.setDirection(Direction.Vertical)
                    .setxSizing(new Sizing.Fixed(256))
                    .setySizing(new Sizing.Grow())
                    .setxAlign(Alignment.Center);

            layout.openElement("sidebar-header", header -> {
                header.setDirection(Direction.Horizontal)
                        .setxSizing(new Sizing.Grow())
                        .setySizing(new Sizing.Fixed(50));
            });

            layout.openElement("sidebar-header2", header2 -> {
                header2.setDirection(Direction.Horizontal)
                        .setxSizing(new Sizing.Grow())
                        .setySizing(new Sizing.Fixed(50));
            });
        });

        layout.openElement("content", content -> {
            content.setDirection(Direction.Horizontal)
                    .setxSizing(new Sizing.Grow())
                    .setySizing(new Sizing.Grow());
        });

        layout.finalizeLayout();
        // Log how the layout is structured
        layout.prettyPrintElementTree();
        
        return layout;
    }
}
