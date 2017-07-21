package blackbird.core.util;

import java.util.function.Predicate;

public class TypeFilter<T> implements Predicate<T> {

    private Class<?> type;


    public TypeFilter(Class<?> type) {

        super();
        this.type = type;
    }


    @Override
    public boolean test(T t) {

        return type.isAssignableFrom(t.getClass());
    }

}
