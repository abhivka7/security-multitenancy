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

package org.opensearch.security.action.tenancy;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.OpenSearchSecurityException;
import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.RestToXContentListener;
import org.opensearch.security.privileges.PrivilegesEvaluator;

import static org.opensearch.rest.RestRequest.Method.GET;
import static org.opensearch.rest.RestRequest.Method.PUT;

public class PrivateTenantEnabledRestHandler extends BaseRestHandler {

    private final PrivilegesEvaluator evaluator;

    public PrivateTenantEnabledRestHandler(PrivilegesEvaluator evaluator) {

        super();
        this.evaluator = evaluator;
    }

    @Override
    public String getName() {
        return "Multi Tenancy actions for Retrieve / Update private_tenant_enabled field.";
    }

    @Override
    public List<Route> routes() {
        return ImmutableList.of(
                new Route(GET, "/_plugins/_security/api/tenancy/private_tenant_enabled"),
                new Route(PUT, "/_plugins/_security/api/tenancy/private_tenant_enabled")
        );
    }

    public void validate(final RestRequest request) throws Exception {

        boolean isMultitenancyEnabled = this.evaluator.multitenancyEnabled();

        if( !isMultitenancyEnabled ) {
            throw new OpenSearchSecurityException("Private tenant enabled configuration can not be changed if multi-tenancy is disabeld.");
        }

        JSONParser parser = new JSONParser();
        JSONObject request_content = (JSONObject) parser.parse(request.content().utf8ToString());

        boolean isPrivateTenantEnabled = (boolean) request_content.get("value") ;

        String default_tenant = this.evaluator.dashboardsDefaultTenant();

        if(default_tenant.equals("Private") && !isPrivateTenantEnabled ) {
            throw new OpenSearchSecurityException("Private tenant can not be disabled if it is the default tenant.");
        }
    }

    @Override
    protected RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient nodeClient) throws IOException {

        switch (request.method()) {
            case GET:
                return channel -> nodeClient.execute(
                        PrivateTenantEnabledRetrieveAction.INSTANCE,
                        new EmptyRequest(),
                        new RestToXContentListener<>(channel));
            case PUT:
                try {
                    validate(request);
                } catch (Exception e)
                {
                    throw new OpenSearchSecurityException(e.getMessage());
                }
                return channel -> nodeClient.execute(
                        PrivateTenantEnabledUpdateAction.INSTANCE,
                        BooleanSettingUpdateRequest.fromXContent(request.contentParser()),
                        new RestToXContentListener<>(channel));
            default:
                throw new RuntimeException("Not implemented");
        }
    }
}
