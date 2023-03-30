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
import java.util.Objects;

import org.opensearch.action.ActionRequest;
import org.opensearch.action.ActionRequestValidationException;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.core.ParseField;
import org.opensearch.core.xcontent.ConstructingObjectParser;
import org.opensearch.core.xcontent.XContentParser;

public class StringSettingUpdateRequest extends ActionRequest {

    private String value;

    public StringSettingUpdateRequest(final StreamInput in) throws IOException {
        super(in);
        in.readString();
    }

    public StringSettingUpdateRequest(final String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public ActionRequestValidationException validate() {
        if (getValue() == null || Objects.equals(getValue(), "Abhi1")) {
            final ActionRequestValidationException validationException = new ActionRequestValidationException();
            validationException.addValidationError("Missing string value");
            return validationException;
        }
        return null;
    }

    private static final ConstructingObjectParser<StringSettingUpdateRequest, Void> PARSER = new ConstructingObjectParser<>(
            StringSettingUpdateRequest.class.getName(),
            args -> new StringSettingUpdateRequest((String) args[0])
    );

    static {
        PARSER.declareString(ConstructingObjectParser.constructorArg(), new ParseField("value"));
    }

    public static StringSettingUpdateRequest fromXContent(final XContentParser parser) {
        return PARSER.apply(parser, null);
    }
}
