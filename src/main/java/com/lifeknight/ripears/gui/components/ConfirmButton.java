package com.lifeknight.ripears.gui.components;

import net.minecraft.client.Minecraft;

public abstract class ConfirmButton extends SmartButton {
    private boolean hasConfirmed = false;
    private final String buttonText;
    private final String confirmText;
    public ConfirmButton(int componentId, int x, int y, int width, String buttonText, String confirmText) {
        super(buttonText, componentId, x, y, width);
        this.buttonText = buttonText;
        this.confirmText = confirmText;
    }

    public void work() {
        if (this.hasConfirmed) {
            this.onConfirm();
            this.reset();
        } else {
            this.displayString = this.confirmText;
            this.hasConfirmed = true;
        }
    }

    public void reset() {
        this.hasConfirmed = false;
        this.displayString = this.buttonText;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (!super.mousePressed(mc, mouseX, mouseY)) {
            this.reset();
            return false;
        }
        return true;
    }

    public abstract void onConfirm();
}
