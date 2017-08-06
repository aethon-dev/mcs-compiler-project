package com.mcs1205.semantic.warning;

/**
 * Created by kasunp on 8/5/17.
 */
public class TruncationWarning implements Warning {

    String message;

    public TruncationWarning(String message) {

    }

    @Override
    public String getMessage() {
        return message;
    }
}
