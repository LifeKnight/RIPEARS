package com.lifeknight.ripears.gui;

import com.lifeknight.ripears.gui.components.ScrollBar;
import com.lifeknight.ripears.gui.components.SmartButton;
import com.lifeknight.ripears.gui.components.SmartSlider;
import com.lifeknight.ripears.gui.components.SmartTextField;
import com.lifeknight.ripears.mod.Core;
import com.lifeknight.ripears.utilities.Video;
import com.lifeknight.ripears.variables.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.lifeknight.ripears.mod.Core.MOD_COLOR;
import static net.minecraft.util.EnumChatFormatting.*;

public class SmartGui extends GuiScreen {
    private final String name;
    private final List<SmartVariable> guiVariables = new ArrayList<>();
    private final List<SmartTextField> smartTextFields = new ArrayList<>();
    private int panelHeight = 0;
    private final List<GuiButton> displayedButtons = new ArrayList<>();
    private final List<SmartButton> extraButtons = new ArrayList<>();
    private ScrollBar scrollBar;
    private SmartTextField searchField;
    private String panelMessage = "";
    private final List<String> groupNames = new ArrayList<>(Collections.singletonList("All"));
    public String selectedGroup = "All";

    public SmartGui(String name, List<SmartVariable> smartVariables) {
        this.name = name;
        for (SmartVariable smartVariable : smartVariables) {
            if (smartVariable.showInLifeKnightGui()) {
                this.guiVariables.add(smartVariable);
                if (!this.groupNames.contains(smartVariable.getGroup())) {
                    this.groupNames.add(smartVariable.getGroup());
                }
            }
        }
    }

    public SmartGui(String name, SmartVariable... smartVariables) {
        this(name, Arrays.asList(smartVariables));
    }

