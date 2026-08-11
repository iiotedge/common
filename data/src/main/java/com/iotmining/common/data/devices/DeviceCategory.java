package com.iotmining.common.data.devices;

public enum DeviceCategory {

    // Smart Home
    LIGHTING,
    CLIMATE_CONTROL,
    SECURITY,
    ENTERTAINMENT,
    APPLIANCES,

    // Industrial IoT (Industry 4.0)
    INDUSTRIAL_AUTOMATION,
    ROBOTICS,
    CNC_SYSTEMS,
    PLC_CONTROLLER,
    EDGE_DEVICE,
    INDUSTRIAL_PC,

    // Energy & Utilities
    ENERGY_MANAGEMENT,
    SMART_METER,
    BMS_SYSTEM,
    SOLAR_INVERTER,
    EV_CHARGER,

    // Mobility & Automotive
    VEHICLE,
    TELEMATICS,
    FLEET_TRACKING,
    AGV,
    DRONE,

    // Healthcare IoT
    MEDICAL_DEVICE,
    PATIENT_MONITOR,
    WEARABLE,

    // Agriculture IoT
    AGRICULTURE_SENSOR,
    IRRIGATION_SYSTEM,
    LIVESTOCK_MONITORING,

    // Smart City / Infrastructure
    TRAFFIC_CONTROL,
    SMART_PARKING,
    ENVIRONMENT_MONITORING,
    WATER_MANAGEMENT,

    // Generic
    SENSOR,
    ACTUATOR,
    CONTROLLER,
    GATEWAY,
    CAMERA,
    OTHER;

    public static DeviceCategory fromString(String category) {
        try {
            return DeviceCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid device category: " + category);
        }
    }
}
