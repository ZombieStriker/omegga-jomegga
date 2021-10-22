package me.zombie_striker.omeggajava.plugins;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface OmeggaMain {

    String pluginname();
    String description() default "";
    String author();
}