    public SmartGui(String name, List<SmartVariable> smartVariables, List<SmartButton> extraButtons) {
        this(name, smartVariables);
        this.extraButtons.addAll(extraButtons);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawCenteredString(this.fontRendererObj, MOD_COLOR + this.name, Video.getScaledWidth(150), Video.getScaledHeight(60), 0xffffffff);
        super.drawCenteredString(this.fontRendererObj, this.panelMessage, Video.get2ndPanelCenter(), this.height / 2, 0xffffffff);
        super.drawVerticalLine(Video.getScaledWidth(300), 0, this.height, 0xffffffff);
        this.searchField.drawTextBoxAndName();

        for (int i = 0; i < this.groupNames.size() - 1; i++) {
            drawHorizontalLine(Video.getScaledWidth(100), Video.getScaledWidth(200), Video.getScaledHeight(150) + 25 * i + 22, 0xffffffff);
        }

        if (this.displayedButtons.size() != 0) {
            int j = Mouse.getDWheel() / 7;
            if (((j > 0) && this.scrollBar.yPosition > 0) || ((j < 0) && this.scrollBar.yPosition + this.scrollBar.height < this.height)) {
                while (j > 0 && this.displayedButtons.get(0).yPosition + j > 10) {
                    j--;
                }

                while (j < 0 && this.displayedButtons.get(this.displayedButtons.size() - 1).yPosition + 20 + j < this.height - 10) {
                    j++;
                }

                for (GuiButton guiButton : this.displayedButtons) {
                    guiButton.yPosition += j;
                    if (guiButton instanceof SmartButton) {
                        ((SmartButton) guiButton).updateOriginalYPosition();
                    } else if (guiButton instanceof SmartSlider) {
                        ((SmartSlider) guiButton).updateOriginalYPosition();
                    }
                }
                for (GuiButton guiButton : SmartGui.this.buttonList) {
                    if (guiButton instanceof SmartButton && (guiButton.displayString.equals(">") || guiButton.displayString.equals("<"))) {
                        guiButton.yPosition += j;
                        ((SmartButton) guiButton).updateOriginalYPosition();
                    }
                }
                for (SmartTextField smartTextField : this.smartTextFields) {
                    smartTextField.yPosition += j;
                    smartTextField.updateOriginalYPosition();
                }
            }
            this.scrollBar.yPosition = (int) (((-(this.displayedButtons.get(0).yPosition - 10)) / (this.panelHeight - (double) this.height) * (this.height - this.scrollBar.height)));
        } else {
            this.scrollBar.visible = false;
        }

        for (SmartTextField smartTextField : this.smartTextFields) {
            if (((this.selectedGroup.equals("All") || this.selectedGroup.equals(smartTextField.smartVariable.getGroup())) && (this.searchField.getText().isEmpty() || smartTextField.smartVariable.getName().toLowerCase().contains(this.searchField.getText().toLowerCase())))) {
                smartTextField.drawTextBoxAndName();
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (GuiButton guiButton : this.buttonList) {
            if (guiButton instanceof SmartButton.SmartVariableButton && guiButton.isMouseOver()) {
                ((SmartButton.SmartVariableButton) guiButton).tryRender(mouseX, mouseY);
            } else if (guiButton instanceof SmartSlider && guiButton.isMouseOver()) {
                ((SmartSlider) guiButton).tryRender(mouseX, mouseY);
            }
        }

        for (SmartTextField smartTextField : this.smartTextFields) {
            if (smartTextField.isMouseOver(mouseX, mouseY)) smartTextField.tryRender(mouseX, mouseY);
        }

    }

    public void initGui() {
        this.searchField = new SmartTextField(0, Video.getScaledWidth(75), this.height - 40, Video.getScaledWidth(150), 20, "Search") {
            @Override
            public boolean textboxKeyTyped(char characterTyped, int keyCode) {
                if (super.textboxKeyTyped(characterTyped, keyCode)) {
                    this.handleInput();
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void handleInput() {
                SmartGui.this.listComponents();
            }
        };
        this.listComponents();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof SmartButton) {
            ((SmartButton) button).work();
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode != 1) {
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            for (SmartTextField smartTextField : this.smartTextFields) {
                smartTextField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        for (SmartTextField smartTextField : this.smartTextFields) {
            smartTextField.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void listComponents() {
        this.smartTextFields.clear();
        this.buttonList.clear();
        this.displayedButtons.clear();
        boolean noButtons = true;
        int componentId = 0;
        int nextYPosition = 5;

        for (SmartVariable smartVariable : this.guiVariables) {
            if (((this.selectedGroup.equals("All") || this.selectedGroup.equals(smartVariable.getGroup())) && (this.searchField.getText().isEmpty() || smartVariable.getName().toLowerCase().contains(this.searchField.getText().toLowerCase()) || smartVariable.getCustomDisplayString().toLowerCase().contains(this.searchField.getText().toLowerCase())))) {
                noButtons = false;
                if (smartVariable instanceof SmartBoolean) {
                    if (((SmartBoolean) smartVariable).hasList()) {
                        SmartButton open;
                        this.buttonList.add(open = new SmartButton(componentId,
                                Video.get2ndPanelCenter() + 110,
                                nextYPosition,
                                20,
                                20, ">") {
                            @Override
                            public void work() {
                                if (((SmartBoolean) smartVariable).getList() instanceof SmartList<?>) {
                                    Core.openGui(new ListGui((SmartList<?>) ((SmartBoolean) smartVariable).getList(), SmartGui.this));
                                } else {
                                    Core.openGui(new SmartObjectListGui((SmartObjectList<?>) ((SmartBoolean) smartVariable).getList(), SmartGui.this));
                                }
                            }
                        });
                        this.displayedButtons.add(new SmartButton.SmartBooleanButton(componentId, nextYPosition, (SmartBoolean) smartVariable, open));
                    } else {
                        this.displayedButtons.add(new SmartButton.SmartBooleanButton(componentId, nextYPosition, (SmartBoolean) smartVariable, null));
                    }
                    nextYPosition += 30;
                    componentId++;
                } else if (smartVariable instanceof SmartNumber) {
                    if (!(smartVariable instanceof SmartNumber.SmartLong)) {
                        this.displayedButtons.add(new SmartSlider(componentId, nextYPosition, false, (SmartNumber) smartVariable));
                        nextYPosition += 30;
                        componentId++;
                    } else {
                        nextYPosition += 15;
                        int i = this.smartTextFields.size();
                        this.smartTextFields.add(new SmartTextField(componentId, nextYPosition, smartVariable) {
                            @Override
                            public void handleInput() {
                                if (!this.getText().isEmpty()) {
                                    try {
                                        this.lastInput = this.getText();
                                        this.setText("");
                                        long l = Long.parseLong(this.lastInput);
                                        if (l >= (Long) ((SmartNumber.SmartLong) this.smartVariable).getMinimumValue() && l <= (Long) ((SmartNumber.SmartLong) this.smartVariable).getMaximumValue()) {
                                            ((SmartNumber.SmartLong) this.smartVariable).setValue(l);
                                        } else {
                                            throw new Exception();
                                        }
                                        this.name = this.smartVariable.getCustomDisplayString();
                                    } catch (Exception exception) {
                                        this.name = RED + "Invalid input!";
                                    }
                                }
                            }
                        });
                        this.buttonList.add(new SmartButton(componentId + 1, Video.get2ndPanelCenter() + 110,
                                nextYPosition,
                                20,
                                20, ">") {
                            @Override
                            public void work() {
                                SmartGui.this.smartTextFields.get(i).handleInput();
                            }
                        });
                        nextYPosition += 30;
                        componentId += 2;
                    }
                } else if (smartVariable instanceof SmartString) {
                    int i = this.smartTextFields.size();
                    nextYPosition += 15;
                    this.smartTextFields.add(new SmartTextField(componentId, nextYPosition, smartVariable) {
                        @Override
                        public void handleInput() {
                            if (!this.getText().isEmpty()) {
                                this.lastInput = this.getText();
                                this.setText("");
                                ((SmartString) this.smartVariable).setValue(this.lastInput);
                                this.name = this.smartVariable.getCustomDisplayString();
                            }
                        }
                    });
                    this.buttonList.add(new SmartButton(componentId, Video.get2ndPanelCenter() + 110,
                            nextYPosition,
                            20,
                            20, ">") {
                        @Override
                        public void work() {
                            SmartGui.this.smartTextFields.get(i).handleInput();
                        }
                    });
                    nextYPosition += 30;
                    componentId += 2;
                } else if (smartVariable instanceof SmartCycle) {
                    SmartButton cycleButton;
                    this.displayedButtons.add(cycleButton = new SmartButton.SmartVariableButton(componentId, nextYPosition, smartVariable) {
                        @Override
                        public void work() {
                            ((SmartCycle) smartVariable).next();
                        }

                        @Override
                        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                            this.displayString = smartVariable.getCustomDisplayString();
                            this.width = Math.max(200, Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) + 20);
                            this.xPosition = Video.get2ndPanelCenter() - this.width / 2;
                            super.drawButton(minecraft, mouseX, mouseY);
                        }
                    });
                    this.buttonList.add(new SmartButton.ConnectedButton(componentId, cycleButton, false) {
                        @Override
                        public void work() {
                            ((SmartCycle) smartVariable).previous();
                        }
                    });
                    this.buttonList.add(new SmartButton.ConnectedButton(componentId, cycleButton, true) {
                        @Override
                        public void work() {
                            ((SmartCycle) smartVariable).next();
                        }
                    });
                    nextYPosition += 30;
                    componentId++;
                } else if (smartVariable instanceof SmartList && ((SmartList<?>) smartVariable).isIndependent()) {
                    this.displayedButtons.add(new SmartButton.SmartVariableButton(componentId, nextYPosition, smartVariable) {
                        @Override
                        public void work() {
                            Core.openGui(new ListGui((SmartList<?>) smartVariable, SmartGui.this));
                        }
                    });
                    nextYPosition += 30;
                    componentId++;
                } else if (smartVariable instanceof SmartObject) {
                    this.displayedButtons.add(new SmartButton(componentId, nextYPosition, smartVariable.getCustomDisplayString()) {
                        @Override
                        public void work() {
                            Core.openGui(new SmartObjectGui((SmartObject) smartVariable, SmartGui.this));
                        }
                    });
                    nextYPosition += 30;
                    componentId++;
                } else if (smartVariable instanceof SmartObjectList && ((SmartObjectList<?>) smartVariable).isIndependent()) {
                    this.displayedButtons.add(new SmartButton(componentId, nextYPosition, smartVariable.getCustomDisplayString()) {
                        @Override
                        public void work() {
                            Core.openGui(new SmartObjectListGui((SmartObjectList<?>) smartVariable, SmartGui.this));
                        }
                    });
                    nextYPosition += 30;
                    componentId++;
                }
            }
        }

        for (SmartButton smartButton : this.extraButtons) {
            if ((this.selectedGroup.equals("All") && (this.searchField.getText().isEmpty() || smartButton.displayString.toLowerCase().contains(this.searchField.getText().toLowerCase())))) {
                noButtons = false;
                smartButton.xPosition = Video.get2ndPanelCenter() - 100;
                smartButton.yPosition = nextYPosition;
                smartButton.id = componentId;

                this.displayedButtons.add(smartButton);
                nextYPosition += 30;
                componentId++;
            }
        }

        this.panelHeight = nextYPosition == 5 ? 0 : nextYPosition;

        this.buttonList.addAll(this.displayedButtons);

        for (int i = 0; i < this.groupNames.size(); i++) {
            int finalI = i;
            this.buttonList.add(new SmartButton(this.buttonList.size() - 1, Video.getScaledWidth(100), Video.getScaledHeight(150) + 25 * i, Video.getScaledWidth(100), 20, this.groupNames.get(i)) {
                final String name = SmartGui.this.groupNames.get(finalI);

                @Override
                public void work() {
                    SmartGui.this.selectedGroup = this.name;
                    SmartGui.this.listComponents();
                }

                @Override
                public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                    if (this.visible) {
                        FontRenderer fontrenderer = minecraft.fontRendererObj;
                        this.displayString = (SmartGui.this.selectedGroup.equals(this.name) ? MOD_COLOR + "" + BOLD : "") + this.name;
                        this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 0xffffffff);
                    }
                }
            });
        }
        this.panelMessage = noButtons ? GRAY + "No settings found" : "";
        this.buttonList.add(this.scrollBar = new ScrollBar() {
            @Override
            public void onDrag(int scroll) {
                scroll = -scroll;

                int scaledScroll = (int) (scroll * SmartGui.this.panelHeight / (double) SmartGui.this.height);

                while (scaledScroll > 0 && SmartGui.this.getFirstComponentOriginalYPosition() + scaledScroll > 10) {
                    scaledScroll--;
                }

                while (scaledScroll < 0 && SmartGui.this.getLastComponentOriginalYPosition() + 20 + scaledScroll < SmartGui.this.height - 10) {
                    scaledScroll++;
                }


                for (GuiButton guiButton : SmartGui.this.displayedButtons) {
                    if (guiButton instanceof SmartButton) {
                        guiButton.yPosition = ((SmartButton) guiButton).originalYPosition + scaledScroll;
                    } else if (guiButton instanceof SmartSlider) {
                        guiButton.yPosition = ((SmartSlider) guiButton).originalYPosition + scaledScroll;
                    }
                }

                for (GuiButton guiButton : SmartGui.this.buttonList) {
                    if (guiButton instanceof SmartButton && (guiButton.displayString.equals(">") || guiButton.displayString.equals("<"))) {
                        guiButton.yPosition = ((SmartButton) guiButton).originalYPosition + scaledScroll;
                    }
                }
                for (SmartTextField smartTextField : SmartGui.this.smartTextFields) {
                    smartTextField.yPosition = smartTextField.originalYPosition + scaledScroll;
                }
            }

            @Override
            public void onMousePress() {
                for (GuiButton guiButton : SmartGui.this.displayedButtons) {
                    if (guiButton instanceof SmartButton) {
                        ((SmartButton) guiButton).updateOriginalYPosition();
                    } else if (guiButton instanceof SmartSlider) {
                        ((SmartSlider) guiButton).updateOriginalYPosition();
                    }
                }
                for (GuiButton guiButton : SmartGui.this.buttonList) {
                    if (guiButton instanceof SmartButton && (guiButton.displayString.equals(">") || guiButton.displayString.equals("<"))) {
                        ((SmartButton) guiButton).updateOriginalYPosition();
                    }
                }
                for (SmartTextField smartTextField : SmartGui.this.smartTextFields) {
                    smartTextField.updateOriginalYPosition();
                }
            }
        });

        this.scrollBar.height = (int) (this.height * (this.height / (double) this.panelHeight));
        this.scrollBar.visible = this.scrollBar.height < this.height;
    }

    private int getFirstComponentOriginalYPosition() {
        Object firstComponent = null;
        int yPosition = 0;

        for (GuiButton guiButton : this.displayedButtons) {
            if (firstComponent == null || (guiButton.yPosition < yPosition)) {
                firstComponent = guiButton;
                yPosition = guiButton.yPosition;
            }
        }

        for (SmartTextField smartTextField : this.smartTextFields) {
            if (firstComponent == null || (smartTextField.yPosition < yPosition)) {
                firstComponent = smartTextField;
                yPosition = smartTextField.yPosition;
            }
        }

        if (firstComponent instanceof SmartButton) {
            return ((SmartButton) firstComponent).originalYPosition;
        } else if (firstComponent instanceof SmartSlider) {
            return ((SmartSlider) firstComponent).originalYPosition;
        } else if (firstComponent instanceof SmartTextField) {
            return ((SmartTextField) firstComponent).originalYPosition;
        }
        return 0;
    }

    private int getLastComponentOriginalYPosition() {
        Object lastComponent = null;
        int yPosition = 0;

        for (GuiButton guiButton : this.displayedButtons) {
            if (lastComponent == null || (guiButton.yPosition > yPosition)) {
                lastComponent = guiButton;
                yPosition = guiButton.yPosition;
            }
        }

        for (SmartTextField smartTextField : this.smartTextFields) {
            if (lastComponent == null || (smartTextField.yPosition > yPosition)) {
                lastComponent = smartTextField;
                yPosition = smartTextField.yPosition;
            }
        }

        if (lastComponent instanceof SmartButton) {
            return ((SmartButton) lastComponent).originalYPosition;
        } else if (lastComponent instanceof SmartSlider) {
            return ((SmartSlider) lastComponent).originalYPosition;
        } else if (lastComponent instanceof SmartTextField) {
            return ((SmartTextField) lastComponent).originalYPosition;
        }
        return 0;
    }
}