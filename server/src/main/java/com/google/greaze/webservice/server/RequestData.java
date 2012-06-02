/*
 * Copyright (C) 2011 Greaze Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.greaze.webservice.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.greaze.definition.HeaderMap;
import com.google.greaze.definition.HeaderMapSpec;
import com.google.greaze.definition.HttpMethod;
import com.google.greaze.definition.UrlParams;
import com.google.greaze.definition.UrlParamsSpec;
import com.google.greaze.definition.internal.utils.GreazeStrings;
import com.google.greaze.definition.webservice.RequestBody;
import com.google.greaze.definition.webservice.RequestBodySpec;
import com.google.greaze.definition.webservice.RequestSpec;
import com.google.greaze.definition.webservice.WebServiceRequest;
import com.google.greaze.definition.webservice.WebServiceRequestInlined;
import com.google.greaze.server.internal.utils.UrlParamsExtractor;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 * A interface that provides access to {@link HttpServletRequest} data without necessarily
 * requiring a servlet request instance. This allows for alternate implementations such as
 * those backed with {@link WebServiceRequestInlined}.
 *
 * @author Inderjeet Singh
 */
public abstract class RequestData {
  private final boolean inlined;
  private final RequestSpec spec;
  private final Gson gson;
  private final HttpMethod method;
  private final HeaderMap headers;
  private final UrlParams urlParams;
  private final RequestBody requestBody;

  public static RequestData create(HttpServletRequest request, RequestSpec spec, Gson gson)
      throws JsonParseException, IOException {
    boolean inline = getInlineParam(request);
    return inline
        ? new RequestDataInlinedBacked(request, spec, gson)
        : new RequestDataHttpBacked(request, spec, gson);
  }

  private static boolean getInlineParam(HttpServletRequest request) {
    String parameter = request.getParameter(WebServiceRequest.INLINE_URL_PARAM);
    return Boolean.parseBoolean(parameter);
  }

  private RequestData(boolean inlined, RequestSpec spec, Gson gson,
      HttpMethod method, Map<String, String> headers,
      UrlParams urlParams, RequestBody requestBody) {
    this.inlined = inlined;
    this.spec = spec;
    this.gson = gson;
    HttpMethod simulatedMethod = getSimulatedHttpMethod(headers);
    this.method = simulatedMethod != null ? simulatedMethod : method;
    this.urlParams = urlParams;
    this.requestBody = requestBody;
    this.headers = buildRequestHeaders(headers);
  }

  private static HttpMethod getSimulatedHttpMethod(Map<String, String> headers) {
    String simulatedMethod = headers.get(HttpMethod.SIMULATED_METHOD_HEADER);
    return GreazeStrings.isEmpty(simulatedMethod) ? null : HttpMethod.getMethod(simulatedMethod);
  }

  public HttpMethod getMethod() {
    return method;
  }

  public HeaderMap getHeaders() {
    return headers;
  }

  public boolean isInlined() {
    return inlined;
  }

  public UrlParams getUrlParams() {
    return urlParams;
  }

  public RequestBody requestBody() {
    return requestBody;
  }

  public boolean hasRequestBody() {
    return requestBody != null;
  }

  public RequestBody getRequestBody() {
    return requestBody;
  }

  private HeaderMap buildRequestHeaders(Map<String, String> headers) {
    HeaderMapSpec headersSpec = this.spec.getHeadersSpec();
    HeaderMap.Builder paramsBuilder = new HeaderMap.Builder(headersSpec);
    for (Map.Entry<String, Type> param : headersSpec.entrySet()) {
      String name = param.getKey();
      Type type = param.getValue();
      String header = headers.get(name);
      if (header != null) { 
        Object value = gson.fromJson(header, type);
        paramsBuilder.put(name, value);
      }
    }
    return paramsBuilder.build();
  }

  public WebServiceRequest get() {
    return new WebServiceRequest(method, headers, urlParams, requestBody, inlined);
  }

  private static final class RequestDataHttpBacked extends RequestData {
    private RequestDataHttpBacked(HttpServletRequest request, RequestSpec spec, Gson gson)
        throws JsonParseException, IOException {
      super(false, spec, gson, HttpMethod.getMethod(request.getMethod()),
          buildRequestParamsAsStringMap(request, spec.getHeadersSpec()),
          buildUrlParams(request, spec.getUrlParamsSpec(), gson),
          buildRequestBody(request, spec.getBodySpec(), gson));
    }

    private static UrlParams buildUrlParams(HttpServletRequest request, UrlParamsSpec spec, Gson gson) {
      UrlParamsExtractor urlParamsExtractor = new UrlParamsExtractor(spec, gson);
      return urlParamsExtractor.extractUrlParams(request);
    }

    private static Map<String, String> buildRequestParamsAsStringMap(HttpServletRequest request,
        HeaderMapSpec paramsSpec) {
      Map<String, String> map = new HashMap<String, String>();
      for (Map.Entry<String, Type> param : paramsSpec.entrySet()) {
        String name = param.getKey();
        String header = request.getHeader(name);
        if (header != null && !header.equals("")) {
          map.put(name, header);
        }
      }
      return map;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static RequestBody buildRequestBody(
        HttpServletRequest request, RequestBodySpec bodySpec, Gson gson) throws JsonSyntaxException {
      try {
        InputStreamReader inputJson = new InputStreamReader(request.getInputStream());
        switch (bodySpec.getContentBodyType()) {
        case SIMPLE:
          Object simpleBody = gson.fromJson(inputJson, bodySpec.getBodyJavaType());
          return new RequestBody.Builder(bodySpec)
          .setSimpleBody(simpleBody)
          .build();
        case LIST:
          List listBody = (List) gson.fromJson(inputJson, bodySpec.getBodyJavaType());
          return new RequestBody.Builder(bodySpec)
          .setListBody(listBody)
          .build();
        case MAP:
          if (bodySpec.size() == 0) {
            return new RequestBody.Builder(bodySpec).build(); // Empty body
          }
          return gson.fromJson(inputJson, RequestBody.class);
        default:
          throw new AssertionError(); 
        }
      } catch (IOException ioe) {
      } catch (JsonIOException ioe) {
      }
      // return empty body if there is an IOException. This is likely because it is a GET
      // request or a request with no body.
      return new RequestBody.Builder(bodySpec).build();
    }
  }

  private static final class RequestDataInlinedBacked extends RequestData {
    private RequestDataInlinedBacked(HttpServletRequest request, RequestSpec spec, Gson gson)
        throws IOException {
      this(extractInlineRequest(request, gson), spec, gson);
    }
    RequestDataInlinedBacked(WebServiceRequestInlined request, RequestSpec spec, Gson gson) {
      super(true, spec, gson, request.getMethod(), request.getHeaders(),
          buildUrlParams(request.getUrlParams(), spec.getUrlParamsSpec(), gson), request.getBody());
    }

    private static UrlParams buildUrlParams(Map<String, String> urlParams,
        UrlParamsSpec spec, Gson gson) {
      UrlParamsExtractor urlParamsExtractor = new UrlParamsExtractor(spec, gson);
      return urlParamsExtractor.extractUrlParams(urlParams);
    }
    private static WebServiceRequestInlined extractInlineRequest(
        HttpServletRequest request, Gson gson) throws IOException {
      return gson.fromJson(new InputStreamReader(request.getInputStream()),
          WebServiceRequestInlined.class);
    }
  }
}
