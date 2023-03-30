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

import org.opensearch.action.ActionResponse;
import org.opensearch.common.Strings;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;

public class StringSettingRetrieveResponse extends ActionResponse implements ToXContentObject {

    private String value;

    public StringSettingRetrieveResponse(final StreamInput in) throws IOException {
        super(in);
        this.value = in.readString();
    }

    public StringSettingRetrieveResponse(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public void writeTo(final StreamOutput out) throws IOException {
        out.writeString(getValue());
    }

    @Override
    public XContentBuilder toXContent(final XContentBuilder builder, final Params params) throws IOException {
        builder.startObject();
        builder.field("value", getValue());
        builder.endObject();
        return builder;
    }
}
