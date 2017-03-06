package blackbird.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class Compound {

    public Compound() {
        Blackbird.getCompounds().add(this);


    }


    public abstract List<Bond> getBonds();

    public List<Device> getComponents() {
        return getBonds().stream().collect(
                ArrayList::new,
                (list, bond) -> {
                    list.add(bond.getFrom());
                    list.add(bond.getTo());
                },
                ArrayList::addAll
        );
    }

}