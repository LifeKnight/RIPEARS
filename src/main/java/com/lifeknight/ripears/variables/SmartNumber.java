package com.lifeknight.ripears.variables;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.lifeknight.ripears.mod.Core.configuration;

public abstract class SmartNumber extends SmartVariable {
    protected final Number defaultValue;
    protected final Number defaultMinimumValue;
    protected final Number defaultMaximumValue;
    protected Number value;
    protected Number minimumValue;
    protected Number maximumValue;

    SmartNumber(String name, String group, Number value, String description) {
        super(name, group, description);
        this.defaultValue = value;
        this.value = value;
        this.defaultMinimumValue = Long.MIN_VALUE;
        this.defaultMaximumValue = Long.MAX_VALUE;
        this.minimumValue = Long.MIN_VALUE;
        this.maximumValue = Long.MAX_VALUE;
    }

    SmartNumber(String name, String group, Number value, Number minimumValue, Number maximumValue, String description) {
        super(name, group, description);
        this.defaultValue = value;
        this.value = value;
        this.defaultMinimumValue = minimumValue;
        this.defaultMaximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    public Number getValue() {
        return this.value;
    }

    public double getAsDouble() {
        return this.value.doubleValue();
    }

    public void setValue(Number newValue) {
        this.value = newValue;
        if (configuration != null) {
            configuration.updateConfigurationFromVariables();
            this.onSetValue();
        }
    }

    public Number getMinimumValue() {
        return this.minimumValue;
    }

    public double getMinimumAsDouble() {
        return this.minimumValue.doubleValue();
    }

    public Number getMaximumValue() {
        return this.maximumValue;
    }

    public double getMaximumAsDouble() {
        return this.maximumValue.doubleValue();
    }

    public void setMinimumValue(Number minimumValue) {
        this.minimumValue = minimumValue;
        this.onSetMinimumValue();
    }

    public void setMaximumValue(Number maximumValue) {
        this.maximumValue = maximumValue;
        this.onSetMaximumValue();
    }

    public void onSetValue() {
    }

    public void onSetMinimumValue() {
    }

    public void onSetMaximumValue() {
    }

    @Override
    public void reset() {
        this.value = this.defaultValue;
        this.maximumValue = this.defaultMaximumValue;
        this.minimumValue = this.defaultMinimumValue;
    }

    @Override
    public void setValueFromJson(JsonObject jsonObject) {
        this.setValue(this.getValueFromJson(jsonObject).getAsNumber());
    }

    @Override
    public void appendToJson(JsonObject jsonObject) {
        jsonObject.add(this.getNameForConfiguration(), new JsonPrimitive(this.getValue()));
    }

    @Override
    public String getCustomDisplayString() {
        if (this.iCustomDisplayString != null) {
            return this.iCustomDisplayString.customDisplayString(this.getValue());
        }
        return this.name + ": " + this.value;
    }

    public String getCustomDisplayString(Number value) {
        if (this.iCustomDisplayString != null) {
            return this.iCustomDisplayString.customDisplayString(value);
        }
        return this.name + ": " + value;
    }

    public static class SmartInteger extends SmartNumber {
        public SmartInteger(String name, String group, Integer value, String description) {
            super(name, group, value, description);
        }

        public SmartInteger(String name, String group, Integer value, Integer minimumValue, Integer maximumValue, String description) {
            super(name, group, value, minimumValue, maximumValue, description);
        }
        

        @Override
        public Integer getValue() {
            return (Integer) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.intValue());
        }


        public String getCustomDisplayString(Number value) {
            value = value.intValue();
            if (this.iCustomDisplayString != null) {
                return this.iCustomDisplayString.customDisplayString(value);
            }
            return this.name + ": " + value;
        }
    }

    public static class SmartDouble extends SmartNumber {
        public SmartDouble(String name, String group, Double value, String description) {
            super(name, group, value, description);
        }

        public SmartDouble(String name, String group, Double value, Double minimumValue, Double maximumValue, String description) {
            super(name, group, value, minimumValue, maximumValue, description);
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        @Override
        public Double getValue() {
            return (Double) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.doubleValue());
        }

        public String getCustomDisplayString(Number value) {
            value = value.doubleValue();
            if (this.iCustomDisplayString != null) {
                return this.iCustomDisplayString.customDisplayString(value);
            }
            return this.name + ": " + value;
        }
    }

    public static class SmartLong extends SmartNumber {
        public SmartLong(String name, String group, Long value, String description) {
            super(name, group, value, description);
        }

        public SmartLong(String name, String group, Long value, Long minimumValue, Long maximumValue, String description) {
            super(name, group, value, minimumValue, maximumValue, description);
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.longValue());
        }

        public String getCustomDisplayString(Number value) {
            value = value.longValue();
            if (this.iCustomDisplayString != null) {
                return this.iCustomDisplayString.customDisplayString(value);
            }
            return this.name + ": " + value;
        }
    }

    public static class SmartFloat extends SmartNumber {
        public SmartFloat(String name, String group, Float value, String description) {
            super(name, group, value, description);
        }

        public SmartFloat(String name, String group, Float value, Float minimumValue, Float maximumValue, String description) {
            super(name, group, value, minimumValue, maximumValue, description);
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        @Override
        public Float getValue() {
            return (Float) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.floatValue());
        }

        public String getCustomDisplayString(Number value) {
            value = value.floatValue();
            if (this.iCustomDisplayString != null) {
                return this.iCustomDisplayString.customDisplayString(value);
            }
            return this.name + ": " + value;
        }
    }
}
