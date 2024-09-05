package org.example.aws;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.text.MessageFormat;

public class LambdaRuntimeHttpClient {
    private static final String AWS_LAMBDA_RUNTIME_API = "AWS_LAMBDA_RUNTIME_API";
    private static final String LAMBDA_VERSION_DATE = "2018-06-01";
    private static final String LAMBDA_NEXT_INVOCATION_TEMPLATE =
            "http://{0}/{1}/runtime/invocation/next";
    private static final String LAMBDA_INVOCATION_RESPONSE_URL_TEMPLATE =
            "http://{0}/{1}/runtime/invocation/{2}/response";
    private static final String LAMBDA_INVOCATION_ERROR_URL_TEMPLATE =
            "http://{0}/{1}/runtime/invocation/{2}/error";
    private static final String ERROR_RESPONSE_TEMPLATE =
            "'{'" + "\"errorMessage\": \"{0}\"," + "\"errorType\": \"{1}\"" + "'}'";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final String runtimeApi;
    private final String nextInvocationUrl;


    public LambdaRuntimeHttpClient() {
        runtimeApi = System.getenv(AWS_LAMBDA_RUNTIME_API);
        nextInvocationUrl = MessageFormat.format(LAMBDA_NEXT_INVOCATION_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE);
    }

    /**
     * Call the next invocation API to get the next event. The response body contains the event data.
     * Response headers contain the request ID and other information.
     */
    public HttpResponse<String> nextInvocation() throws IOException, InterruptedException {
        return HTTP_CLIENT.send(
                HttpRequest.newBuilder(URI.create(nextInvocationUrl)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }
    /**
     * Call the invocation response API to post the response from the handler.
     *
     * @param requestId tracing header from the Lambda-Runtime-Trace-Id header
     * @param body the body of the function output
     */
    public HttpResponse<String> invocationResponse(String requestId, String body)
            throws IOException, InterruptedException {

        String invocationResponseUrl =
                MessageFormat.format(LAMBDA_INVOCATION_RESPONSE_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);

        HttpRequest request =
                HttpRequest.newBuilder(URI.create(invocationResponseUrl))
                        .POST(BodyPublishers.ofString(body))
                        .build();

        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
    /**
     * call the invocation error API to post the error to lambda
     *
     * @param requestId tracing header from the Lambda-Runtime-Trace-Id
     */
    public HttpResponse<String> invocationError(String requestId)
            throws IOException, InterruptedException {
        String invocationErrorUrl =
                MessageFormat.format(LAMBDA_INVOCATION_ERROR_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);
        String error =
                MessageFormat.format(ERROR_RESPONSE_TEMPLATE, "Invocation Error", "RuntimeException");
        HttpRequest request =
                HttpRequest.newBuilder(URI.create(invocationErrorUrl))
                        .POST(BodyPublishers.ofString(error))
                        .build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }
}