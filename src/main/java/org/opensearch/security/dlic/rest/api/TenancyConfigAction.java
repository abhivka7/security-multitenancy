package org.opensearch.security.dlic.rest.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
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
import org.opensearch.security.dlic.rest.validation.SecurityConfigValidator;
import org.opensearch.security.privileges.PrivilegesEvaluator;
import org.opensearch.security.securityconf.impl.CType;
import org.opensearch.security.securityconf.impl.SecurityDynamicConfiguration;
import org.opensearch.security.ssl.transport.PrincipalExtractor;
import org.opensearch.security.support.ConfigConstants;
import org.opensearch.threadpool.ThreadPool;

import org.apache.logging.log4j.LogManager;  // ******** Delete this later.
import org.apache.logging.log4j.Logger;   // ******** Delete this later.

import static org.opensearch.security.dlic.rest.support.Utils.addRoutesPrefix;

public class TenancyConfigAction extends PatchableResourceApiAction{
    protected final Logger log = LogManager.getLogger(this.getClass());

    private static final List<Route> allRoutes = new ImmutableList.Builder<Route>()
            .addAll(addRoutesPrefix(
                    ImmutableList.of(
                            new Route(Method.GET, "/tenancyconfig/"),
                            new Route(Method.PUT, "/tenancyconfig/"),
                            new Route(Method.PATCH, "/tenancyconfig/")
                    )
            ))
            .build();

    @Inject
    public TenancyConfigAction(Settings settings, Path configPath, RestController controller, Client client,
                               AdminDNs adminDNs, ConfigurationRepository cl, ClusterService cs,
                               PrincipalExtractor principalExtractor, PrivilegesEvaluator evaluator,
                               ThreadPool threadPool, AuditLog auditLog) {
        super(settings, configPath, controller, client, adminDNs, cl, cs, principalExtractor, evaluator, threadPool, auditLog);
        log.info("************** Tenancy_abhivka Enter TenancyConfigAction");
    }

    @Override
    public List<Route> routes() {
//        if(adminDNs.isAdmin())
//        {
//            return allRoutes;
//        }
//
//        return Collections.emptyList();
        return allRoutes;
    }

    @Override
    protected void handleGet(RestChannel channel, RestRequest request, Client client, final JsonNode content) throws IOException{

        final SecurityDynamicConfiguration<?> configuration = load(getConfigName(), true);

        log.info("************** Tenancy_abhivka Enter handleget, configuration : " + configuration);

        filter(configuration);

        successResponse(channel,configuration);
    }


    @Override
    protected AbstractConfigurationValidator getValidator(RestRequest request, BytesReference ref, Object... params) {
        return new TenancyConfigValidator(request, ref, this.settings, params);
    }

    @Override
    protected void handleDelete(RestChannel channel, final RestRequest request, final Client client, final JsonNode content) throws IOException{
        notImplemented(channel, Method.DELETE);
    }

    @Override
    protected void handlePut(RestChannel channel, final RestRequest request, final Client client, final JsonNode content) throws IOException{
        log.info("************** Tenancy_abhivka Enter handleput.");
        successResponse(channel);
    }

    @Override
    protected String getResourceName() {
        return null;
    }

    @Override
    protected CType getConfigName() {
        log.info("************** Tenancy_abhivka Enter getConfiGName.");
        return CType.TENANCYCONFIG;
    }

    @Override
    protected boolean hasPermissionsToCreate(final SecurityDynamicConfiguration<?> dynamicConfigFactory,
                                             final Object content,
                                             final String resourceName) {
        return true;
    }

    @Override
    protected Endpoint getEndpoint() {
        return Endpoint.TENANCYCONFIG;
    }
}
