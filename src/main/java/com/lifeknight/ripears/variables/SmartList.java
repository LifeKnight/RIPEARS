package com.lifeknight.ripears.variables;

import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.lifeknight.ripears.utilities.Chat;
import com.lifeknight.ripears.utilities.Utilities;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

import static com.lifeknight.ripears.mod.Core.configuration;

public abstract class SmartList<E> extends SmartVariable {
    private final List<E> defaultValues;
    protected List<E> values = new ArrayList<>();
    protected final HashBiMap<E, String> eToDisplayedStringMap = HashBiMap.create();
    private boolean isIndependent = true;

    public SmartList(String name, String group, List<E> values, String description) {
        super(name, group, description);
        this.defaultValues = values;
    }

    public SmartList(String name, String group, String description) {
        this(name, group, new ArrayList<>(), description);
    }

    @Override
    public List<E> getValue() {
        return this.values;
    }

    public void setValue(List<E> values) {
        this.values = values;
        this.eToDisplayedStringMap.clear();
        for (E element : values) {
            this.eToDisplayedStringMap.put(element, this.asDisplayString(element));
        }
        configuration.updateConfigurationFromVariables();
        this.onSetValue();
    }

    public boolean addElement(E element) {
        if (!this.values.contains(element)) {
            this.values.add(element);
            this.eToDisplayedStringMap.put(element, this.asDisplayString(element));
            configuration.updateConfigurationFromVariables();
            this.onAddElement(element);
            return true;
        }
        return false;
    }

    public boolean removeElement(E element) {
        if (this.values.contains(element)) {
            this.values.remove(element);
            this.eToDisplayedStringMap.remove(element);
            configuration.updateConfigurationFromVariables();
            this.onRemoveElement(element);
            return true;
        }
        return false;
    }

    public boolean contains(E element, Object... objects) {
        return this.values.contains(element);
    }

    public void clear() {
        this.values.clear();
        this.eToDisplayedStringMap.clear();
        configuration.updateConfigurationFromVariables();
        this.onClear();
    }

    public void setValueFromJsonArray(JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {
            try {
                E procuredElement = this.fromString(element.getAsString());
                this.values.add(procuredElement);
                this.eToDisplayedStringMap.put(procuredElement, this.asDisplayString(procuredElement));
            } catch (Exception exception) {
                Chat.queueChatMessageForConnection(EnumChatFormatting.RED + "An error occurred when trying to parse the value of " +
                        EnumChatFormatting.YELLOW + "\"" + element + ".\"" + EnumChatFormatting.RED + " It will not be added to " + this.name + ".");
            }
        }
        if (this.values.isEmpty()) {
            this.values = this.defaultValues;
            for (E element : this.values) {
                this.eToDisplayedStringMap.put(element, this.asDisplayString(element));
            }
        }
    }

    public abstract E fromString(String string);

    public JsonArray toJsonArray() {
        JsonArray asJsonArray = new JsonArray();

        for (E element : this.values) {
            asJsonArray.add(new JsonPrimitive(this.asString(element)));
        }
        return asJsonArray;
    }

    public abstract String asString(E element);

    public E fromFormattedString(String formattedString) {
        return null;
    }

    public String asDisplayString(E element) {
        return element.toString();
    }

    public HashBiMap<E, String> getMap() {
        return this.eToDisplayedStringMap;
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

    public void onAddElement(E element) {
    }

    public void onRemoveElement(E element) {
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

    @Override
    public String getCustomDisplayString() {
        if (this.iCustomDisplayString != null) {
            return this.iCustomDisplayString.customDisplayString();
        }
        return "Edit " + this.name;
    }

    public static class SmartStringList extends SmartList<String> {

        public SmartStringList(String name, String group, List<String> values, String description) {
            super(name, group, values, description);
        }

        public SmartStringList(String name, String group, String description) {
            super(name, group, description);
        }

        @Override
        public String fromString(String string) {
            return string;
        }

        @Override
        public String asString(String element) {
            return element;
        }

        @Override
        public String fromFormattedString(String formattedString) {
            return formattedString;
        }

        @Override
        public boolean contains(String element, Object... objects) {
            if (objects.length == 0) return super.contains(element, objects);

            return Utilities.equalsAny(element, this.values, true, false);
        }
    }

    public static class SmartIntegerList extends SmartList<Integer> {

        public SmartIntegerList(String name, String group, List<Integer> values, String description) {
            super(name, group, values, description);
        }

        public SmartIntegerList(String name, String group, String description) {
            super(name, group, description);
        }

        @Override
        public Integer fromString(String string) {
            return Integer.parseInt(string);
        }

        @Override
        public String asString(Integer element) {
            return String.valueOf(element);
        }

        @Override
        public Integer fromFormattedString(String formattedString) {
            return Integer.parseInt(formattedString);
        }
    }

    public static class SmartDoubleList extends SmartList<Double> {

        public SmartDoubleList(String name, String group, List<Double> values, String description) {
            super(name, group, values, description);
        }

        public SmartDoubleList(String name, String group, String description) {
            super(name, group, description);
        }

        @Override
        public Double fromString(String string) {
            return Double.parseDouble(string);
        }

        @Override
        public String asString(Double element) {
            return String.valueOf(element);
        }

        @Override
        public Double fromFormattedString(String formattedString) {
            return Double.parseDouble(formattedString);
        }
    }
}
