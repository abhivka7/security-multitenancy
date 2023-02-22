/*
 * Copyright 2015-2017 floragunn GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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


package org.opensearch.security.dlic.rest.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;

import org.opensearch.client.Client;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.bytes.BytesReference;
import org.opensearch.common.inject.Inject;
import org.opensearch.common.settings.Settings;
import org.opensearch.rest.RestChannel;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.RestRequest.Method;
import org.opensearch.security.auditlog.AuditLog;
import org.opensearch.security.configuration.AdminDNs;
import org.opensearch.security.configuration.ConfigurationRepository;
import org.opensearch.security.dlic.rest.validation.AbstractConfigurationValidator;
import org.opensearch.security.dlic.rest.validation.TenancyConfigValidator;
import org.opensearch.security.privileges.PrivilegesEvaluator;
import org.opensearch.security.securityconf.impl.CType;
import org.opensearch.security.securityconf.impl.SecurityDynamicConfiguration;
import org.opensearch.security.ssl.transport.PrincipalExtractor;
import org.opensearch.threadpool.ThreadPool;

import static org.opensearch.security.dlic.rest.support.Utils.addRoutesPrefix;

public class TenancyConfigAction extends PatchableResourceApiAction{

    private static final List<Route> allRoutes = new ImmutableList.Builder<Route>()
            .addAll(addRoutesPrefix(
                    ImmutableList.of(
                            new Route(Method.GET, "/tenancyconfig/"),
                            new Route(Method.PUT, "/tenancyconfig/{name}"),
                            new Route(Method.PATCH, "/tenancyconfig/")
                    )
            ))
            .build();
    private PrivilegesEvaluator evaluator;

    @Inject
    public TenancyConfigAction(Settings settings, Path configPath, RestController controller, Client client,
                               AdminDNs adminDNs, ConfigurationRepository cl, ClusterService cs,
                               PrincipalExtractor principalExtractor, PrivilegesEvaluator evaluator,
                               ThreadPool threadPool, AuditLog auditLog) {
        super(settings, configPath, controller, client, adminDNs, cl, cs, principalExtractor, evaluator, threadPool, auditLog);
        this.evaluator = evaluator;

    }

    @Override
    public List<Route> routes() {
        return allRoutes;
    }

    @Override
    protected void handleGet(RestChannel channel, RestRequest request, Client client, final JsonNode content) throws IOException{

        final SecurityDynamicConfiguration<?> configuration = load(getConfigName(), true);
        filter(configuration);
        successResponse(channel,configuration);
    }


    @Override
    protected AbstractConfigurationValidator getValidator(RestRequest request, BytesReference ref, Object... params) {
        return new TenancyConfigValidator(request, ref, this.settings, this.evaluator, params);
    }

    @Override
    protected void handleDelete(RestChannel channel, final RestRequest request, final Client client, final JsonNode content) throws IOException{
        notImplemented(channel, Method.DELETE);
    }

    @Override
    protected void handlePut(RestChannel channel, final RestRequest request, final Client client, final JsonNode content) throws IOException{
        successResponse(channel);
    }

    @Override
    protected String getResourceName() {
        return null;
    }

    @Override
    protected CType getConfigName() {
        return CType.TENANCYCONFIG;
    }

    @Override
    protected boolean hasPermissionsToCreate(final SecurityDynamicConfiguration<?> dynamicConfigFactory,
                                             final Object content, final String resourceName) {
        return true;
    }

    @Override
    protected Endpoint getEndpoint() {
        return Endpoint.TENANCYCONFIG;
    }
}
