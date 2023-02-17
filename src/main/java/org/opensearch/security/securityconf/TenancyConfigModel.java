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

package org.opensearch.security.securityconf;
import org.opensearch.security.securityconf.impl.v7.TenancyConfigV7;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TenancyConfigModel {

    protected final Logger log = LogManager.getLogger(this.getClass());

    private final TenancyConfigV7 tenancyConfig;

    public TenancyConfigModel(TenancyConfigV7 tenancyConfig) {
        log.info("************** Tenancy_abhivka TenancyConfigModel tenancyConfig = " + tenancyConfig.toString());
        this.tenancyConfig = tenancyConfig;
    }

    public boolean isDashboardsMultitenancyEnabled() { return this.tenancyConfig.multitenancy_enabled; };
    public boolean isDashboardsPrivateTenantEnabled() { return this.tenancyConfig.private_tenant_enabled; };
    public String dashboardsDefaultTenant() { return this.tenancyConfig.default_tenant; };
}

