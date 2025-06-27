package dev.sleypner.asparser.domain.model.shared;

public interface Identifiable<T,U> {
    T setId(Integer id);
    Integer getId();
    U getIdentifier();
}
