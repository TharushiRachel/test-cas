package com.itechro.cas.config;

import com.itechro.cas.model.dto.integration.response.UpmDetailResponse;

public class UpmDetailResponseCacheConfig {
    public static final TTLCache<String, UpmDetailResponse> UPM_DETAIL_CACHE = new TTLCache<>(30); // 30 minutes TTL
}

