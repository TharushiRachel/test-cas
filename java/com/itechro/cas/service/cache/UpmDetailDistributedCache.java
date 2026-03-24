package com.itechro.cas.service.cache;

import com.hazelcast.core.Hazelcast;
import com.itechro.cas.commons.constants.CachingConstants;
import com.itechro.cas.config.UpmDetailResponseCacheConfig;
import com.itechro.cas.model.dto.integration.response.UpmDetailResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

/**
 * Distributed UPM detail cache (Hazelcast). Shared across all CAS application instances
 * when nodes are joined via {@code apps.cache.hazelcast.*}.
 */
@Component
public class UpmDetailDistributedCache {

    private static final Logger LOG = LoggerFactory.getLogger(UpmDetailDistributedCache.class);

    public UpmDetailResponse get(String key) {
        if (key == null) {
            return null;
        }
        try {
            return map().get(key);
        } catch (Exception e) {
            LOG.warn("UPM distributed cache get failed for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    public void put(String key, UpmDetailResponse value) {
        if (key == null || value == null) {
            return;
        }
        try {
            map().put(key, value);
        } catch (Exception e) {
            LOG.warn("UPM distributed cache put failed for key {}: {}", key, e.getMessage());
        }
    }

    public void remove(String key) {
        if (key == null) {
            return;
        }
        try {
            map().remove(key);
        } catch (Exception e) {
            LOG.warn("UPM distributed cache remove failed for key {}: {}", key, e.getMessage());
        }
    }

    /**
     * Evicts UPM entries for this principal: AD-style key, user-id+app key (login name), and optional Finacle/UPM numeric id key.
     */
    public void invalidateForUser(String loginName, Integer internalUserId, String applicationCode) {
        String app = StringUtils.isNotBlank(applicationCode) ? StringUtils.trim(applicationCode) : "";
        String trimmed = StringUtils.trimToNull(loginName);
        if (trimmed == null) {
            return;
        }
        remove(UpmDetailResponseCacheConfig.adUpmCacheKey(trimmed, app));
        remove(trimmed + ":" + app);
        if (internalUserId != null) {
            remove(internalUserId + ":" + app);
        }
    }

    private ConcurrentMap<String, UpmDetailResponse> map() {
        return Hazelcast.getHazelcastInstanceByName(CachingConstants.CACHE_INSTANCE_NAME)
                .getMap(CachingConstants.UPM_DETAILS_CACHE_KEY);
    }
}
