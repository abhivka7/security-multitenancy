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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiTenancyRetrieveAction extends ActionType<BooleanSettingRetrieveResponse> {

    public static final MultiTenancyRetrieveAction INSTANCE = new MultiTenancyRetrieveAction();
    public static final String NAME = "cluster:feature/tenancy/multitenancy_enabled/read";
    protected final Logger log = LogManager.getLogger(this.getClass());

    protected MultiTenancyRetrieveAction() {
        super(NAME, BooleanSettingRetrieveResponse::new);
    }
}
