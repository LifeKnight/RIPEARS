package com.lifeknight.ripears.variables;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lifeknight.ripears.utilities.Chat;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.ripears.mod.Core.configuration;

public abstract class SmartObjectList<T extends SmartObject> extends SmartVariable {
    private final List<T> defaultValues;
    private List<T> values = new ArrayList<>();
    private boolean isIndependent = true;

    public SmartObjectList(String name, String group, String description) {
        super(name, group, description);
        this.defaultValues = new ArrayList<>();
    }

    public SmartObjectList(String name, String group, List<T> value, String description) {
        super(name, group, description);
        this.defaultValues = value;
    }

    @Override
    public List<T> getValue() {
        return this.values;
    }

    public void setValues(List<T> values) {
        this.values = values;
        configuration.updateConfigurationFromVariables();
        this.onSetValue();
    }

    public boolean addElement(T element) {
        if (!this.values.contains(element)) {
            this.values.add(element);
            configuration.updateConfigurationFromVariables();
            this.onAddElement(element);
            return true;
        }
        return false;
    }

    public boolean removeElement(T element) {
        if (this.values.contains(element)) {
            this.values.remove(element);
            configuration.updateConfigurationFromVariables();
            this.onRemoveElement(element);
            return true;
        }
        return false;
    }

    public void removeByDisplayString(String displayString) {
        for (T element : this.values) {
            if (element.getCustomDisplayString().equals(displayString)) {
                this.removeElement(element);
                break;
            }
        }
    }

    public void clear() {
        this.values.clear();
        configuration.updateConfigurationFromVariables();
        this.onClear();
    }

    public void setValueFromJsonArray(JsonArray jsonElements) {
        for (JsonElement element : jsonElements) {
            try {
                T procuredElement = fromString(element.toString());
                this.values.add(procuredElement);
            } catch (Exception exception) {
                Chat.queueChatMessageForConnection(EnumChatFormatting.RED + "An error occurred when trying to parse the value of " +
                        EnumChatFormatting.YELLOW + "\"" + element + ".\"" + EnumChatFormatting.RED + " It will not be added to " + name + ".");
            }

            if (this.values.isEmpty()) this.values = this.defaultValues;
        }
    }

    public T fromString(String string) {
        T lifeKnightObject = getDefault();
        lifeKnightObject.setValueFromJsonObject(new JsonParser().parse(string).getAsJsonObject());
        return lifeKnightObject;
    }

    public JsonArray toJsonArray() {
        JsonArray jsonArray = new JsonArray();

        for (T element : this.values) {
            jsonArray.add(element.getAsJsonObject());
        }

        return jsonArray;
    }

    @Override
    public void reset() {
        this.values = this.defaultValues;
    }

    @Override
    public void setValueFromJson(JsonObject jsonObject) {
        this.setValueFromJsonArray(this.getValueFromJson(jsonObject).getAsJsonArray());
    }

    @Override
    public void appendToJson(JsonObject jsonObject) {
        jsonObject.add(this.getNameForConfiguration(), this.toJsonArray());
    }

    @Override
    public String getCustomDisplayString() {
        if (this.iCustomDisplayString != null) {
            return this.iCustomDisplayString.customDisplayString();
        }
        return "Edit " + this.name;
    }


    public void onAddElement(T element) {
    }

    public void onRemoveElement(T element) {
    }

    public void onClear() {
    }

    public void onSetValue() {
    }

    public void setIndependent(boolean isIndependent) {
        this.isIndependent = isIndependent;
    }

    public boolean isIndependent() {
        return this.isIndependent;
    }

    public abstract T getDefault();
}
