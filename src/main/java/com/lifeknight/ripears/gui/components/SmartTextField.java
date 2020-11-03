package com.lifeknight.ripears.gui.components;

import com.lifeknight.ripears.utilities.Video;
import com.lifeknight.ripears.variables.SmartVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public abstract class SmartTextField extends GuiTextField {
    private final int width;
    private final int height;
    public boolean hovered = false;
    private boolean wasHovered = false;
    private long lastHoverTime = 0L;
    public String name;
    public final SmartVariable smartVariable;
    public String lastInput = "";
    public int originalYPosition = 0;
    private String subDisplayMessage = "";

    public SmartTextField(int componentId, int x, int y, int par5Width, int par6Height, String name, SmartVariable smartVariable) {
        super(componentId, Minecraft.getMinecraft().fontRendererObj, x, y, par5Width, par6Height);
        this.width = par5Width;
        this.height = par6Height;
        this.name = name;
        this.smartVariable = smartVariable;
        super.setMaxStringLength(100);
        this.setFocused(false);
        this.setCanLoseFocus(true);
    }

    public SmartTextField(int componentId, int x, int y, int par5Width, int par6Height, String name) {
        this(componentId, x, y, par5Width, par6Height, name, null);
    }

    public SmartTextField(int componentId, int y, SmartVariable smartVariable) {
        this(componentId, Video.get2ndPanelCenter() - 100,
                y,
                200,
                20,
                smartVariable.getCustomDisplayString(),
                smartVariable);
        this.originalYPosition = this.yPosition;
    }

    public boolean hasSmartVariable() {
        return this.smartVariable != null;
    }

    private int getHalfNameWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.name) / 2;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        this.hovered = mouseX >= Math.min(this.xPosition + this.width / 2 - this.getHalfNameWidth(), this.xPosition) &&
                mouseX <= Math.max(this.xPosition + this.width / 2 + this.getHalfNameWidth(), this.xPosition + this.width) &&
                mouseY >= this.yPosition - 16 &&
                mouseY <= this.yPosition + this.height;

        return this.hovered;
    }

    public void tryRender(int mouseX, int mouseY) {
        long timeSinceHover = System.currentTimeMillis() - this.lastHoverTime;

        if (timeSinceHover > 750L && this.wasHovered && !this.isFocused()) {
            this.smartVariable.drawDescription(mouseX, mouseY);
        }
    }

    public void drawTextBoxAndName() {
        super.drawTextBox();
        if (this.hasSmartVariable()) this.name = this.smartVariable.getCustomDisplayString();

        super.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, this.name, this.xPosition + this.width / 2, this.yPosition - 15, 0xffffffff);
        if (this.hasSmartVariable()) {
            if (this.smartVariable.hasDescription()) {
                if (this.hovered) {
                    if (!this.wasHovered) this.lastHoverTime = System.currentTimeMillis();
                    this.wasHovered = true;
                } else {
                    this.wasHovered = false;
                }
            }
        }
    }

    public void drawStringBelowBox() {
        super.drawCenteredString(Minecraft.getMinecraft().fontRendererObj, this.subDisplayMessage, this.xPosition + this.width / 2, this.yPosition + this.height + 10, 0xffffffff);
    }

    public boolean textboxKeyTyped(char characterTyped, int keyCode) {
        if (super.textboxKeyTyped(characterTyped, keyCode)) {
            return true;
        } else if (this.isFocused() && keyCode == 0x1C) {
            this.handleInput();
            return true;
        }
        return false;
    }

    public void updateOriginalYPosition() {
        this.originalYPosition = this.yPosition;
    }

    public abstract void handleInput();

    public void setSubDisplayMessage(String subDisplayMessage) {
        this.subDisplayMessage = subDisplayMessage;
    }
}
