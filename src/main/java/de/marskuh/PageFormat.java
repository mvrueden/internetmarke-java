package de.marskuh;

import lombok.Data;

@Data
public class PageFormat {

    enum Orientation {
        Landscape,
        Portrait
    }

    private float width;
    private float height;
    private int id;
    private Orientation orientation;
    private String name;
    private String description;
    private Margins margins;

    // Anzahl Etiketten in x - Richtung
    private int xLabelCount;

    // Anzahl Etiketten in y - Richtung
    private int yLabelCount;
}
