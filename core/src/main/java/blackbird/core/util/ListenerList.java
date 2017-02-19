package blackbird.core.util;


import java.util.ArrayList;
import java.util.function.Consumer;

public class ListenerList<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public void fire(Consumer<T> consumer) {
        Object[] array = this.toArray();
        for (Object e : array)
            new Thread() {

                public void run() {
                    consumer.accept((T) e);
                }
            }.start();
    }

}
