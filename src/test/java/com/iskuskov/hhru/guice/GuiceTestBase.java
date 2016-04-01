package com.iskuskov.hhru.guice;

import com.google.inject.Injector;

import static com.google.inject.Guice.createInjector;

public class GuiceTestBase {

    private static final Injector injector = createInjector(new GuiceCommonModule(), new GuiceTestOnlyModule());

    protected static <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }
}
