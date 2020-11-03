package com.lifeknight.ripears.gui;

import com.lifeknight.ripears.gui.components.ManipulableButton;
import com.lifeknight.ripears.gui.components.SmartButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.ripears.gui.Manipulable.manipulableComponents;

public class ManipulableGui extends GuiScreen {
    public static final List<ManipulableButton> manipulableButtons = new ArrayList<>();

    @Override
    public void initGui() {
        manipulableButtons.clear();
        for (Manipulable manipulable : manipulableComponents) {
            manipulableButtons.add(new ManipulableButton(manipulable));
        }
        this.buttonList.addAll(manipulableButtons);

        for (Manipulable manipulable : manipulableComponents) {
            for (Object component : manipulable.connectedComponents) {
                if (component instanceof GuiButton) {
                    this.buttonList.add((GuiButton) component);
                }
            }
        }

        this.buttonList.add(new SmartButton(this.buttonList.size(), this.width / 2 - 50, this.height - 30, 100, 20, "Reset") {
            @Override
            public void work() {
                for (GuiButton guiButton : ManipulableGui.this.buttonList) {
                    if (guiButton instanceof ManipulableButton) {
                        ((ManipulableButton) guiButton).reset();
                    }
                }
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof SmartButton) ((SmartButton) button).work();
    }
}
