package me.zombie_striker.omeggajava.events;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    EventPriority priority() default EventPriority.NORMAL;
}
