package com.lifeknight.ripears.gui.components;

import com.lifeknight.ripears.utilities.Render;
import com.lifeknight.ripears.utilities.Video;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public abstract class ListItemButton extends GuiButton {
    public boolean isSelectedButton = false;
    public int originalYPosition;

    public ListItemButton(int componentId, String element) {
        super(componentId, Video.get2ndPanelCenter() - 100,
                (componentId - 6) * 30 + 10,
                200,
                20, element);
        int j;
        if ((j = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) + 30) > this.width) {
            this.width = j;
            this.xPosition = Video.get2ndPanelCenter() - this.width / 2;
        }
        this.originalYPosition = this.yPosition;
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(minecraft, mouseX, mouseY)) {
            this.isSelectedButton = true;
            this.work();
            return true;
        } else {
            this.isSelectedButton = false;
            return false;
        }
    }

    public void updateOriginalYPosition() {
        this.originalYPosition = this.yPosition;
    }

    public abstract void work();

    public void drawButton(Minecraft minecraft, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = minecraft.fontRendererObj;
            Render.drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, this.isSelectedButton ? Color.RED : Color.WHITE, 255F, 1);
            this.mouseDragged(minecraft, mouseX, mouseY);
            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + super.width / 2, this.yPosition + (super.height - 8) / 2, 0xffffffff);
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }
}
