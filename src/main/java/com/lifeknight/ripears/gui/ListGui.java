package com.lifeknight.ripears.gui;

import com.lifeknight.ripears.gui.components.*;
import com.lifeknight.ripears.mod.Core;
import com.lifeknight.ripears.utilities.Video;
import com.lifeknight.ripears.variables.SmartList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.RED;

public class ListGui extends GuiScreen {
    private final List<ListItemButton> listItemButtons = new ArrayList<>();
    private final SmartList lifeKnightList;
    private ConfirmButton clearButton;
    private ScrollBar scrollBar;
    private SmartTextField addField, searchField;
    public ListItemButton selectedItem;
    public SmartButton addButton, removeButton;
    private String listMessage = "";
    public GuiScreen lastGui;

    public ListGui(SmartList<?> lifeKnightList) {
        this.lifeKnightList = lifeKnightList;
        this.lastGui = null;
    }

    public ListGui(SmartList<?> lifeKnightList, GuiScreen lastGui) {
        this(lifeKnightList);
        this.lastGui = lastGui;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, this.listMessage, Video.get2ndPanelCenter(), super.height / 2, 0xffffffff);
        drawVerticalLine(Video.getScaledWidth(300), 0, super.height, 0xffffffff);
        this.searchField.drawTextBoxAndName();
        this.addField.drawTextBoxAndName();
        this.addField.drawStringBelowBox();

        if (this.listItemButtons.size() != 0) {
            int j = Mouse.getDWheel() / 7;
            while (j > 0 && this.listItemButtons.get(0).yPosition + j > 10) {
                j--;
            }

            while (j < 0 && this.listItemButtons.get(this.listItemButtons.size() - 1).yPosition + 30 + j < super.height - 10) {
                j++;
            }
            for (ListItemButton listItemButton : this.listItemButtons) {
                listItemButton.yPosition += j;
            }
            int panelHeight = this.listItemButtons.size() * 30;
            this.scrollBar.yPosition = (int) ((super.height * (-this.listItemButtons.get(0).yPosition - 10) / (double) (panelHeight - super.height)) * ((super.height - scrollBar.height) / (double) super.height)) + 8;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
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
                ListGui.this.listItems();
            }
        };

        this.addField = new SmartTextField(1, Video.getScaledWidth(75), Video.getScaledHeight(85), Video.getScaledWidth(150), 20, lifeKnightList.getName()) {
            @Override
            public void handleInput() {
                this.lastInput = this.getText();
                this.setText("");
                try {
                    if (!this.lastInput.isEmpty()) {
                        ListGui.this.lifeKnightList.addElement(ListGui.this.lifeKnightList.fromFormattedString(this.lastInput));
                        this.setSubDisplayMessage("");
                        ListGui.this.listItems();
                    }
                } catch (Exception exception) {
                    this.setSubDisplayMessage(RED + "Invalid input!");
                }
            }
        };

        this.buttonList.add(this.addButton = new SmartButton("Add", 2, Video.getScaledWidth(75), Video.getScaledHeight(165), Video.getScaledWidth(150)) {
            @Override
            public void work() {
                ListGui.this.addField.handleInput();
                ListGui.this.selectedItem = null;
            }
        });

        this.buttonList.add(this.removeButton = new SmartButton("Remove", 3, Video.getScaledWidth(75), Video.getScaledHeight(230), Video.getScaledWidth(150)) {
            @Override
            public void work() {
                ListGui.this.removeSelectedButton();
            }
        });
        this.removeButton.visible = false;

        this.buttonList.add(this.clearButton = new ConfirmButton(4, Video.getScaledWidth(75), Video.getScaledHeight(295), Video.getScaledWidth(150), "Clear", RED + "Confirm") {
            @Override
            public void onConfirm() {
                ListGui.this.lifeKnightList.clear();
                ListGui.this.listItems();
            }
        });
        this.clearButton.visible = false;

        this.buttonList.add(this.scrollBar = new ScrollBar() {
            @Override
            public void onDrag(int scroll) {
                scroll = -scroll;
                int scaledScroll = (int) (scroll * (ListGui.this.listItemButtons.size() * 30) / (double) ListGui.super.height);
                while (scaledScroll > 0 && ListGui.this.listItemButtons.get(0).originalYPosition + scaledScroll > 10) {
                    scaledScroll--;
                }
                while (scaledScroll < 0 && ListGui.this.listItemButtons.get(listItemButtons.size() - 1).originalYPosition + 30 + scaledScroll < ListGui.super.height - 10) {
                    scaledScroll++;
                }
                for (ListItemButton listItemButton : ListGui.this.listItemButtons) {
                    listItemButton.yPosition = listItemButton.originalYPosition + scaledScroll;
                }
            }

            @Override
            public void onMousePress() {
                for (ListItemButton listItemButton : ListGui.this.listItemButtons) {
                    listItemButton.updateOriginalYPosition();
                }
            }
        });

        int panelHeight = this.listItemButtons.size() * 30;

        this.scrollBar.height = (int) (this.height * (this.height / (double) panelHeight));
        this.scrollBar.visible = this.scrollBar.height < this.height;

        if (this.lastGui != null) {
            this.buttonList.add(new SmartButton("Back", 5, 5, 5, 50) {
                @Override
                public void work() {
                    Core.openGui(ListGui.this.lastGui);
                }
            });
        }
        this.listItems();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof ListItemButton) {
            ((ListItemButton) button).work();
        } else if (button instanceof SmartButton) {
            ((SmartButton) button).work();
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 0xD3 && this.selectedItem != null) {
            this.removeSelectedButton();
        } else {
            this.addField.textboxKeyTyped(typedChar, keyCode);
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
        this.addField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean aButtonHasBeenSelected = false;
        for (ListItemButton listItemButton : this.listItemButtons) {
            if (listItemButton.isSelectedButton) {
                aButtonHasBeenSelected = true;
                break;
            }
        }
        this.removeButton.visible = aButtonHasBeenSelected;
    }

    protected void removeSelectedButton() {
        this.lifeKnightList.removeElement(this.lifeKnightList.getMap().inverse().get(this.selectedItem.displayString));
        this.selectedItem.visible = false;
        this.removeButton.visible = false;
        this.selectedItem = null;
        this.listItems();
    }

    private void listItems() {
        this.listItemButtons.clear();
        this.buttonList.removeIf(guiButton -> guiButton instanceof ListItemButton);

        for (Object element : this.lifeKnightList.getValue()) {
            if (this.searchField.getText().isEmpty() || ((String) Objects.requireNonNull(this.lifeKnightList.getMap().get(element))).toLowerCase().contains(this.searchField.getText().toLowerCase())) {
                ListItemButton listItemButton = new ListItemButton(this.listItemButtons.size() + 6, (String) this.lifeKnightList.getMap().get(element)) {
                    @Override
                    public void work() {
                        if (this.isSelectedButton) {
                            this.isSelectedButton = false;
                            ListGui.this.selectedItem = null;
                        } else {
                            this.isSelectedButton = true;
                            ListGui.this.selectedItem = this;
                        }
                    }
                };
                listItemButtons.add(listItemButton);
            }
        }
        this.listMessage = listItemButtons.isEmpty() ? GRAY + "No items found" : "";

        this.clearButton.visible = listItemButtons.size() > 1;

        this.buttonList.addAll(listItemButtons);
    }
}
