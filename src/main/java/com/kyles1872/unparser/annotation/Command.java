package com.kyles1872.unparser.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kyle
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
  public String name();

  String permission() default "";

  String[] aliases() default {};

  String description() default "";

  String usage() default "";

  boolean inGameOnly() default false;
}
