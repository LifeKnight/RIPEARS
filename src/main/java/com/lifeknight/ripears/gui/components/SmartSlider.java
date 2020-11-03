package com.lifeknight.ripears.gui.components;

import com.lifeknight.ripears.utilities.Video;
import com.lifeknight.ripears.variables.SmartNumber;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiSlider;

public class SmartSlider extends GuiSlider {
    private boolean wasHovered = false;
    private long lastHoverTime = 0L;
    private final SmartNumber smartNumber;
    public int originalYPosition;

    public SmartSlider(int componentId, int yPosition, boolean showDecimals, SmartNumber smartNumber) {
        super(componentId, Video.get2ndPanelCenter() - 100,
                yPosition,
                200,
                20, smartNumber.getCustomDisplayString(), "", smartNumber.getMinimumAsDouble(), smartNumber.getMaximumAsDouble(), smartNumber.getAsDouble(), showDecimals, false);
        this.smartNumber = smartNumber;
        this.originalYPosition = this.yPosition;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
        this.smartNumber.setValue(this.getValue());
    }

    public void tryRender(int mouseX, int mouseY) {
        long timeSinceHover = System.currentTimeMillis() - this.lastHoverTime;

        if (timeSinceHover > 750L && this.wasHovered && !this.dragging) {
            this.smartNumber.drawDescription(mouseX, mouseY);
        }
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        this.minValue = this.smartNumber.getMinimumAsDouble();
        this.maxValue = this.smartNumber.getMaximumAsDouble();
        if (!this.dragging) {
            this.sliderValue = (this.smartNumber.getAsDouble() - this.minValue) / (this.maxValue - this.minValue);
        }
        this.displayString = this.smartNumber.getCustomDisplayString(this.getValue());
        super.drawButton(minecraft, mouseX, mouseY);
        if (this.smartNumber.hasDescription()) {
            if (this.hovered) {
                if (!this.wasHovered) this.lastHoverTime = System.currentTimeMillis();
                this.wasHovered = true;
            } else {
                this.wasHovered = false;
            }
        }
    }

    public void updateOriginalYPosition() {
        this.originalYPosition = this.yPosition;
    }
}