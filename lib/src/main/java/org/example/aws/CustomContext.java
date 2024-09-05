package org.example.aws;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class CustomContext implements Context {
    private final CustomLogger logger = new CustomLogger();
    private final String requestId;
    private final String functionArn;
    private final long deadline;

    public CustomContext(String requestId, String functionArn, long deadline) {
        this.requestId = requestId;
        this.functionArn = functionArn;
        this.deadline = deadline;
    }

    @Override
    public String getAwsRequestId() {
        return requestId;
    }

    @Override
    public String getLogGroupName() {
        return System.getenv("AWS_LAMBDA_LOG_GROUP_NAME");
    }

    @Override
    public String getLogStreamName() {
        return System.getenv("AWS_LAMBDA_LOG_STREAM_NAME");
    }

    @Override
    public String getFunctionName() {
        return System.getenv("AWS_LAMBDA_FUNCTION_NAME");
    }

    @Override
    public String getFunctionVersion() {
        return System.getenv("AWS_LAMBDA_FUNCTION_VERSION");
    }

    @Override
    public String getInvokedFunctionArn() {
        return functionArn;
    }

    @Override
    public CognitoIdentity getIdentity() {
        return null;
    }

    @Override
    public ClientContext getClientContext() {
        return null;
    }

    @Override
    public int getRemainingTimeInMillis() {
        return (int) (deadline - Math.floor((double) System.currentTimeMillis() / 1000));
    }

    @Override
    public int getMemoryLimitInMB() {
        return Integer.parseInt(System.getenv("AWS_LAMBDA_FUNCTION_MEMORY_SIZE"));
    }

    @Override
    public LambdaLogger getLogger() {
        return logger;
    }
}
