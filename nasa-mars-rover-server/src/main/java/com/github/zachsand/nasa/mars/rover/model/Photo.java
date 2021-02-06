package com.github.zachsand.nasa.mars.rover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Photo {

    private int id;

    private int sol;

    private Camera camera;

    private String imgSrc;

    private String earthDate;

    private PhotoRover rover;
}
