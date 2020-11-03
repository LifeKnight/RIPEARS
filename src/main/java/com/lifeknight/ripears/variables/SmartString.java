package com.lifeknight.ripears.variables;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.EnumChatFormatting;

import static com.lifeknight.ripears.mod.Core.configuration;

public class SmartString extends SmartVariable {
    private final String defaultValue;
    private String value;

    public SmartString(String name, String group, String value, String description) {
        super(name, group, description);
        this.defaultValue = value;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if (configuration != null) {
            configuration.updateConfigurationFromVariables();
            this.onSetValue();
        }
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void clear() {
        this.value = "";
        this.onClear();
    }

    public void onSetValue(){
    }

    public void onClear() {}

    @Override
    public void reset() {
        this.value = this.defaultValue;
    }

    @Override
    public void setValueFromJson(JsonObject jsonObject) {
        this.value = this.getValueFromJson(jsonObject).getAsString();
    }

    @Override
    public void appendToJson(JsonObject jsonObject) {
        jsonObject.add(this.getNameForConfiguration(), new JsonPrimitive(this.value));
    }

    @Override
    public String getCustomDisplayString() {
        if (this.iCustomDisplayString != null) {
            return this.iCustomDisplayString.customDisplayString();
        }
        return this.name + ": " + EnumChatFormatting.YELLOW + this.value;
    }
}
