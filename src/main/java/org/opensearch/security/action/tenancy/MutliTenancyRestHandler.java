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

import com.google.common.collect.ImmutableList;

import org.opensearch.client.node.NodeClient;
import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.RestToXContentListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.opensearch.rest.RestRequest.Method.GET;
import static org.opensearch.rest.RestRequest.Method.PUT;

public class MutliTenancyRestHandler extends BaseRestHandler {

    protected final Logger log = LogManager.getLogger(this.getClass());

    public MutliTenancyRestHandler() {
        super();
    }

    @Override
    public String getName() {
        return "Multi Tenancy actions for Retrieve / Update";
    }
    
    @Override
    public List<Route> routes() {
        return ImmutableList.of(
            new Route(GET, "/_plugins/_security/api/tenancy/multitenancy_enabled"),
            new Route(PUT, "/_plugins/_security/api/tenancy/multitenancy_enabled")
        );
    }

    @Override
    protected RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient nodeClient) throws IOException {

        switch (request.method()) {
            case GET:
                return channel -> nodeClient.execute(
                    MultiTenancyRetrieveAction.INSTANCE,
                    new EmptyRequest(),
                    new RestToXContentListener<>(channel));
            case PUT: 
                return channel -> nodeClient.execute(
                    MultiTenancyUpdateAction.INSTANCE,
                    BooleanSettingUpdateRequest.fromXContent(request.contentParser()),
                    new RestToXContentListener<>(channel));
            default:
                throw new RuntimeException("Not implemented");
        }
    }
}
