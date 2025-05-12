package org.example.yhw.bookstorecrud.utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * A utility class for performing reflection-related operations, such as finding fields
 * and retrieving values from objects using reflection and getter methods.
 * <p>
 * This class is not meant to be instantiated.
 * </p>
 *
 * @author Zin Ko Win
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Finds a field within a class hierarchy based on the provided field name.
     *
     * @param clazz     the class containing the field
     * @param fieldName the name of the field to find. Supports nested fields using dot notation.
     * @return the Field object representing the found field, or null if not found.
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        if (clazz == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        try {
            String[] fieldNames = fieldName.split("\\.");
            Field field = null;
            for (String fName : fieldNames) {
                field = clazz.getDeclaredField(fName);
                clazz = field.getType();
            }
            return field;
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return findField(superclass, fieldName);
            }
            return null;
        }
    }

    /**
     * Retrieves the value of a nested field from an object using reflection and getter methods.
     *
     * @param item      the object from which to retrieve the field value.
     * @param fieldName the name of the nested field to retrieve. Supports dot notation for nested fields.
     * @return the value of the nested field, or null if any exceptions occurs during retrieval.
     */
    public static Object getValueByFieldName(Object item, String fieldName) {
        if (item == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        try {
            Object fieldValue = item;
            Class<?> itemClass = item.getClass();

            String[] fieldNames = fieldName.split("\\.");
            for (String nestedFieldName : fieldNames) {
                Method getter = findGetterMethod(itemClass, nestedFieldName);
                if (getter == null) {
                    throw new NoSuchMethodException("Getter method not found for field: " + nestedFieldName);
                }
                fieldValue = getter.invoke(fieldValue);
                itemClass = getter.getReturnType();
            }

            return fieldValue;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recursively finds a getter method within a class hierarchy based on the provided field name.
     *
     * @param clazz     the class containing the getter method
     * @param fieldName the name of the field to find the getter for
     * @return the Method object representing the found getter method, or null if not found
     */
    private static Method findGetterMethod(Class<?> clazz, String fieldName) {
        String getterName = "get" + capitalize(fieldName);
        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return findGetterMethod(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }

    /**
     * Retrieves a {@code Field} object that reflects the specified declared field of the given class or any of its superclasses.
     *
     * @param clazz     the {@code Class} object in which the field is declared
     * @param fieldName the name of the field
     * @return the {@code Field} object for the specified field, or {@code null} if the field does not exist in the class or any of its superclasses
     */
    private static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            return null;
        }
    }

    /**
     * Retrieves the names of fields declared in a given class.
     *
     * @param data the class for which to retrieve field names
     * @return a List of field names declared in the class
     */
    public static List<Field> getFields(Class<?> data) {
        return Arrays.stream(data.getDeclaredFields()).collect(Collectors.toList());
    }

    /**
     * Retrieves fields from a class based on their names.
     *
     * @param clazz      the class to retrieve fields from
     * @param fieldNames an array of field names to retrieve
     * @return a list of fields with the specified names that exist in the class
     */
    public static List<Field> getFieldsByNamesFromClass(Class<?> clazz, String[] fieldNames) {
        return Arrays.stream(fieldNames)
                .map(fieldName -> getFieldIfExists(clazz, fieldName))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Attempts to retrieve a field from a class if it exists.
     *
     * @param clazz     the class to retrieve the field from
     * @param fieldName the name of the field to retrieve
     * @return the Field object if it exists in the class; otherwise, null
     */
    private static Field getFieldIfExists(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Creates a new instance of a given class using its default constructor.
     *
     * @param <T>  the type of the object to be created
     * @param type the Class object representing the type to instantiate
     * @return a new instance of the specified class
     * @throws IllegalStateException if an error occurs while creating the instance
     */
    public static <T> T newInstanceOf(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot create a new instance of " + type.getName(), ex);
        }
    }

    /**
     * Sets the value of a specified field in an object instance.
     *
     * @param fieldName the name of the field to set
     * @param value     the new value to assign to the field
     * @param instance  the instance containing the field to be updated
     * @throws IllegalStateException    if an error occurs while generalsetting the field value
     * @throws IllegalArgumentException if the field is not found
     */
    public static void setFieldData(String fieldName, Object value, Object instance) {
        Field field = getFieldIfExists(instance.getClass(), fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to set field value", e);
            }
        } else {
            throw new IllegalArgumentException("Field not found: " + fieldName);
        }
    }

    /**
     * Capitalizes the first character of a string.
     *
     * @param s the input string
     * @return the input string with its first character capitalized
     */
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
