package dev.sleypner.asparser.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@RequiredArgsConstructor
public class ElementOptions {
    @NonNull
    private final String name;
    @NonNull
    private final String value;
    @NonNull
    @Builder.Default
    private Boolean selected = false;

}
