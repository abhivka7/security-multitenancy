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

package org.opensearch.security.securityconf.impl.v7;

import com.fasterxml.jackson.annotation.JsonInclude;

public class TenancyConfigV7 {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public boolean multitenancy_enabled = true;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public boolean private_tenant_enabled = true;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String default_tenant = "";

    @Override
    public String toString() {
        return "TenancyConfigV7 [multitenancy_enabled=" + multitenancy_enabled + ", private_tenant_enabled=" + private_tenant_enabled + ", default_tenant=" + default_tenant + "]";
    }

}
