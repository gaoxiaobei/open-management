package com.openmanagement.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermission {
    /**
     * The alias of dept_id column in the query, default "dept_id"
     */
    String deptAlias() default "";

    /**
     * The alias of user_id column in the query, default "created_by"
     */
    String userAlias() default "";
}
