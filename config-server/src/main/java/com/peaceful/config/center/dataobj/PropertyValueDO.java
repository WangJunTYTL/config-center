package com.peaceful.config.center.dataobj;

import com.peaceful.config.center.domain.Property;

/**
 * Created by Jun on 2018/7/20.
 */
public class PropertyValueDO extends Property {

    private String value;

    public PropertyValueDO(Property property) {
        this.setName(property.getName());
        this.setOption(property.getOption());
        this.setType(property.getType());
        this.setDefaultValue(property.getDefaultValue());
        this.setDescription(property.getDescription());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
