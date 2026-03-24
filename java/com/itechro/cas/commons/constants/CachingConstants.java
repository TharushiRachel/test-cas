package com.itechro.cas.commons.constants;

public class CachingConstants {

    public static final String CACHE_INSTANCE_NAME = "cas-hazelcast-cache";

    public static final String USERS_CACHE_KEY = "cas-users";
    public static final String SYSTEM_PARAMS_KEY = "cas-system-params";

    /** Hazelcast map name for UPM responses (keys: {@code ad:{adUser}:{app}} or {@code {userId}:{app}}). */
    public static final String UPM_DETAILS_CACHE_KEY = "cas-upm-details";

}
