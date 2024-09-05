package org.example.aws;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.lang.reflect.Constructor;

public class Main {
    public static void main(String[] args) throws Exception {
        LambdaRuntimeHttpClient lambdaRuntimeHttpClient = new LambdaRuntimeHttpClient();

        String handlerName = System.getenv("_HANDLER");
        System.out.println("Invoking handler " + handlerName);

        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> handler = getHandlerInstance(handlerName);
        if (handler == null) {
            System.out.println("Handler is null");
            return;
        }

        LambdaRuntimeLoop lambdaRuntimeLoop = new LambdaRuntimeLoop(lambdaRuntimeHttpClient, handler);
        lambdaRuntimeLoop.run();
    }

    private static RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getHandlerInstance(String handlerName) {
        Class<?> handlerClass = loadHandlerClass(handlerName);
        if (handlerClass == null) {
            return null;
        }
        Constructor<?> constructor = findZeroArgConstructor(handlerClass);
        if (constructor == null) {
            System.out.println("No zero-arg constructor found for " + handlerName);
            return null;
        }

        return instantiateHandler(constructor);
    }

    private static Class<?> loadHandlerClass(String handlerName) {
        try {
            return Class.forName(handlerName);
        } catch (ClassNotFoundException e) {
            System.out.println("Handler not found: " + e.getMessage());
            return null;
        }
    }

    private static Constructor<?> findZeroArgConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return constructor;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> instantiateHandler(Constructor<?> constructor) {
        try {
            return (RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>) constructor.newInstance();
        } catch (Exception e) {
            System.out.println("Failed to construct handler: " + e.getMessage());
            return null;
        }
    }
}
