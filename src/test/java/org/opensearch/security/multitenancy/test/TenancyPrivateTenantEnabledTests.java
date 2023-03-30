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

package org.opensearch.security.multitenancy.test;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.Test;

import org.opensearch.security.test.SingleClusterTest;
import org.opensearch.security.test.helper.rest.RestHelper.HttpResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

public class TenancyPrivateTenantEnabledTests extends SingleClusterTest {
    private final Header asAdminUser = encodeBasicHeader("admin", "admin");
    private final Header asUser = encodeBasicHeader("kirk", "kirk");
    private final Header onUserTenant = new BasicHeader("securitytenant", "__user__");

    private static String createIndexPatternDoc(final String title) {
        return "{"+
                "\"type\" : \"index-pattern\","+
                "\"updated_at\" : \"2018-09-29T08:56:59.066Z\","+
                "\"index-pattern\" : {"+
                "\"title\" : \"" + title + "\""+
                "}}";
    }

    @Override
    protected String getResourceFolder() {
        return "multitenancy";
    }

    @Test
    public void testPrivateTenantDisabled_endToEndTest() throws Exception {
        setup();

        final HttpResponse getSettingResponse = nonSslRestHelper().executeGetRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", asAdminUser);
        assertThat(getSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(getSettingResponse.findValueInJson("value"), equalTo("true"));

        final HttpResponse createDocInGlobalTenantResponse = nonSslRestHelper().executePostRequest(".kibana/_doc?refresh=true", createIndexPatternDoc("globalIndex"), asAdminUser);
        assertThat(createDocInGlobalTenantResponse.getStatusCode(), equalTo(HttpStatus.SC_CREATED));
        final HttpResponse createDocInUserTenantResponse = nonSslRestHelper().executePostRequest(".kibana/_doc?refresh=true", createIndexPatternDoc("userIndex"), onUserTenant, asAdminUser);
        assertThat(createDocInUserTenantResponse.getStatusCode(), equalTo(HttpStatus.SC_CREATED));

        final HttpResponse setPrivateTenantAsDefaultResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/default_tenant", "{\"value\": \"Private\"}", asAdminUser);
        assertThat(setPrivateTenantAsDefaultResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));
        final HttpResponse updateSettingResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", "{\"value\":false}", asAdminUser);
        assertThat(updateSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_SERVER_ERROR));
        assertThat(updateSettingResponse.findValueInJson("error.reason"), containsString("Private tenant can not be disabled if it is the default tenant."));

        final HttpResponse setGlobalTenantAsDefaultResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/default_tenant", "{\"value\": \"\"}", asAdminUser);
        assertThat(setGlobalTenantAsDefaultResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));

        final HttpResponse searchInUserTenantWithPrivateTenantEnabled = nonSslRestHelper().executeGetRequest(".kibana/_search", onUserTenant, asAdminUser);
        assertThat(searchInUserTenantWithPrivateTenantEnabled.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(searchInUserTenantWithPrivateTenantEnabled.findValueInJson("hits.hits[0]._source.index-pattern.title"), equalTo("userIndex"));

        final HttpResponse updatePrivateTenantToDisabled = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", "{\"value\":false}", asAdminUser);
        assertThat(updatePrivateTenantToDisabled.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(updatePrivateTenantToDisabled.findValueInJson("value"), equalTo("false"));

        final HttpResponse disableMultiTenancyResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/multitenancy_enabled", "{\"value\":false}", asAdminUser);
        assertThat(disableMultiTenancyResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));

        final HttpResponse updatePrivateTenantEnabledFailResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", "{\"value\":false}", asAdminUser);
        assertThat(updatePrivateTenantEnabledFailResponse.getStatusCode(), equalTo(HttpStatus.SC_SERVER_ERROR));
        assertThat(updatePrivateTenantEnabledFailResponse.findValueInJson("error.reason"), containsString("Private tenant enabled configuration can not be changed if multi-tenancy is disabeld."));

    }
    @Test
    public void testForbiddenAccess() throws Exception {
        setup();

        final HttpResponse getSettingResponse = nonSslRestHelper().executeGetRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", asUser);
        assertThat(getSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_FORBIDDEN));
        assertThat(getSettingResponse.findValueInJson("error.reason"), containsString("no permissions for [cluster:feature/tenancy/private_tenant_enabled/read]"));

        final HttpResponse updateSettingResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", "{\"value\": false}", asUser);
        assertThat(updateSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_FORBIDDEN));
        assertThat(updateSettingResponse.findValueInJson("error.reason"), containsString("no permissions for [cluster:feature/tenancy/private_tenant_enabled/update]"));
    }
}
