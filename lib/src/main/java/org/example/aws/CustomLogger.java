package org.example.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class CustomLogger implements LambdaLogger {
    private static final Logger LOGGER = LogManager.getLogger(Context.class);

    @Override
    public void log(String s) {
        LOGGER.info(s);
    }

    @Override
    public void log(byte[] bytes) {
        log(new String(bytes));
    }
}
