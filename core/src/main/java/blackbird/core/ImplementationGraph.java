package blackbird.core;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImplementationGraph {

    private Group group;

    public ImplementationGraph() {

        group = new Group();

    }

    public void add(DInterface implementation) {
        Node node = new Node(implementation);

        group.getChildren().stream().collect(Collectors.toList()).stream()
                .filter(n -> n.isCoveredBy(node))
                .forEach(n -> {
                    group.remove(n);
                    node.add(n);
                });

        group.add(node);
    }

    public Node find(Class<?> clazz) {
        return group.find(clazz);
    }

    public static class Group {

        private List<Node> children;

        public Group() {
            children = new ArrayList<>();
        }

        public boolean add(Node node) {
            return children.add(node);
        }

        public Node find(Class<?> clazz) {
            return getChildren().stream()
                    .filter(n -> clazz.isAssignableFrom(n.getImplementationClass()))
                    .findFirst().orElse(null);
        }

        public List<Node> getChildren() {
            return children;
        }

        public boolean remove(Node o) {
            return children.remove(o);
        }

    }

    public static class Node extends Group {

        // private boolean lock; // TODO LOCK
        private DInterface implementation;

        public Node(DInterface implementation) {
            this.implementation = implementation;
        }

        public DInterface getImplementation() {
            return implementation;
        }

        public Class<DInterface> getImplementationClass() {
            //noinspection unchecked
            return (Class<DInterface>) implementation.getClass();
        }

        public boolean isCoveredBy(Node node) {
            return this.getImplementationClass().isAssignableFrom(node.getImplementationClass());
        }

    }

}
