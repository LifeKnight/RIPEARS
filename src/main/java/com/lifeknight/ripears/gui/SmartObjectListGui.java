package com.lifeknight.ripears.gui;

import com.lifeknight.ripears.gui.components.*;
import com.lifeknight.ripears.mod.Core;
import com.lifeknight.ripears.utilities.Video;
import com.lifeknight.ripears.variables.SmartObject;
import com.lifeknight.ripears.variables.SmartObjectList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.RED;

public class SmartObjectListGui extends GuiScreen {
    private final List<ListItemButton> listItemButtons = new ArrayList<>();
    private final List<SmartButton> openButtons = new ArrayList<>();
    private final SmartObjectList lifeKnightObjectList;
    private ConfirmButton clearButton;
    private ScrollBar scrollBar;
    private SmartTextField searchField;
    public ListItemButton selectedItem;
    public SmartButton addButton, removeButton;
    private String searchInput = "", listMessage = "";
    public GuiScreen lastGui;

    public SmartObjectListGui(SmartObjectList lifeKnightObjectList) {
        this.lifeKnightObjectList = lifeKnightObjectList;
        this.lastGui = null;
    }

    public SmartObjectListGui(SmartObjectList lifeKnightObjectList, GuiScreen lastGui) {
        this(lifeKnightObjectList);
        this.lastGui = lastGui;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.listMessage, Video.get2ndPanelCenter(), this.height / 2, 0xffffffff);
        this.drawCenteredString(this.fontRendererObj, this.lifeKnightObjectList.getCustomDisplayString(), Video.getScaledWidth(150), Video.getScaledHeight(60), 0xffffffff);
        this.drawVerticalLine(Video.getScaledWidth(300), 0, this.height, 0xffffffff);
        this.searchField.drawTextBoxAndName();

        if (this.listItemButtons.size() != 0) {
            int j = Mouse.getDWheel() / 7;
            while (j > 0 && this.listItemButtons.get(0).yPosition + j > 10) {
                j--;
            }

            while (j < 0 && this.listItemButtons.get(this.listItemButtons.size() - 1).yPosition + 30 + j < this.height - 10) {
                j++;
            }
            for (ListItemButton listItemButton : this.listItemButtons) {
                listItemButton.yPosition += j;
            }
            int panelHeight = this.listItemButtons.size() * 30;
            this.scrollBar.yPosition = (int) ((this.height * (-this.listItemButtons.get(0).yPosition - 10) / (double) (panelHeight - this.height)) * ((this.height - scrollBar.height) / (double) this.height)) + 8;
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
                SmartObjectListGui.this.searchInput = this.getText();
                SmartObjectListGui.this.listItems();
            }
        };

        this.buttonList.add(this.addButton = new SmartButton("Add", 2, Video.getScaledWidth(75), Video.getScaledHeight(165), Video.getScaledWidth(150)) {
            @Override
            public void work() {
                SmartObjectListGui.this.lifeKnightObjectList.addElement(SmartObjectListGui.this.lifeKnightObjectList.getDefault());
                SmartObjectListGui.this.selectedItem = null;
            }
        });

        this.buttonList.add(this.removeButton = new SmartButton("Remove", 3, Video.getScaledWidth(75), Video.getScaledHeight(230), Video.getScaledWidth(150)) {
            @Override
            public void work() {
                SmartObjectListGui.this.removeSelectedButton();
            }
        });
        this.removeButton.visible = false;

        this.buttonList.add(this.clearButton = new ConfirmButton(4, Video.getScaledWidth(75), Video.getScaledHeight(295), Video.getScaledWidth(150), "Clear", RED + "Confirm") {
            @Override
            public void onConfirm() {
                SmartObjectListGui.this.lifeKnightObjectList.clear();
                SmartObjectListGui.this.listItems();
            }
        });
        this.clearButton.visible = false;

        this.buttonList.add(this.scrollBar = new ScrollBar() {
            @Override
            public void onDrag(int scroll) {
                scroll = -scroll;
                int scaledScroll = (int) (scroll * (SmartObjectListGui.this.listItemButtons.size() * 30) / (double) SmartObjectListGui.this.height);
                while (scaledScroll > 0 && SmartObjectListGui.this.listItemButtons.get(0).originalYPosition + scaledScroll > 10) {
                    scaledScroll--;
                }
                while (scaledScroll < 0 && SmartObjectListGui.this.listItemButtons.get(listItemButtons.size() - 1).originalYPosition + 30 + scaledScroll < SmartObjectListGui.this.height - 10) {
                    scaledScroll++;
                }
                for (ListItemButton listItemButton : SmartObjectListGui.this.listItemButtons) {
                    listItemButton.yPosition = listItemButton.originalYPosition + scaledScroll;
                }
            }

            @Override
            public void onMousePress() {
                for (ListItemButton listItemButton : SmartObjectListGui.this.listItemButtons) {
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
                    Core.openGui(SmartObjectListGui.this.lastGui);
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
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            super.keyTyped(typedChar, keyCode);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
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
        this.lifeKnightObjectList.removeByDisplayString(this.selectedItem.displayString);
        this.selectedItem.visible = false;
        this.removeButton.visible = false;
        this.selectedItem = null;
        this.listItems();
    }

    private void listItems() {
        this.listItemButtons.clear();
        this.openButtons.clear();
        this.buttonList.removeIf(guiButton -> guiButton instanceof ListItemButton || guiButton.displayString.equals(">"));

        for (Object object : this.lifeKnightObjectList.getValue()) {
            SmartObject element = (SmartObject) object;
            if (this.searchInput.isEmpty() || element.isSearchResult(this.searchInput)) {
                ListItemButton listItemButton = new ListItemButton(this.listItemButtons.size() + 6, element.getCustomDisplayString()) {
                    @Override
                    public void work() {
                        if (this.isSelectedButton) {
                            this.isSelectedButton = false;
                            SmartObjectListGui.this.selectedItem = null;
                        } else {
                            this.isSelectedButton = true;
                            SmartObjectListGui.this.selectedItem = this;
                        }
                    }
                };
                SmartObjectListGui.this.listItemButtons.add(listItemButton);
                SmartButton smartButton = new SmartButton(SmartObjectListGui.this.listItemButtons.size() + 1000, listItemButton.xPosition + listItemButton.width + 10,
                        10 + (SmartObjectListGui.this.listItemButtons.size() - 1) * 30,
                        20,
                        20, ">") {
                    @Override
                    public void work() {
                        Core.openGui(new SmartObjectGui(element, SmartObjectListGui.this));
                    }
                };
                SmartObjectListGui.this.openButtons.add(smartButton);
            }
        }
        this.listMessage = this.listItemButtons.isEmpty() ? GRAY + "No items found" : "";

        this.clearButton.visible = this.listItemButtons.size() > 1;

        this.buttonList.addAll(this.listItemButtons);

        this.buttonList.addAll(this.openButtons);
    }
}
