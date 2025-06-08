package com.iotmining.common.base.context;

public class TenantKeySpaceContext {

    private static final ThreadLocal<String> currentKeyspace = new ThreadLocal<>();

    public static void setKeyspace(String keyspace) {
        currentKeyspace.set(keyspace);
    }

    public static String getKeyspace() {
        return currentKeyspace.get();
    }

    public static void clear() {
        currentKeyspace.remove();
    }
}
