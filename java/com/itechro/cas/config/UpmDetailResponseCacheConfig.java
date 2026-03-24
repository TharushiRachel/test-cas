package com.itechro.cas.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Helpers for UPM cache keys. Values are stored in Hazelcast map {@link com.itechro.cas.commons.constants.CachingConstants#UPM_DETAILS_CACHE_KEY}.
 */
public class UpmDetailResponseCacheConfig {

    /**
     * Canonical key for UPM loaded by AD user id + app code (must match IntegrationService AD UPM methods).
     */
    public static String adUpmCacheKey(String adUserId, String applicationCode) {
        if (adUserId == null) {
            return null;
        }
        String app = StringUtils.isNotBlank(applicationCode) ? StringUtils.trim(applicationCode) : "";
        return "ad:" + StringUtils.trim(adUserId) + ":" + app;
    }
}

