package blackbird.core.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

public class MultiException {

    public static String generateMultipleExceptionText(List<? extends Exception> exceptions, List<String> headlines) {
        String text = "\n";
        for (int i = 0; i < exceptions.size(); i++) {
            text += headlines.get(i) + ": ";
            text += ExceptionUtils.getStackTrace(exceptions.get(i));
            text += "\n\n";
        }
        return text;
    }

    public static String generateMultipleExceptionText(List<? extends Exception> exceptions) {
        String text = "\n";
        for (Exception exception : exceptions) {
            String lines = ExceptionUtils.getStackTrace(exception);
            text += lines.replace("\n", "\n\t");
            text += "\n\n";
        }
        return text;
    }

}
