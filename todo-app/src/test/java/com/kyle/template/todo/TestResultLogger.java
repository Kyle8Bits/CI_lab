package com.kyle.template.todo;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestResultLogger implements TestWatcher {

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("\u001B[32m" + context.getDisplayName() + " : PASS");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("\u001B[31m" + context.getDisplayName() + " : FAIL");
    }
}
