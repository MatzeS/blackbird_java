package blackbird.core.util;

import java.util.List;

public class MultiRuntimeException extends RuntimeException {
    private List<Exception> exceptions;


    public MultiRuntimeException(String message, List<Exception> exceptions) {

        super(message);
        this.exceptions = exceptions;
    }


    public MultiRuntimeException(List<Exception> exceptions) {

        this.exceptions = exceptions;
    }


    public List<Exception> getExceptions() {

        return exceptions;
    }
}
