/**
 * 
 */
package com.jzb.an;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author n63636
 * 
 */
@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface MSFComponent {

    String namespace() default "";

    String name() default "";

    boolean deprecated() default false;

    MSFEvent[] events() default {};

    String documentation() default "";
}
