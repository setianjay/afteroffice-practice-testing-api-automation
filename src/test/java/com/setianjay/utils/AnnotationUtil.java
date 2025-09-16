package com.setianjay.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtil {

    public static <T extends Annotation> T findClassAnnotation(Class<?> clazz, Class<T> annotationClass){
        return clazz.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T findMethodAnnotation(Class<?> clazz, String methodName, Class<T> annotationClass) throws NoSuchMethodException {
        Method method = clazz.getMethod(methodName);
        return method.getAnnotation(annotationClass);
    }
}
