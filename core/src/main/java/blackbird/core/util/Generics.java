package blackbird.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Generics {

    public static Class<?> getGenericArgument(Object obj, int argNum) {
        return getGenericArgument(obj.getClass(), argNum);
    }

    public static Class<?> getGenericArgument(Class<?> clazz, int argNum) {
        Type type = ((ParameterizedType) clazz.getGenericSuperclass())
                .getActualTypeArguments()[argNum];
        if (type instanceof ParameterizedType)
            return (Class<?>) ((ParameterizedType) type).getRawType();
        else
            return (Class<?>) type;
    }

}
