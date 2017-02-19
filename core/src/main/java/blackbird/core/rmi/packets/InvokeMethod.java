package blackbird.core.rmi.packets;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import blackbird.core.Packet;
import blackbird.core.rmi.MethodIdentifier;

public class InvokeMethod {

    public static class Error extends Reply {

        private static final long serialVersionUID = -5799600105629333882L;
        private Exception exception;
        private String message;

        public Error(Request request, String message) {
            this(request, message, null);
        }

        public Error(Request request, String message, Exception exception) {
            super(request, null);
            this.message = message;
            this.exception = exception;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Error error = (Error) o;

            return exception != null ? exception.equals(error.exception) : error.exception == null;

        }

        public Exception getException() {
            return exception;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (exception != null ? exception.hashCode() : 0);
            return result;
        }
    }

    public static class Reply extends Packet {

        private static final long serialVersionUID = -7250331264313186033L;
        private Request originRequest;

        private Serializable result;

        public Reply(Request originRequest, Serializable result) {
            this.result = result;
            this.originRequest = originRequest;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Reply reply = (Reply) o;
            return Objects.equals(originRequest, reply.originRequest) &&
                    Objects.equals(result, reply.result);
        }

        public Request getOriginRequest() {
            return originRequest;
        }

        public Object getResult() {
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hash(originRequest, result);
        }

    }

    public static class Request extends Packet {

        private static final long serialVersionUID = 6800070873977564752L;

        private String namespace;
        private int objectID;
        private MethodIdentifier methodIdentifier;
        private Serializable[] args;

        public Request(String namespace, int objectID, MethodIdentifier methodIdentifier, Serializable[] args) {
            this.namespace = namespace;
            this.objectID = objectID;
            this.methodIdentifier = methodIdentifier;
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Request request = (Request) o;

            if (objectID != request.objectID) return false;
            if (methodIdentifier != null ? !methodIdentifier.equals(request.methodIdentifier) : request.methodIdentifier != null)
                return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(args, request.args);

        }

        public Object[] getArgs() {
            return args;
        }

        public MethodIdentifier getMethodIdentifier() {
            return methodIdentifier;
        }

        public String getNamespace() {
            return namespace;
        }

        public int getObjectID() {
            return objectID;
        }

        @Override
        public int hashCode() {
            int result = objectID;
            result = 31 * result + (methodIdentifier != null ? methodIdentifier.hashCode() : 0);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }

    }

}
