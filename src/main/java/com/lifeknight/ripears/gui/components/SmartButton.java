package com.lifeknight.ripears.gui.components;

import com.lifeknight.ripears.utilities.Video;
import com.lifeknight.ripears.variables.SmartBoolean;
import com.lifeknight.ripears.variables.SmartVariable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.input.Mouse;

import java.awt.*;

public abstract class SmartButton extends GuiButton {
    public int originalYPosition = 0;

    public SmartButton(int componentId, int yPosition, String buttonText) {
        super(componentId, Video.get2ndPanelCenter() - 100,
                yPosition,
                200,
                20, buttonText);
        int j;
        if ((j = Minecraft.getMinecraft().fontRendererObj.getStringWidth(buttonText) + 30) > this.width) {
            this.width = j;
            this.xPosition = Video.get2ndPanelCenter() - this.width / 2;
        }
        this.originalYPosition = this.yPosition;
    }

    public SmartButton(int componentId, int x, int y, int width, int height, String buttonText) {
        super(componentId, x, y, width, height, buttonText);
    }

    public SmartButton(String buttonText, int componentId, int x, int y, int width) {
        super(componentId, x,
                y,
                width,
                20,
                buttonText);
    }

    public SmartButton(String buttonText) {
        super(0, 0, 0, 200, 20, buttonText);
    }

    public void updateOriginalYPosition() {
        this.originalYPosition = this.yPosition;
    }

    public abstract void work();

    public static class VersatileSmartButton extends SmartButton {
        public IAction iAction;

        public VersatileSmartButton(String buttonText, IAction iAction) {
            super(0, 0, 0, Minecraft.getMinecraft().fontRendererObj.getStringWidth(buttonText) + 15, 20, buttonText);
            this.iAction = iAction;
        }

        @Override
        public void work() {
            if (this.iAction != null) this.iAction.work(this);
        }
    }

    public interface IAction {
        void work(VersatileSmartButton versatileLifeKnightButton);
    }

    public static abstract class SmartVariableButton extends SmartButton {
        private final static Color boxColor = new Color(26, 26, 26);
        private boolean wasHovered = false;
        private long lastHoverTime = 0L;
        private final SmartVariable smartVariable;

        public SmartVariableButton(int componentId, int yPosition, SmartVariable smartVariable) {
            super(componentId, yPosition, smartVariable.getCustomDisplayString());
            this.smartVariable = smartVariable;
        }

        public SmartVariable getSmartVariable() {
            return this.smartVariable;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            super.drawButton(minecraft, mouseX, mouseY);
            if (this.smartVariable.hasDescription()) {
                if (this.hovered) {
                    if (!this.wasHovered) this.lastHoverTime = System.currentTimeMillis();
                    this.wasHovered = true;
                } else {
                    this.wasHovered = false;
                }
            }
        }

        public void tryRender(int mouseX, int mouseY) {
            long timeSinceHover = System.currentTimeMillis() - this.lastHoverTime;

            if (timeSinceHover > 750L && this.wasHovered && !Mouse.isButtonDown(0)) {
                this.smartVariable.drawDescription(mouseX, mouseY);
            }
        }

            public abstract void work();
        }

        public static class SmartBooleanButton extends SmartVariableButton {
            private final SmartBoolean lifeKnightBoolean;

            public SmartBooleanButton(int componentId, int yPosition, SmartBoolean smartBoolean, SmartButton connectedButton) {
                super(componentId, yPosition, smartBoolean);
                this.lifeKnightBoolean = smartBoolean;
                if (connectedButton != null) {
                    connectedButton.xPosition = this.xPosition + this.width + 10;
                }
            }

            @Override
            public void work() {
                this.lifeKnightBoolean.toggle();
            }

            @Override
            public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                this.displayString = this.lifeKnightBoolean.getCustomDisplayString();
                super.drawButton(minecraft, mouseX, mouseY);
            }
        }

        public static abstract class ConnectedButton extends SmartButton {
            GuiButton connectedButton;
            boolean next;
            public ConnectedButton(int componentId, GuiButton connectedButton, boolean next) {
                super(componentId,
                        next ? connectedButton.xPosition + connectedButton.width + 10 : connectedButton.xPosition - 30,
                        connectedButton.yPosition,
                        20,
                        20,
                        next ? ">" : "<");
                this.connectedButton = connectedButton;
                this.next = next;
            }

            @Override
            public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                this.xPosition = this.next ? this.connectedButton.xPosition + this.connectedButton.width + 10 :
                this.connectedButton.xPosition - 10 - this.width;
                super.drawButton(minecraft, mouseX, mouseY);
            }
        }
    }
