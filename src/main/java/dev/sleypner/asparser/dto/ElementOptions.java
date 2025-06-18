package dev.sleypner.asparser.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
@RequiredArgsConstructor
public class ElementOptions {
    @NonNull
    private final String name;
    @NonNull
    private final String value;
    @NonNull
    private Boolean selected = false;

}
