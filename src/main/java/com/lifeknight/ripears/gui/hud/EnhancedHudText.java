package com.lifeknight.ripears.gui.hud;

import com.lifeknight.ripears.gui.Manipulable;
import com.lifeknight.ripears.gui.components.SmartButton;
import com.lifeknight.ripears.mod.Core;
import com.lifeknight.ripears.utilities.*;
import com.lifeknight.ripears.variables.SmartBoolean;
import com.lifeknight.ripears.variables.SmartCycle;
import com.lifeknight.ripears.variables.SmartString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.RED;

public abstract class EnhancedHudText extends Manipulable {
    public static final List<EnhancedHudText> textToRender = new ArrayList<>();
    private static final Color textBoxColor = new Color(26, 26, 26);
    private static final Color buttonHoveredColor = new Color(51, 153, 255);
    private static final Color defaultButtonColor = new Color(71, 71, 71);
    private final String prefix;
    private final SmartBoolean hudTextVisible;
    private final SmartCycle separator;
    private final SmartCycle prefixColor;
    private final SmartCycle contentColor;
    private final SmartCycle alignment;
    private final SmartString lastString;
    public final List<SmartButton> connectedButtons = new ArrayList<>();

    public EnhancedHudText(String name, int defaultX, int defaultY, String prefix) {
        super(name + " HUD Text", defaultX, defaultY);
        this.prefix = prefix;
        this.hudTextVisible = new SmartBoolean("Visible", name + " HUD Text", true, null);
        this.separator = new SmartCycle("Separator", name + " HUD Text", Arrays.asList(">", ":", "|", "-", "[]"), null);
        this.prefixColor = new SmartCycle("Prefix Color", name + " HUD Text", Arrays.asList(
                "Red",
                "Gold",
                "Yellow",
                "Green",
                "Aqua",
                "Blue",
                "Light Purple",
                "Dark Red",
                "Dark Green",
                "Dark Aqua",
                "Dark Blue",
                "Dark Purple",
                "White",
                "Gray",
                "Dark Gray",
                "Black",
                "Chroma"
        ), 12, null) {
            @Override
            public String getCustomDisplayString() {
                return "Prefix Color: " + (this.getValue() == 16 ? Miscellaneous.CHROMA_STRING : Miscellaneous.getEnumChatFormatting(this.getCurrentValueString()) + this.getCurrentValueString());
            }
        };
        this.contentColor = new SmartCycle("Content Color", name + " HUD Text", Arrays.asList(
                "Red",
                "Gold",
                "Yellow",
                "Green",
                "Aqua",
                "Blue",
                "Light Purple",
                "Dark Red",
                "Dark Green",
                "Dark Aqua",
                "Dark Blue",
                "Dark Purple",
                "White",
                "Gray",
                "Dark Gray",
                "Black",
                "Chroma"
        ), 12, null) {
            @Override
            public String getCustomDisplayString() {
                return "Content Color: " + (this.getValue() == 16 ? Miscellaneous.CHROMA_STRING : Miscellaneous.getEnumChatFormatting(this.getCurrentValueString()) + this.getCurrentValueString());
            }
        };
        this.alignment = new SmartCycle("Alignment", name + " HUD Text", Arrays.asList(
                "Left",
                "Center",
                "Right"
        ), null);
        this.lastString = new SmartString("Last String", name + " HUD Text", this.getTextToDisplay(), null);
        this.lastString.setShowInLifeKnightGui(false);
        this.hudTextVisible.setShowInLifeKnightGui(false);
        this.separator.setShowInLifeKnightGui(false);
        this.prefixColor.setShowInLifeKnightGui(false);
        this.contentColor.setShowInLifeKnightGui(false);
        this.alignment.setShowInLifeKnightGui(false);

        this.hudTextVisible.setiCustomDisplayString(objects -> this.hudTextVisible.getValue() ? GREEN + "Shown" : RED + "Hidden");
        this.separator.setiCustomDisplayString(objects -> "Separator: " + this.separator.getCurrentValueString());
        this.alignment.setiCustomDisplayString(objects -> "Alignment: " + this.alignment.getCurrentValueString());

        this.connectedButtons.add(new SmartButton(this.hudTextVisible.getCustomDisplayString(), 0, 0, 0, 100) {
            @Override
            public void work() {
                EnhancedHudText.this.hudTextVisible.toggle();
            }

            @Override
            public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                this.displayString = EnhancedHudText.this.hudTextVisible.getCustomDisplayString();
                if (this.visible) {
                    FontRenderer fontRenderer = minecraft.fontRendererObj;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                    Render.drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Color.WHITE, 255F);
                    Render.drawRectangle(this.xPosition + 1, this.yPosition + 1, this.xPosition + 1 + this.width - 2, this.yPosition + 1 + this.height - 2, this.hovered ? buttonHoveredColor : defaultButtonColor, 120F);

                    this.mouseDragged(minecraft, mouseX, mouseY);
                    int j = 14737632;
                    if (this.packedFGColour != 0) {
                        j = this.packedFGColour;
                    } else if (!this.enabled) {
                        j = 10526880;
                    } else if (this.hovered) {
                        j = 16777120;
                    }

                    this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
                }
            }
        });

        if (!prefix.isEmpty()) {
            this.connectedButtons.add(new SmartButton(this.separator.getCustomDisplayString(), 0, 0, 0, 100) {
                @Override
                public void work() {
                    EnhancedHudText.this.separator.next();
                }

                @Override
                public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                    this.displayString = EnhancedHudText.this.separator.getCustomDisplayString();
                    if (this.visible) {
                        FontRenderer fontRenderer = minecraft.fontRendererObj;
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                        Render.drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Color.WHITE, 255F);
                        Render.drawRectangle(this.xPosition + 1, this.yPosition + 1, this.xPosition + 1 + this.width - 2, this.yPosition + 1 + this.height - 2, this.hovered ? buttonHoveredColor : defaultButtonColor, 120F);

                        this.mouseDragged(minecraft, mouseX, mouseY);
                        int j = 14737632;
                        if (this.packedFGColour != 0) {
                            j = this.packedFGColour;
                        } else if (!this.enabled) {
                            j = 10526880;
                        } else if (this.hovered) {
                            j = 16777120;
                        }

                        this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
                    }
                }
            });
            this.connectedButtons.add(new SmartButton(this.prefixColor.getCustomDisplayString(), 0, 0, 0, 100) {
                @Override
                public void work() {
                    EnhancedHudText.this.prefixColor.next();
                }

                @Override
                public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                    this.displayString = EnhancedHudText.this.prefixColor.getCustomDisplayString();
                    int i;
                    if (!((i = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) + 15) < 100)) {
                        this.width = i;
                    } else {
                        this.width = 100;
                    }
                    if (this.visible) {
                        FontRenderer fontRenderer = minecraft.fontRendererObj;
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                        Render.drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Color.WHITE, 255F);
                        Render.drawRectangle(this.xPosition + 1, this.yPosition + 1, this.xPosition + 1 + this.width - 2, this.yPosition + 1 + this.height - 2, this.hovered ? buttonHoveredColor : defaultButtonColor, 120F);

                        this.mouseDragged(minecraft, mouseX, mouseY);
                        int j = 14737632;
                        if (this.packedFGColour != 0) {
                            j = this.packedFGColour;
                        } else if (!this.enabled) {
                            j = 10526880;
                        } else if (this.hovered) {
                            j = 16777120;
                        }

                        this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
                    }
                }
            });
        }

        this.connectedButtons.add(new SmartButton(this.contentColor.getCustomDisplayString(), 0, 0, 0, 100) {
            @Override
            public void work() {
                EnhancedHudText.this.contentColor.next();
            }

            @Override
            public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                this.displayString = EnhancedHudText.this.contentColor.getCustomDisplayString();
                int i;
                if (!((i = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.displayString) + 15) < 100)) {
                    this.width = i;
                } else {
                    this.width = 100;
                }
                if (this.visible) {
                    FontRenderer fontRenderer = minecraft.fontRendererObj;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                    Render.drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Color.WHITE, 255F);
                    Render.drawRectangle(this.xPosition + 1, this.yPosition + 1, this.xPosition + 1 + this.width - 2, this.yPosition + 1 + this.height - 2, this.hovered ? buttonHoveredColor : defaultButtonColor, 120F);

                    this.mouseDragged(minecraft, mouseX, mouseY);
                    int j = 14737632;
                    if (this.packedFGColour != 0) {
                        j = this.packedFGColour;
                    } else if (!this.enabled) {
                        j = 10526880;
                    } else if (this.hovered) {
                        j = 16777120;
                    }

                    this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
                }
            }
        });

        this.connectedButtons.add(new SmartButton(this.alignment.getCustomDisplayString(), 0, 0, 0, 100) {
            @Override
            public void work() {
                EnhancedHudText.this.alignment.next();
            }

            @Override
            public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
                this.displayString = EnhancedHudText.this.alignment.getCustomDisplayString();
                if (this.visible) {
                    FontRenderer fontRenderer = minecraft.fontRendererObj;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

                    Render.drawEmptyBox(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Color.WHITE, 255F);
                    Render.drawRectangle(this.xPosition + 1, this.yPosition + 1, this.xPosition + 1 + this.width - 2, this.yPosition + 1 + this.height - 2, this.hovered ? buttonHoveredColor : defaultButtonColor, 120F);

                    this.mouseDragged(minecraft, mouseX, mouseY);
                    int j = 14737632;
                    if (this.packedFGColour != 0) {
                        j = this.packedFGColour;
                    } else if (!this.enabled) {
                        j = 10526880;
                    } else if (this.hovered) {
                        j = 16777120;
                    }

                    this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
                }
            }
        });

        super.connectedComponents.addAll(this.connectedButtons);
        textToRender.add(this);
    }

    public EnhancedHudText(String name, int defaultX, int defaultY) {
        this(name, defaultX, defaultY, "");
    }

    public EnhancedHudText(String name, String prefix) {
        this(name, 0, 0, prefix);
    }

    public EnhancedHudText(String name) {
        this(name, 0, 0);
    }

    public abstract String getTextToDisplay();

    public String getDisplayText() {
        if (this.prefix.isEmpty()) {
            return Miscellaneous.getEnumChatFormatting(this.contentColor.getCurrentValueString()) + this.getTextToDisplay();
        } else {
            switch (this.separator.getValue()) {
                case 1:
                    return Miscellaneous.getEnumChatFormatting(this.prefixColor.getCurrentValueString()) + this.prefix + ": " + Miscellaneous.getEnumChatFormatting(this.contentColor.getCurrentValueString()) + this.getTextToDisplay();
                case 4:
                    return Miscellaneous.getEnumChatFormatting(this.prefixColor.getCurrentValueString()) + "[" + this.prefix + "] " + Miscellaneous.getEnumChatFormatting(this.contentColor.getCurrentValueString()) + this.getTextToDisplay();
            }
            return Miscellaneous.getEnumChatFormatting(this.prefixColor.getCurrentValueString()) + this.prefix + " " + this.separator.getCurrentValueString() + " " + Miscellaneous.getEnumChatFormatting(this.contentColor.getCurrentValueString()) + this.getTextToDisplay();
        }
    }

    private String getCleanDisplayText() {
        return EnumChatFormatting.getTextWithoutFormattingCodes(this.getDisplayText());
    }

    public abstract boolean isVisible();

    public void doRender() {
        if (Minecraft.getMinecraft().inGameHasFocus && this.hudTextVisible.getValue() && this.isVisible()) {
            float scale = this.getScale();
            float xPosition = this.getXCoordinate();
            float yPosition = this.getRawYPosition();
            float height = this.getHeight();
            float width = this.getWidth();
            if (Core.hudTextBox.getValue()) {
                Render.drawRectangle(xPosition, yPosition, xPosition + width, yPosition + height + 1, 1.0F, textBoxColor, 255F * Core.hudTextBoxOpacity.getValue());
            }
            this.drawText(xPosition, yPosition, width, height, scale);
        }
    }

    private void drawText(float xPosition, float yPosition, float width, float height, float scale) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        if (Core.hudTextBox.getValue()) {
            float offset = (height - (fontRenderer.FONT_HEIGHT + 0.3F) * scale);
            yPosition += offset;
        }

        boolean dropShadow = Core.hudTextShadow.getValue();
        boolean contentChroma = this.contentColor.getValue() == 16;

        float chromaSpeed = 1000F / Core.chromaSpeed.getValue();

        EnumChatFormatting prefixColor = Miscellaneous.getEnumChatFormatting(this.prefixColor.getCurrentValueString());
        EnumChatFormatting contentColor = Miscellaneous.getEnumChatFormatting(this.contentColor.getCurrentValueString());

        if (this.prefix.isEmpty()) {
            if (contentChroma) {
                if (Core.hudTextBox.getValue()) {
                    Render.drawHorizontallyCenteredChromaString(xPosition + width / 2F, yPosition, scale, dropShadow, chromaSpeed, this.getCleanDisplayText());
                } else {
                    Render.drawHorizontallyCenteredString(xPosition + width / 2F, yPosition, scale, dropShadow, this.getDisplayText());
                }
            }
        } else {
            boolean prefixChroma = this.prefixColor.getValue() == 16;
            float textXPosition = Core.hudTextBox.getValue() ? xPosition + width / 2 - scale * fontRenderer.getStringWidth(this.getDisplayText()) / 2F + 0.7F : xPosition;

            if (this.separator.getValue() == 4) {
                if (prefixChroma) {
                    Render.drawChromaString(textXPosition, yPosition, scale, dropShadow, chromaSpeed, "[");
                } else {
                    Render.drawString(textXPosition, yPosition, scale, dropShadow, prefixColor + "[");
                }
                textXPosition += fontRenderer.getCharWidth('[') * scale;
            }

            if (prefixChroma) {
                Render.drawChromaString(textXPosition, yPosition, scale, dropShadow, chromaSpeed, this.prefix);
            } else {
                Render.drawString(textXPosition, yPosition, scale, dropShadow, prefixColor + this.prefix);
            }


            textXPosition += fontRenderer.getStringWidth(this.prefix) * scale;

            switch (this.separator.getValue()) {
                case 1:
                    if (prefixChroma) {
                        Render.drawChromaString(textXPosition, yPosition, scale, dropShadow, chromaSpeed, ":");
                    } else {
                        Render.drawString(textXPosition, yPosition, scale, dropShadow, prefixColor + ":");
                    }
                    textXPosition += fontRenderer.getCharWidth(':') * scale;
                    break;
                case 4:
                    if (prefixChroma) {
                        Render.drawChromaString(textXPosition, yPosition, scale, dropShadow, chromaSpeed, "]");
                    } else {
                        Render.drawString(textXPosition, yPosition, scale, dropShadow, prefixColor + "]");
                    }
                    textXPosition += fontRenderer.getCharWidth(']') * scale;
                    break;

                default:
                    String separator = " " + this.separator.getCurrentValueString();
                    if (prefixChroma) {
                        Render.drawChromaString(textXPosition, yPosition, scale, dropShadow, chromaSpeed, separator);
                    } else {
                        Render.drawString(textXPosition, yPosition, scale, dropShadow, prefixColor + separator);
                    }
                    textXPosition += fontRenderer.getStringWidth(separator) * scale;
                    break;
            }

            if (contentChroma) {
                Render.drawChromaString(textXPosition, yPosition, scale, dropShadow, chromaSpeed, " " + this.getTextToDisplay());
            } else {
                Render.drawString(textXPosition, yPosition, scale, dropShadow, " " + contentColor + this.getTextToDisplay());
            }
        }
    }

    @Override
    public void update(int newX, int newY, float newScale) {
        this.updateString(this.getDisplayText());
        super.update(newX, newY, newScale);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, int xPosition, int yPosition, int width, int height, float scale, boolean isSelectedButton) {
        if (Core.hudTextBox.getValue()) {
            Render.drawRectangle(xPosition, yPosition, xPosition + width, yPosition + height, 1.0F, textBoxColor, 255F * Core.hudTextBoxOpacity.getValue());
        }
        this.drawText(xPosition, yPosition, width, height, scale);
        for (SmartButton smartButton : this.connectedButtons) {
            smartButton.visible = isSelectedButton;
            smartButton.xPosition = xPosition - 120 < 0 ? xPosition + width + 20 : xPosition - 120;
            smartButton.yPosition = yPosition + this.connectedButtons.size() * 30 + 5 > Video.getGameHeight() ?
                    yPosition - 30 * this.connectedButtons.indexOf(smartButton) - 2 :
                    yPosition + this.connectedButtons.indexOf(smartButton) * 30 - 2;
        }
    }


    private float getLastStringTheoreticalWidth() {
        float defaultWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.lastString.getValue()) + (Core.hudTextShadow.getValue() ? 0.3F : -0.2F);

        if (Core.hudTextBox.getValue()) defaultWidth += 10F;

        return (float) Math.ceil(defaultWidth * this.getScale());
    }

    @Override
    public float getXCoordinate() {
        float xCoordinate = this.getRawXPosition();
        float toAddX;
        switch (this.alignment.getValue()) {
            case 0:
                toAddX = 0;
                break;
            case 1:
                toAddX = (int) ((-this.getWidth() / 2F) + this.getLastStringTheoreticalWidth() / 2F);
                break;
            default:
                toAddX = this.getLastStringTheoreticalWidth() - this.getWidth();
                break;
        }
        xCoordinate += toAddX;
        if (xCoordinate + this.getWidth() > Video.getGameWidth() + 1) {
            xCoordinate = Video.getGameWidth() + 1 - this.getWidth();
        }

        return Math.max(xCoordinate, 0F);
    }

    private void updateString(String newString) {
        this.lastString.setValue(Utilities.removeFormattingCodes(newString));
    }

    @Override
    public float getDefaultWidth() {
        float defaultWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.getDisplayText()) + (Core.hudTextShadow.getValue() ? 0.3F : -0.2F);

        if (!Core.hudTextBox.getValue()) return defaultWidth;

        return defaultWidth + 10F;
    }

    @Override
    public float getDefaultHeight() {
        float defaultHeight = 8F + (Core.hudTextShadow.getValue() ? 0.5F : -0.5F);

        if (!Core.hudTextBox.getValue()) return defaultHeight;

        return defaultHeight + 3F;
    }

    @Override
    public float getWidth() {
        return (float) Math.ceil(this.getDefaultWidth() * this.getScale());
    }

    @Override
    public float getHeight() {
        return (float) Math.ceil(this.getDefaultHeight() * this.getScale());
    }

    public void setVisibility(boolean newVisibility) {
        this.hudTextVisible.setValue(newVisibility);
    }

    public void setSeparator(int newSeparatorId) {
        this.separator.setCurrentValue(newSeparatorId);
    }

    public void setPrefixColor(int newPrefixColorId) {
        this.prefixColor.setCurrentValue(newPrefixColorId);
    }

    public void setContentColor(int newContentColorId) {
        this.contentColor.setCurrentValue(newContentColorId);
    }

    public boolean hudTextVisible() {
        return this.hudTextVisible.getValue();
    }
}
