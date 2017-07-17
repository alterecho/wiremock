package com.github.tomakehurst.wiremock.admin;

import com.github.tomakehurst.wiremock.admin.model.CaptureHeadersSpec;
import com.github.tomakehurst.wiremock.admin.model.RequestPatternTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.junit.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.matching.MockRequest.mockRequest;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.junit.Assert.assertEquals;

public class RequestPatternTransformerTest {
    @Test
    public void applyWithDefaultsAndNoBody() {
        Request request = mockRequest()
            .url("/foo")
            .method(RequestMethod.GET)
            .header("User-Agent", "foo")
            .header("X-Foo", "bar");
        RequestPatternBuilder expected = new RequestPatternBuilder(RequestMethod.GET, urlEqualTo("/foo"));

        // Default is to include method and URL exactly
        assertEquals(expected.build(), new RequestPatternTransformer(null, null).apply(request).build());
    }

    @Test
    public void applyWithUrlAndPlainTextBody() {
        Request request = mockRequest()
            .url("/foo")
            .method(RequestMethod.GET)
            .body("HELLO")
            .header("Accept", "foo")
            .header("User-Agent", "bar");

        RequestPatternBuilder expected = new RequestPatternBuilder(RequestMethod.GET, urlEqualTo("/foo"))
            .withRequestBody(equalTo("HELLO"));

        Map<String, CaptureHeadersSpec> headers = newLinkedHashMap();

        assertEquals(expected.build(), new RequestPatternTransformer(headers, null).apply(request).build());
    }

    @Test
    public void applyWithOnlyJsonBody() {
        Request request = mockRequest()
            .url("/somewhere")
            .header("Content-Type", "application/json")
            .body("['hello']");
        RequestPatternBuilder expected = new RequestPatternBuilder()
            .withUrl("/somewhere")
            .withRequestBody(equalToJson("['hello']"));

        assertEquals(expected.build(), new RequestPatternTransformer(null, null).apply(request).build());
    }

    @Test
    public void applyWithOnlyXmlBody() {
        Request request = mockRequest()
            .url("/somewhere")
            .header("Content-Type", "application/xml")
            .body("<foo/>");

        RequestPatternBuilder expected = new RequestPatternBuilder()
            .withUrl("/somewhere")
            .withRequestBody(equalToXml("<foo/>"));

        assertEquals(expected.build(), new RequestPatternTransformer(null, null).apply(request).build());
    }
}
