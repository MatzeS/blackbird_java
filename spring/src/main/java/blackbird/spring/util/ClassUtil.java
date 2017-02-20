package blackbird.spring.util;

import java.util.ArrayList;
import java.util.List;

public class ClassUtil {

    public static List<Class<?>> getSuperclasses(Class<?> clazz) {
        List<Class<?>> result = new ArrayList<>();
        do {
            result.add(clazz);
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return result;
    }

}
