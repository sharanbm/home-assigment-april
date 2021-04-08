package com.hsbc.assigment.eventbus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

final class MethodIdentifier {

    private final String name;
    private final List<Class<?>> parameterTypes;

    MethodIdentifier(Method method) {
        this.name = method.getName();
        this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MethodIdentifier) {
            MethodIdentifier identifier = (MethodIdentifier) o;
            return name.equals(identifier.name) && parameterTypes.equals(identifier.parameterTypes);
        }
        return false;
    }

}
