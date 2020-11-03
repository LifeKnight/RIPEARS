package com.lifeknight.ripears.variables;


import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.lifeknight.ripears.mod.Core.configuration;
import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.RED;

public class SmartBoolean extends SmartVariable {
    private final boolean defaultValue;
    private boolean value;
    private SmartVariable lifeKnightList;

    public SmartBoolean(String name, String group, boolean value, String description) {
        super(name, group, description);
        this.value = value;
        this.defaultValue = value;
    }

    public SmartBoolean(String name, String group, boolean value, SmartList<?> lifeKnightList, String description) {
        this(name, group, value, description);
        this.lifeKnightList = lifeKnightList;
        lifeKnightList.setIndependent(false);
    }

    public SmartBoolean(String name, String group, boolean value, SmartObjectList<?> lifeKnightObjectList, String description) {
        this(name, group, value, description);
        this.lifeKnightList = lifeKnightObjectList;
        lifeKnightObjectList.setIndependent(false);
    }

    public Boolean getDefaultValue() {
        return this.defaultValue;
    }

    public boolean hasList() {
        return this.lifeKnightList != null;
    }

    public SmartVariable getList() {
        return this.lifeKnightList;
    }

    public Boolean getValue() {
        return this.value;
    }

    public void toggle() {
        this.value = !this.value;
        configuration.updateConfigurationFromVariables();
        this.onSetValue();
    }

    public void setValue(boolean newValue) {
        this.value = newValue;
        if (configuration != null) {
            configuration.updateConfigurationFromVariables();
            this.onSetValue();
        }
    }

    public String getAsString() {
        if (this.value) {
            return this.name + ": " + GREEN + "ENABLED";
        } else {
            return this.name + ": " + RED + "DISABLED";
        }
    }

    public void onSetValue() {
    }

    @Override
    public void reset() {
        this.value = this.defaultValue;
    }

    @Override
    public void setValueFromJson(JsonObject jsonObject) {
        this.value = this.getValueFromJson(jsonObject).getAsBoolean();
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
        return this.name + ": " + (this.value ? GREEN + "ENABLED" : RED + "DISABLED");
    }
}
