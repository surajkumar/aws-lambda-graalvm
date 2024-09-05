package org.example.aws;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class CustomLogger implements LambdaLogger {

    @Override
    public void log(String s) {
        System.out.println(s);
    }

    @Override
    public void log(byte[] bytes) {
        log(new String(bytes));
    }
}
