package com.iotmining.common.data.devices;

public enum DeviceType {

    /* =======================
       SMART HOME
    ======================== */
    BULB(DeviceCategory.LIGHTING),
    SMART_SWITCH(DeviceCategory.LIGHTING),

    AC(DeviceCategory.CLIMATE_CONTROL),
    FAN(DeviceCategory.CLIMATE_CONTROL),
    THERMOSTAT(DeviceCategory.CLIMATE_CONTROL),

    DOORBELL(DeviceCategory.SECURITY),
    CCTV(DeviceCategory.SECURITY),
    MOTION_SENSOR(DeviceCategory.SECURITY),

    CAMERA(DeviceCategory.SECURITY),

    TV(DeviceCategory.ENTERTAINMENT),
    SPEAKER(DeviceCategory.ENTERTAINMENT),

    WASHING_MACHINE(DeviceCategory.APPLIANCES),
    REFRIGERATOR(DeviceCategory.APPLIANCES),

    /* =======================
       INDUSTRIAL IOT
    ======================== */
    PLC(DeviceCategory.PLC_CONTROLLER),
    CNC(DeviceCategory.CNC_SYSTEMS),
    INDUSTRIAL_ROBOT(DeviceCategory.ROBOTICS),
    INDUSTRIAL_GATEWAY(DeviceCategory.EDGE_DEVICE),
    INDUSTRIAL_PC(DeviceCategory.INDUSTRIAL_PC),

    /* =======================
       ENERGY
    ======================== */
    ENERGY_METER(DeviceCategory.SMART_METER),
    BMS(DeviceCategory.BMS_SYSTEM),
    SOLAR_INVERTER(DeviceCategory.SOLAR_INVERTER),
    EV_CHARGER_STATION(DeviceCategory.EV_CHARGER),

    /* =======================
       MOBILITY
    ======================== */
    CAR(DeviceCategory.VEHICLE),
    TRUCK(DeviceCategory.VEHICLE),
    GPS_TRACKER(DeviceCategory.TELEMATICS),
    FLEET_DEVICE(DeviceCategory.FLEET_TRACKING),
    AGV_VEHICLE(DeviceCategory.AGV),
    DRONE_UNIT(DeviceCategory.DRONE),

    /* =======================
       HEALTHCARE
    ======================== */
    HEART_MONITOR(DeviceCategory.MEDICAL_DEVICE),
    BLOOD_PRESSURE_MONITOR(DeviceCategory.MEDICAL_DEVICE),
    FITNESS_TRACKER(DeviceCategory.WEARABLE),

    /* =======================
       AGRICULTURE
    ======================== */
    SOIL_SENSOR(DeviceCategory.AGRICULTURE_SENSOR),
    WEATHER_STATION(DeviceCategory.AGRICULTURE_SENSOR),
    SMART_IRRIGATION(DeviceCategory.IRRIGATION_SYSTEM),

    /* =======================
       SMART CITY
    ======================== */
    TRAFFIC_LIGHT(DeviceCategory.TRAFFIC_CONTROL),
    PARKING_SENSOR(DeviceCategory.SMART_PARKING),
    AIR_QUALITY_SENSOR(DeviceCategory.ENVIRONMENT_MONITORING),
    WATER_LEVEL_SENSOR(DeviceCategory.WATER_MANAGEMENT),

    /* =======================
       GENERIC
    ======================== */
    GENERIC_SENSOR(DeviceCategory.SENSOR),
    GENERIC_ACTUATOR(DeviceCategory.ACTUATOR),
    GENERIC_CONTROLLER(DeviceCategory.CONTROLLER),
    GENERIC_GATEWAY(DeviceCategory.GATEWAY),
    GENERIC_CAMERA(DeviceCategory.CAMERA),
    CUSTOM(DeviceCategory.OTHER);

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
