package blackbird.core.rmi;

import com.google.common.base.Joiner;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Just a helper class wrapping up Methods, because Method does not implement Serializable.
 */
public class MethodIdentifier implements Serializable {

    private static final long serialVersionUID = 4379130643195548114L;
    private Class<?> declaringClass;
    private String name;
    private Class<?>[] parameterTypes;

    public MethodIdentifier(Method method) {
        declaringClass = method.getDeclaringClass();
        name = method.getName();
        parameterTypes = method.getParameterTypes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodIdentifier that = (MethodIdentifier) o;

        if (declaringClass != null ? !declaringClass.equals(that.declaringClass) : that.declaringClass != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(parameterTypes, that.parameterTypes);
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public String getName() {
        return name;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public int hashCode() {
        int result = declaringClass != null ? declaringClass.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(parameterTypes);
        return result;
    }

    @Override
    public String toString() {
        return declaringClass.getName() + "." + name + "(" + Joiner.on(", ").join(Arrays.stream(parameterTypes).map(Class::getName)
                .collect(Collectors.toList())) + ")";
    }
}
