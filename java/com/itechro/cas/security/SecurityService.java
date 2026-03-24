package com.itechro.cas.security;

import com.hazelcast.core.Hazelcast;
import com.itechro.cas.commons.constants.CachingConstants;
import com.itechro.cas.config.CasProperties;
import com.itechro.cas.dao.master.jdbc.UserJDBCDao;
import com.itechro.cas.exception.impl.AppsException;
import com.itechro.cas.model.dto.integration.request.UmpDetailLoadByAdIdRQ;
import com.itechro.cas.model.dto.integration.request.UpmDetailLoadRQ;
import com.itechro.cas.model.dto.integration.response.UpmDetailResponse;
import com.itechro.cas.model.dto.master.UserDTO;
import com.itechro.cas.security.helper.UserRetriever;
import com.itechro.cas.service.cache.UpmDetailDistributedCache;
import com.itechro.cas.service.integration.IntegrationService;
import com.itechro.cas.service.master.SystemParameterService;
import com.itechro.cas.util.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class SecurityService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    private SystemParameterService systemParameterService;

    @Autowired
    private CasProperties casProperties;

    @Autowired
    private UserJDBCDao userJDBCDao;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private UpmDetailDistributedCache upmDetailDistributedCache;

    private Map<String, String> credentialsCache;

    @Transactional(propagation = Propagation.SUPPORTS)
//    @Cacheable(CachingConstants.USERS_CACHE_KEY)
    public UserDTO getUserByUsername(String userDetailsStr) throws AppsException {

        UserRetriever userRetriever = new UserRetriever(userDetailsStr);
        userRetriever.setCasProperties(casProperties);
        userRetriever.setUserJDBCDao(userJDBCDao);
        userRetriever.setSystemParameterService(systemParameterService);
        userRetriever.setIntegrationService(integrationService);
        userRetriever.setSecurityService(this);

        return userRetriever
                .initiate()
                .validate()
                .retrieveUser()
                .getUserDTO();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public String getUserCredentials(String userName) {
        if (this.credentialsCache == null || this.credentialsCache.get(userName) == null) {
            return null;
        }
        return new String(Base64.getDecoder().decode(this.credentialsCache.get(userName)));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void expireUserFromCache(String userDetailsStr) {
        Boolean activeDirectoryEnabled = this.systemParameterService.isActiveDirectoryEnabled();
//        if (activeDirectoryEnabled) {
//            String[] split = PasswordUtil.getSplittedUserDetailsStr(userDetailsStr);
//            if (split != null) {
//                this.removeUserCredentials(split[0]);
//            }
//        }

        Hazelcast.getHazelcastInstanceByName(CachingConstants.CACHE_INSTANCE_NAME)
                .getMap(CachingConstants.USERS_CACHE_KEY).remove(userDetailsStr);

//        LOG.info("User cache cleared : {}", userDetailsStr);
    }

    /**
     * Log-out: clears the Hazelcast user session entry and distributed UPM cache for this principal.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void expireUserSessionFromCache(String userDetailsStr, Integer internalUserId) {
        expireUserFromCache(userDetailsStr);
        String loginName = resolveLoginName(userDetailsStr);
        if (loginName != null) {
            upmDetailDistributedCache.invalidateForUser(loginName, internalUserId, casProperties.getApplicationCode());
        }
    }

    private static String resolveLoginName(String userDetailsStr) {
        if (StringUtils.isBlank(userDetailsStr)) {
            return null;
        }
        String[] split = PasswordUtil.getSplittedUserDetailsStr(userDetailsStr);
        if (split != null && split.length > 0 && StringUtils.isNotBlank(split[0])) {
            return StringUtils.trim(split[0]);
        }
        return StringUtils.trim(userDetailsStr);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void destroyUserCache() {
        Hazelcast.getHazelcastInstanceByName(CachingConstants.CACHE_INSTANCE_NAME)
                .getMap(CachingConstants.USERS_CACHE_KEY).destroy();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void cacheUserCredentials(String userName, String password) {
        if (this.credentialsCache == null) {
            this.credentialsCache = new HashMap<>();
        }
        String encoded = Base64.getEncoder().encodeToString(password.getBytes());
        this.credentialsCache.put(userName, encoded);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeUserCredentials(String userName) {
        if (this.credentialsCache != null) {
            this.credentialsCache.remove(userName);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public UpmDetailResponse getUserUPMDetails(String adUserID) throws AppsException {
        UpmDetailResponse response = new UpmDetailResponse();

        if (systemParameterService.isUpmEnabled()) {
            UmpDetailLoadByAdIdRQ upmDetailLoadRQ = new UmpDetailLoadByAdIdRQ();

            upmDetailLoadRQ.setAdUserID(adUserID);
            upmDetailLoadRQ.setAppCode(this.casProperties.getApplicationCode());
            response = integrationService.getUpmDetailsByAdUserIdAndAppCode(upmDetailLoadRQ);

        } else {
            LOG.info("WARN : UPM Is not enabled");
            response.setSolID("004");
            if (adUserID.equals("SYSTEM")) {
                response.setUserID("1");
                response.setFirstName("Itechro");
                response.setLastName("Admin");
                response.setAdUserID("SYSTEM");
                response.setSecurityClass("50");
                response.setApplicationSecurityClass("10");
            } else if (adUserID.equals("amith")) {
                response.setUserID("2");
                response.setFirstName("Amith");
                response.setLastName("Bandara");
                response.setAdUserID("amith");
                response.setSecurityClass("20");
                response.setApplicationSecurityClass("10");
            }

        }

        return response;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public UpmDetailResponse getUserUPMDetailsById(String adUserID) throws AppsException {
        UpmDetailResponse response = new UpmDetailResponse();

        if (systemParameterService.isUpmEnabled()) {
            UpmDetailLoadRQ upmDetailLoadRQ = new UpmDetailLoadRQ();

            upmDetailLoadRQ.setUserID(adUserID);
            upmDetailLoadRQ.setAppCode(this.casProperties.getApplicationCode());
            response = integrationService.getUpmDetailsByUserIdAndAppCode(upmDetailLoadRQ);

        } else {
            LOG.info("WARN : UPM Is not enabled");
            response.setSolID("004");
            if (adUserID.equals("SYSTEM")) {
                response.setUserID("1");
                response.setFirstName("Itechro");
                response.setLastName("Admin");
                response.setAdUserID("SYSTEM");
                response.setSecurityClass("50");
                response.setApplicationSecurityClass("10");
            } else if (adUserID.equals("amith")) {
                response.setUserID("2");
                response.setFirstName("Amith");
                response.setLastName("Bandara");
                response.setAdUserID("amith");
                response.setSecurityClass("20");
                response.setApplicationSecurityClass("10");
            }

        }

        return response;
    }

    @Transactional(readOnly = true)
    public UserDTO getUserDetailsByUserAD(String userAD) throws AppsException {

        UserRetriever userRetriever = new UserRetriever(userAD);
        userRetriever.setCasProperties(casProperties);
        userRetriever.setUserJDBCDao(userJDBCDao);
        userRetriever.setSystemParameterService(systemParameterService);
        userRetriever.setIntegrationService(integrationService);
        userRetriever.setSecurityService(this);

        return userRetriever
                .initiate()
                .retrieveUser()
                .getUserDTO();
    }
}
