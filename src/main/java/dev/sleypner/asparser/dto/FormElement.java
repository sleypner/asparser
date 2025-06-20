package dev.sleypner.asparser.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@RequiredArgsConstructor
public class FormElement {
    @NonNull
    private final String name;
    @NonNull
    private final String label;
    @NonNull
    private final String type;
    private List<ElementOptions> options;
    private final String baseValue;
    private boolean multiply;
}
