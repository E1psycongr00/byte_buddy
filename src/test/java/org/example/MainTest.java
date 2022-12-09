package org.example;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void newClassTest()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> toString = new ByteBuddy()
                .subclass(Object.class)
                .method(named("toString"))
                .intercept(FixedValue.value("Hello world!"))
                .make().load(getClass().getClassLoader())
                .getLoaded();
        assertThat(toString.getConstructor().newInstance().toString())
                .isEqualTo("Hello world!");


    }

    @Test
    void delegateTest()
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends Foo> loaded = new ByteBuddy()
                .subclass(Foo.class)
                .method(named("sayHelloFoo")
                        .and(isDeclaredBy(Foo.class)
                                .and(returns(String.class))))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        String s = loaded.getConstructor().newInstance().sayHelloFoo();
        assertThat(s).isEqualTo(Bar.sayHelloBar());
    }
}

