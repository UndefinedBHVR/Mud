package com.undefinedbhvr.mud;

import com.undefinedbhvr.mud.layout.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MudRenderer extends Screen {
    private final Map<String, Integer> elementColors = new HashMap<>();

    public MudRenderer() {
        super(Component.literal("Mud UI"));

        elementColors.put("sidebar", new Color(232, 59, 59, 128).getRGB());
        elementColors.put("sidebar-header", new Color(57, 121, 183, 128).getRGB());
        elementColors.put("sidebar-header2", new Color(154, 59, 232, 128).getRGB());
        elementColors.put("content", new Color(232, 203, 59, 128).getRGB());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        mc.getProfiler().push("profile.mud.layout");
        Layout layout = new Layout(width, height);

        layout.openElement("sidebar", sidebar -> {
            sidebar.setDirection(Direction.Vertical)
                    .setxSizing(new Sizing.Fixed(256))
                    .setySizing(new Sizing.Grow())
                    .setyAlign(Alignment.Center)
                    .setPaddingLeft(4)
                    .setPaddingRight(4)
                    .setChildGap(4);

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
                    .setySizing(new Sizing.Grow())
                    .setyAlign(Alignment.Center)
                    .setxAlign(Alignment.Center);
            layout.openElement("button", button -> {
                button.setDirection(Direction.Horizontal)
                        .setxSizing(new Sizing.Fixed(128))
                        .setySizing(new Sizing.Fixed(50));
            });
        });

        layout.finalizeLayout();
        mc.getProfiler().pop();
        super.render(graphics, mouseX, mouseY, delta);
        renderElement(graphics, layout.getRootElement());
    }

    private void renderElement(GuiGraphics graphics, Element element) {
        // Get the element's dimensions and position
        int x = (int) element.getScreenPositionX();
        int y = (int) element.getScreenPositionY();
        int width = (int) element.getWidth();
        int height = (int) element.getHeight();

        // Render the element background based on its ID
        String id = element.getId();
        int color = elementColors.getOrDefault(id, new Color(55, 255, 141, 128).getRGB());

        // Draw the element background
        graphics.fill(x, y, x + width, y + height, color);

        graphics.renderOutline(x, y, width, height, new Color(255, 255, 255, 255).getRGB());

        // Draw the element ID for debugging
        graphics.drawString(this.font, id, x + 5, y + 5, 0xFFFFFFFF, false);

        // Recursively render children
        for (Element child : element.getChildren()) {
            renderElement(graphics, child);
        }
    }
}
