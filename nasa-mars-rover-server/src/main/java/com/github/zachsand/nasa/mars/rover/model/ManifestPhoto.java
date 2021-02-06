package com.github.zachsand.nasa.mars.rover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManifestPhoto {

    private int sol;

    private String earthDate;

    private int totalPhotos;

    private String[] cameras;
}
