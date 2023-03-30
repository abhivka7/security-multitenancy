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

public class TenancyDefaultTenantTests extends SingleClusterTest {
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
    public void testDefaultTenant_endToEndTest() throws Exception {
        setup();

        final HttpResponse getSettingResponse = nonSslRestHelper().executeGetRequest("/_plugins/_security/api/tenancy/default_tenant", asAdminUser);
        assertThat(getSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(getSettingResponse.findValueInJson("value"), equalTo(""));

        final HttpResponse disablePrivateTenantResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", "{\"value\":false}", asAdminUser);
        assertThat(disablePrivateTenantResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));



        final HttpResponse setPrivateTenantAsDefaultFailResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/default_tenant", "{\"value\": \"Private\"}", asAdminUser);
        assertThat(setPrivateTenantAsDefaultFailResponse.getStatusCode(), equalTo(HttpStatus.SC_SERVER_ERROR));
        assertThat(setPrivateTenantAsDefaultFailResponse.findValueInJson("error.reason"), containsString("Default tenant can not be set to Private if Private tenant is disabled."));

        final HttpResponse enablePrivateTenantResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/private_tenant_enabled", "{\"value\":true}", asAdminUser);
        assertThat(enablePrivateTenantResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));

        final HttpResponse setPrivateTenantAsDefaultResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/default_tenant", "{\"value\": \"Private\"}", asAdminUser);
        assertThat(setPrivateTenantAsDefaultResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));

        final HttpResponse getSettingResponseAfterUpdate = nonSslRestHelper().executeGetRequest("/_plugins/_security/api/tenancy/default_tenant", asAdminUser);
        assertThat(getSettingResponseAfterUpdate.getStatusCode(), equalTo(HttpStatus.SC_OK));
        assertThat(getSettingResponseAfterUpdate.findValueInJson("value"), equalTo("Private"));

        final HttpResponse disableMultiTenancyResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/multitenancy_enabled", "{\"value\":false}", asAdminUser);
        assertThat(disableMultiTenancyResponse.getStatusCode(), equalTo(HttpStatus.SC_OK));

        final HttpResponse setDefaultTenantFailResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/default_tenant", "{\"value\": \"Private\"}", asAdminUser);
        assertThat(setDefaultTenantFailResponse.getStatusCode(), equalTo(HttpStatus.SC_SERVER_ERROR));
        assertThat(setDefaultTenantFailResponse.findValueInJson("error.reason"), containsString("Default tenant can  not be set if multi-tenancy is disabled."));




    }
    @Test
    public void testForbiddenAccess() throws Exception {
        setup();

        final HttpResponse getSettingResponse = nonSslRestHelper().executeGetRequest("/_plugins/_security/api/tenancy/default_tenant", asUser);
        assertThat(getSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_FORBIDDEN));
        assertThat(getSettingResponse.findValueInJson("error.reason"), containsString("no permissions for [cluster:feature/tenancy/default_tenant/read]"));

        final HttpResponse updateSettingResponse = nonSslRestHelper().executePutRequest("/_plugins/_security/api/tenancy/default_tenant", "{\"value\": \"Private\"}", asUser);
        assertThat(updateSettingResponse.getStatusCode(), equalTo(HttpStatus.SC_FORBIDDEN));
        assertThat(updateSettingResponse.findValueInJson("error.reason"), containsString("no permissions for [cluster:feature/tenancy/default_tenant/update]"));
    }
}
