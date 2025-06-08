package com.iotmining.common.data.devices;

public enum DeviceType {
    BULB(DeviceCategory.LIGHTING),
    AC(DeviceCategory.CLIMATE_CONTROL),
    FAN(DeviceCategory.CLIMATE_CONTROL),
    THERMOSTAT(DeviceCategory.CLIMATE_CONTROL),
    DOORBELL(DeviceCategory.SECURITY),
    CAMERA(DeviceCategory.SECURITY),
    SPEAKER(DeviceCategory.ENTERTAINMENT),
    TV(DeviceCategory.ENTERTAINMENT);

    private final DeviceCategory category;

    DeviceType(DeviceCategory category) {
        this.category = category;
    }

    public DeviceCategory getCategory() {
        return category;
    }

    public static DeviceType fromString(String type) {
        try {
            return DeviceType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid device type: " + type);
        }
    }
}
