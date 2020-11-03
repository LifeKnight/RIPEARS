package com.lifeknight.ripears.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lifeknight.ripears.utilities.Render;
import com.lifeknight.ripears.utilities.Utilities;
import com.lifeknight.ripears.utilities.Video;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SmartVariable {
    protected static final List<SmartVariable> variables = new ArrayList<>();
    private final static Color boxColor = new Color(26, 26, 26);
    protected final String name;
    protected final String group;
    private final String description;
    private boolean storeValue = true;
    private boolean showInLifeKnightGui = true;
    protected ICustomDisplayString iCustomDisplayString = null;

    public SmartVariable(String name, String group, String description) {
        this.name = name;
        this.group = group;
        this.description = description;
        variables.add(this);
    }
    public SmartVariable(String name, String group) {
        this(name, group, null);
    }

    public static List<SmartVariable> getVariables() {
        return variables;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public String getNameForConfiguration() {
        if (this.name.contains(" ")) {
            String[] words = this.name.split(" ");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                result.append(Utilities.formatCapitalization(words[i], i != 0));
            }
            return result.toString();
        }
        return Utilities.formatCapitalization(this.name, false);
    }
    
    public String getGroup() {
        return this.group;
    }

    public String getGroupForConfiguration() {
        if (this.group.contains(" ")) {
            String[] words = this.group.split(" ");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                result.append(Utilities.formatCapitalization(words[i], i != 0));
            }
            return result.toString();
        }
        return Utilities.formatCapitalization(this.group, false);
    }

    public abstract Object getValue();

    public abstract void reset();

    public abstract void setValueFromJson(JsonObject jsonObject);

    public abstract void appendToJson(JsonObject jsonObject);

    protected JsonElement getValueFromJson(JsonObject jsonObject) {
        return jsonObject.getAsJsonObject(this.getGroupForConfiguration()).get(this.getNameForConfiguration());
    }

    public boolean isStoreValue() {
        return this.storeValue;
    }

    public void setStoreValue(boolean storeValue) {
        this.storeValue = storeValue;
    }

    public boolean showInLifeKnightGui() {
        return this.showInLifeKnightGui;
    }

    public void setShowInLifeKnightGui(boolean showInLifeKnightGui) {
        this.showInLifeKnightGui = showInLifeKnightGui;
    }

    public String getCustomDisplayString() {
        if (this.iCustomDisplayString != null) {
            return this.iCustomDisplayString.customDisplayString();
        }
        return this.name + ": " + this.getValue();
    }

    public void setiCustomDisplayString(ICustomDisplayString iCustomDisplayString) {
        this.iCustomDisplayString = iCustomDisplayString;
    }

    public void drawDescription(int mouseX, int mouseY) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        String description = this.getDescription();

        int width = Math.min(fontRenderer.getStringWidth(description), Video.getGameWidth() / 2);
        int xPosition = mouseX - width < 0 ? mouseX : mouseX - width;

        int height = 9 * fontRenderer.listFormattedStringToWidth(description, width - 8).size() + 4;

        int yPosition = mouseY + height > Video.getGameHeight() ? mouseY - height : mouseY;

        Render.drawEmptyBox(xPosition, yPosition, xPosition + width, yPosition + height, Color.BLACK, 200F);
        Render.drawRectangle(xPosition + 1, yPosition + 1, xPosition + 1 + width - 2, yPosition + 1 + height - 2, boxColor, 170F);
        fontRenderer.drawSplitString(description, xPosition + 4, yPosition + 2, width - 8, 0xffffffff);
    }

    public interface ICustomDisplayString {
        String customDisplayString(Object... objects);
    }
}
