package com.lifeknight.ripears.variables;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public abstract class SmartObject extends SmartVariable {
    protected final List<SmartVariable> connectedVariables = new ArrayList<>();

    public SmartObject(String name, String group, String description) {
        super(name, group, description);
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public void reset() {

    }

    @Override
    public abstract String getCustomDisplayString();

    public List<SmartVariable> getConnectedVariables() {
        return this.connectedVariables;
    }

    public SmartBoolean createBoolean(String name, String group, boolean value, String description) {
        SmartBoolean lifeKnightBoolean = new SmartBoolean(name, group, value, description);
        lifeKnightBoolean.setShowInLifeKnightGui(false);
        lifeKnightBoolean.setStoreValue(false);
        this.connectedVariables.add(lifeKnightBoolean);
        return lifeKnightBoolean;
    }

    public SmartNumber createNumber(String name, String group, Number value, String description) {
        SmartNumber lifeKnightNumber;
        if (value instanceof Integer) {
            lifeKnightNumber = new SmartNumber.SmartInteger(name, group, (Integer) value, description);
        } else if (value instanceof Double) {
            lifeKnightNumber = new SmartNumber.SmartDouble(name, group, (Double) value, description);
        } else if (value instanceof Long) {
            lifeKnightNumber = new SmartNumber.SmartLong(name, group, (Long) value, description);
        } else if (value instanceof Float) {
            lifeKnightNumber = new SmartNumber.SmartFloat(name, group, (Float) value, description);
        } else {
            throw new IllegalArgumentException("Invalid parameters!");
        }
        lifeKnightNumber.setShowInLifeKnightGui(false);
        lifeKnightNumber.setStoreValue(false);
        this.connectedVariables.add(lifeKnightNumber);
        return lifeKnightNumber;
    }

    public SmartNumber createNumber(String name, String group, Number value, Number minimumValue, Number maximumValue, String description) {
        SmartNumber lifeKnightNumber;
        if (value instanceof Integer) {
            lifeKnightNumber = new SmartNumber.SmartInteger(name, group, (Integer) value, (Integer) minimumValue, (Integer) maximumValue, description);
        } else if (value instanceof Double) {
            lifeKnightNumber = new SmartNumber.SmartDouble(name, group, (Double) value, (Double) minimumValue, (Double) maximumValue, description);
        } else if (value instanceof Long) {
            lifeKnightNumber = new SmartNumber.SmartLong(name, group, (Long) value, (Long) minimumValue, (Long) maximumValue, description);
        } else if (value instanceof Float) {
            lifeKnightNumber = new SmartNumber.SmartFloat(name, group, (Float) value, (Float) minimumValue, (Float) maximumValue, description);
        } else {
            throw new IllegalArgumentException("Invalid parameters!");
        }
        lifeKnightNumber.setShowInLifeKnightGui(false);
        lifeKnightNumber.setStoreValue(false);
        this.connectedVariables.add(lifeKnightNumber);
        return lifeKnightNumber;
    }

    public SmartString createString(String name, String group, String value, String description) {
        SmartString lifeKnightString = new SmartString(name, group, value, description);
        lifeKnightString.setShowInLifeKnightGui(false);
        lifeKnightString.setStoreValue(false);
        this.connectedVariables.add(lifeKnightString);
        return lifeKnightString;
    }

    public SmartCycle createCycle(String name, String group, List<String> elements, int startingIndex, String description) {
        SmartCycle lifeKnightCycle = new SmartCycle(name, group, elements, startingIndex, description);
        lifeKnightCycle.setShowInLifeKnightGui(false);
        lifeKnightCycle.setStoreValue(false);
        this.connectedVariables.add(lifeKnightCycle);
        return lifeKnightCycle;
    }

    public boolean isSearchResult(String search) {
        return this.name.toLowerCase().contains(search.toLowerCase());
    }

    public JsonObject getAsJsonObject() {
        JsonObject asJsonObject = new JsonObject();

        List<String> groups = new ArrayList<>();

        for (SmartVariable variable : this.connectedVariables) {
            if (!groups.contains(variable.getGroupForConfiguration())) {
                groups.add(variable.getGroupForConfiguration());
            }
        }

        for (String group : groups) {
            JsonObject jsonObject = new JsonObject();
            for (SmartVariable smartVariable : this.connectedVariables) {
                if (smartVariable.getGroupForConfiguration().equals(group)) {
                    smartVariable.appendToJson(jsonObject);
                }
            }
            asJsonObject.add(group, jsonObject);
        }
        return asJsonObject;
    }

    public void setValueFromJsonObject(JsonObject jsonObject) {
        for (SmartVariable variable : this.connectedVariables) {
            variable.setValueFromJson(jsonObject);
        }
    }

    public static SmartObject getDefault() {
        SmartObject lifeKnightObject = new SmartObject("Default Name", "Default Group", "Default SmartObject") {
            @Override
            public void setValueFromJson(JsonObject jsonObject) {

            }

            @Override
            public void appendToJson(JsonObject jsonObject) {

            }

            @Override
            public String getCustomDisplayString() {
                return null;
            }
        };
        lifeKnightObject.setStoreValue(false);
        lifeKnightObject.setShowInLifeKnightGui(false);
        return lifeKnightObject;
    }
}
