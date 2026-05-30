package common.annotations;

import common.extensions.AccountSessionExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(AccountSessionExtension.class)
public @interface AccountSession {
    int value() default 1;

    int deposit() default 1;
}
