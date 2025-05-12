package org.example.yhw.bookstorecrud.query.annotations;

import org.example.yhw.bookstorecrud.query.enums.Fetch;
import org.example.yhw.bookstorecrud.query.enums.Join;
import org.example.yhw.bookstorecrud.query.enums.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zin Ko Win
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    String propName() default "";

    Type type() default Type.EQUAL;


    /**
     * The attribute name of the connection query, such as userRole in the User class
     */
    String joinName() default "";

    /**
     * Default left connection
     */
    Join join() default Join.LEFT;

    /**
     * The attribute is to fetch lazy object
     */
    Fetch fetchType() default Fetch.NONE;

    /**
     * Multi-field fuzzy search, only supports String type fields, multiple separated by commas, such as @Query (blurry = "email, username")
     */
    String blurry() default "";

    boolean distinct() default false;
}

