package org.opensearch.security.dlic.rest.validation;



import java.util.Map;
import java.util.Set;


import org.opensearch.common.settings.Settings;

import org.opensearch.common.bytes.BytesReference;
import org.opensearch.common.xcontent.XContentHelper;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.rest.RestRequest;



import org.apache.logging.log4j.LogManager; //********
import org.apache.logging.log4j.Logger; //********
import org.opensearch.security.privileges.PrivilegesEvaluator;

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

public class TenancyConfigValidator extends AbstractConfigurationValidator {

    protected final Logger log = LogManager.getLogger(this.getClass());
    private PrivilegesEvaluator evaluator;

    public TenancyConfigValidator(final RestRequest request, final BytesReference ref,
                                  final Settings opensearchSettings,final PrivilegesEvaluator evaluator, Object... param ) {
        super(request, ref, opensearchSettings, param);
        this.evaluator = evaluator;
        this.payloadMandatory = true;

        allowedKeys.put("multitenancy_enabled", DataType.BOOLEAN);
        allowedKeys.put("private_tenant_enabled", DataType.BOOLEAN);
        allowedKeys.put("default_tenant", DataType.STRING);
    }

    @Override
    public boolean validate() {
        if (!super.validate()) {
            return false;
        }
        log.info("************** Tenancy_abhivka Entering new validate ***WOW****");
        final Map<String, Object> contentAsMap = XContentHelper.convertToMap(this.content, false, XContentType.JSON).v2();

        String default_tenant = (String) contentAsMap.get("default_tenant");

        log.info("************** Tenancy_abhivka validate contentAsMap = " + contentAsMap.toString());
        log.info("************** Tenancy_abhivka validate default_tenant = " + default_tenant);

        Boolean isPrivateTenantEnabled = (Boolean) contentAsMap.get("private_tenant_enabled");
        log.info("************** Tenancy_abhivka validate private tenant in content = " + isPrivateTenantEnabled);


        log.info("************** Tenancy_abhivka validate default_tenant 2  = " + this.evaluator.getDashboardsDefaultTenant());

        log.info("************** Tenancy_abhivka validate default_tenant 3  = " + evaluator.getDashboardsDefaultTenant());

        log.info("************** Tenancy_abhivka validate all tenants = " + this.evaluator.getAllConfiguredTenantNames());

        Set<String> availableTenants = this.evaluator.getAllConfiguredTenantNames();

        log.info("************** Tenancy_abhivka isPrivateTenantEnabled = " + isPrivateTenantEnabled);

        if(default_tenant.equals("Private"))
        {
            log.info("************** Tenancy_abhivka MY case 5");
            if(isPrivateTenantEnabled)
            {
                log.info("************** Tenancy_abhivka MY case 7");
                return true;
            }
            log.info("************** Tenancy_abhivka MY case 9");
            return false;
        }

        if(default_tenant == null)
        {
            return false;
        }
        if(!availableTenants.contains(default_tenant))
        {
            log.info("************** Tenancy_abhivka MY case 2");
            return false;
        }



//        if(default_tenant.equals("Private")){
//            log.info("************** Tenancy_abhivka MY case 2");
//            return false;
//        }
        log.info("************** Tenancy_abhivka MY case 3");
        return true;
    }
}
