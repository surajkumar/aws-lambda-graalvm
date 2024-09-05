package org.example.aws;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.http.HttpResponse;

public class LambdaRuntimeLoop {
    private static final Logger LOGGER = LogManager.getLogger(LambdaRuntimeLoop.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String AWS_LAMBDA_REQUEST_ID = "Lambda-Runtime-Aws-Request-Id";
    private static final String AWS_LAMBDA_INVOKED_FUNCTION_ARN = "Lambda-Runtime-Invoked-Function-Arn";
    private static final String AWS_LAMBDA_DEADLINE_MS = "Lambda-Runtime-Invoked-Function-Arn";

    private final LambdaRuntimeHttpClient lambdaRuntimeHttpClient;
    private final RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> handler;


    public LambdaRuntimeLoop(LambdaRuntimeHttpClient lambdaRuntimeHttpClient,
                             RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> handler) {
        this.lambdaRuntimeHttpClient = lambdaRuntimeHttpClient;
        this.handler = handler;
    }

    public void run() throws IOException, InterruptedException {
        while (true) {
            HttpResponse<String> event = lambdaRuntimeHttpClient.nextInvocation();
            String requestId = event.headers().firstValue(AWS_LAMBDA_REQUEST_ID).orElseThrow();
            String functionArn = event.headers().firstValue(AWS_LAMBDA_INVOKED_FUNCTION_ARN).orElseThrow();
            try {
                APIGatewayProxyRequestEvent request = OBJECT_MAPPER.readValue(event.body(), APIGatewayProxyRequestEvent.class);
                APIGatewayProxyResponseEvent output
                        = handler.handleRequest(request, new CustomContext(requestId, functionArn, 0));
                lambdaRuntimeHttpClient.invocationResponse(requestId, writeValue(output));
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
                try {
                    lambdaRuntimeHttpClient.invocationError(requestId);
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private String writeValue(Object o) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(o);
    }
}
