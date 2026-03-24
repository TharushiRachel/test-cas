package com.itechro.cas.config;

import com.itechro.cas.commons.constants.CachingConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * UPM cache configuration: Hazelcast map name, key helpers, and access via
 * {@link com.itechro.cas.service.cache.UpmDetailDistributedCache} (replaces the former in-JVM {@code TTLCache} field {@code UPM_DETAIL_CACHE}).
 */
public class UpmDetailResponseCacheConfig {

    /**
     * Hazelcast distributed map name for {@link com.itechro.cas.model.dto.integration.response.UpmDetailResponse} entries.
     * Same value as {@link CachingConstants#UPM_DETAILS_CACHE_KEY}; use {@link com.itechro.cas.service.cache.UpmDetailDistributedCache} for get/put/remove.
     */
    public static final String UPM_DETAIL_CACHE = CachingConstants.UPM_DETAILS_CACHE_KEY;

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

