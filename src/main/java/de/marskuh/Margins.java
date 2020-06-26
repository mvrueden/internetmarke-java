package de.marskuh;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(builderClassName = "Builder")
public class Margins {
    float left;
    float right;
    float top;
    float bottom;
}
