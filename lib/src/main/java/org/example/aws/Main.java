package org.example.aws;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;

public class Main {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    static {
        System.setProperty("software.amazon.awssdk.http.service.impl", "software.amazon.awssdk.http.urlconnection.UrlConnectionSdkHttpService");
    }

    public static void main(String[] args) throws Exception {
        LambdaRuntimeHttpClient lambdaRuntimeHttpClient = new LambdaRuntimeHttpClient();

        String handlerName = System.getenv("_HANDLER");
        LOGGER.info("Invoking handler " + handlerName);

        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> handler = getHandlerInstance(handlerName);
        LambdaRuntimeLoop lambdaRuntimeLoop = new LambdaRuntimeLoop(lambdaRuntimeHttpClient, handler);
        lambdaRuntimeLoop.run();
    }

    private static RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getHandlerInstance(String handlerName) {
        Class<?> handlerClass = loadHandlerClass(handlerName);
        Constructor<?> constructor = findZeroArgConstructor(handlerClass);
        if (constructor == null) {
            throw new RuntimeException("No zero-arg constructor");
        }
        return instantiateHandler(constructor);
    }

    private static Class<?> loadHandlerClass(String handlerName) {
        try {
            return Class.forName(handlerName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Handler not found on classpath: " + handlerName);
            throw new RuntimeException(e);
        }
    }

    private static Constructor<?> findZeroArgConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }
        LOGGER.warn("No zero-arg constructor found for " + clazz.getName());
        return null;
    }

    @SuppressWarnings("unchecked")
    private static RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> instantiateHandler(Constructor<?> constructor) {
        try {
            return (RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>) constructor.newInstance();
        } catch (Exception e) {
            LOGGER.error("Failed to construct instance of handler: ", e);
            throw new RuntimeException(e);
        }
    }
}
