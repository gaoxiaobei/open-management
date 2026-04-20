package com.openmanagement.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {
    /** Module name */
    String module() default "";
    /** Operation name */
    String name() default "";
}
