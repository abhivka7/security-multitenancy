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

import org.opensearch.action.ActionType;

public class DefaultTenantRetrieveAction extends ActionType<StringSettingRetrieveResponse> {

    public static final DefaultTenantRetrieveAction INSTANCE = new DefaultTenantRetrieveAction();
    public static final String NAME = "cluster:feature/tenancy/default_tenant/read";

    protected DefaultTenantRetrieveAction() {
        super(NAME, StringSettingRetrieveResponse::new);
    }
}
