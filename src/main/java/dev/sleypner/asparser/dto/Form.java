package dev.sleypner.asparser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Accessors(chain = true)
public class Form {
    @NonNull
    private List<FormElement> elements;
    @NonNull
    private String name;
    private String action;
    @NonNull
    private Boolean button;
    private String title;
    @NonNull
    private String type;
}
