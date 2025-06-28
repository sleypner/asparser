package dev.sleypner.asparser.domain.model.shared;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityDiffer <T extends Identifiable<T, U>,U>{

    private final BiPredicate<T, T> contentComparator;

    public EntityDiffer(BiPredicate<T, T> contentComparator) {
        this.contentComparator = contentComparator;
    }
    public Set<T> findUpdated(Set<T> parsed, Set<T> saved) {
        Map<U,T> savedMap = saved.stream()
                .collect(Collectors.toMap(Identifiable::getIdentifier, Function.identity()));
        return parsed.stream()
                .map(parsedItem ->{

                    T savedItem = savedMap.get(parsedItem.getIdentifier());
                    if (isUpdated(parsedItem, savedItem)) {
                        parsedItem.setId(savedItem.getId());
                        return parsedItem;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private boolean isUpdated(T parsedItem, T savedItem) {
        return savedItem != null && !contentComparator.test(savedItem, parsedItem);
    }
}

