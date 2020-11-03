package com.lifeknight.ripears.gui.components;

import com.lifeknight.ripears.utilities.Render;
import com.lifeknight.ripears.utilities.Video;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public abstract class ScrollBar extends GuiButton {
    public boolean dragging = false;
    public int startY = 0;
    public int originalMouseYPosition = 0;
    public int originalYPosition = 0;

    public ScrollBar() {
        super(-1, Video.getGameWidth() - 7, 0, 5, Video.getGameHeight(), "");
        this.visible = false;
    }

    public ScrollBar(int componentId, int xPosition, int yPosition, int width, int height) {
        super(componentId, xPosition, yPosition, width, height, "");
        this.visible = false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            Render.drawRectangle(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.dragging ? Color.GRAY : Color.WHITE, 255F);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(minecraft, mouseX, mouseY)) {
            this.startY = mouseY;
            this.dragging = true;
            this.originalMouseYPosition = mouseY;
            this.originalYPosition = this.yPosition;
            this.onMousePress();
            return true;
        } else {
            return false;
        }
    }

    protected abstract void onMousePress();

    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    @Override
    public void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.visible && this.dragging) {
            this.onDrag(mouseY - this.originalMouseYPosition);
        }
    }

    public abstract void onDrag(int scroll);

    public static abstract class HorizontalScrollBar extends GuiButton {
        public boolean dragging = false;
        public int startX = 0;
        public int originalMouseXPosition = 0;
        public int originalXPosition = 0;

        public HorizontalScrollBar() {
            super(-1, 0, Video.getGameHeight() - 7, Video.getGameWidth(), 5, "");
            this.visible = false;
        }

        public HorizontalScrollBar(int componentId, int xPosition, int yPosition, int width, int height) {
            super(componentId, xPosition, yPosition, width, height, "");
            this.visible = false;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible) {
                Render.drawRectangle(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.dragging ? Color.GRAY : Color.WHITE, 255F);
                this.mouseDragged(minecraft, mouseX, mouseY);
            }
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            if (super.mousePressed(minecraft, mouseX, mouseY)) {
                this.startX = mouseX;
                this.dragging = true;
                this.originalMouseXPosition = mouseX;
                this.originalXPosition = this.xPosition;
                this.onMousePress();
                return true;
            } else {
                return false;
            }
        }

        protected abstract void onMousePress();

        public void mouseReleased(int mouseX, int mouseY) {
            this.dragging = false;
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
        }

        @Override
        public void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
            if (super.visible && this.dragging) {
                this.onDrag(mouseX - this.originalMouseXPosition);
            }
        }

        public abstract void onDrag(int scroll);
    }
}
