package com.iotmining.common.data.devices;

public enum DeviceCategory {
    LIGHTING,
    CLIMATE_CONTROL,
    SECURITY,
    ENTERTAINMENT,
    APPLIANCES;

    public static DeviceCategory fromString(String category) {
        try {
            return DeviceCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid device category: " + category);
        }
    }
}
