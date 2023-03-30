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
import net.minidev.json.JSONObject;
import com.google.common.collect.ImmutableList;
import net.minidev.json.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.OpenSearchSecurityException;
import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.RestToXContentListener;
import org.opensearch.security.privileges.PrivilegesEvaluator;
import org.opensearch.action.ActionRequestValidationException;

import static org.opensearch.rest.RestRequest.Method.GET;
import static org.opensearch.rest.RestRequest.Method.PUT;

public class DefaultTenantRestHandler extends BaseRestHandler {

    protected final Logger log = LogManager.getLogger(this.getClass());

    private final PrivilegesEvaluator evaluator;

    public DefaultTenantRestHandler(PrivilegesEvaluator evaluator) {
        super();
        this.evaluator = evaluator;
    }

    @Override
    public String getName() {
        return "Multi Tenancy actions for Retrieve / Update default tenant.";
    }

    @Override
    public List<Route> routes() {
        return ImmutableList.of(
                new Route(GET, "/_plugins/_security/api/tenancy/default_tenant"),
                new Route(PUT, "/_plugins/_security/api/tenancy/default_tenant")
        );
    }

    public void validate(final RestRequest request) throws Exception {

        boolean isPrivateTenantEnabled = this.evaluator.privateTenantEnabled();
        boolean isMultitenancyEnabled = this.evaluator.multitenancyEnabled();

        if( !isMultitenancyEnabled ) {
            throw new OpenSearchSecurityException("Default tenant can  not be set if multi-tenancy is disabled.");
        }

        JSONParser parser = new JSONParser();
        JSONObject request_content = (JSONObject) parser.parse(request.content().utf8ToString());

        String default_tenant = (String) request_content.get("value");

        if(default_tenant.equals(""))
        {
            return;
        }

        if(default_tenant.equals("Private")) {
            if(!isPrivateTenantEnabled)
            {
                throw new OpenSearchSecurityException("Default tenant can not be set to Private if Private tenant is disabled.");
            }
            return;
        }

        Set<String> availableTenants = this.evaluator.getAllConfiguredTenantNames();

        if(!availableTenants.contains(default_tenant))
        {
            throw new OpenSearchSecurityException("Default tenant can only be from one of the available tenants.");
        }

    }

    @Override
    protected RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient nodeClient) throws IOException {

        switch (request.method()) {
            case GET:
                return channel -> nodeClient.execute(
                        DefaultTenantRetrieveAction.INSTANCE,
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
                        DefaultTenantUpdateAction.INSTANCE,
                        StringSettingUpdateRequest.fromXContent(request.contentParser()),
                        new RestToXContentListener<>(channel));
            default:
                throw new RuntimeException("Not implemented");
        }
    }



}
