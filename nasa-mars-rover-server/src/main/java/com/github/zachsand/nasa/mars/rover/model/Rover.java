package com.github.zachsand.nasa.mars.rover.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rover {

    private int id;

    private String name;

    private String landingDate;

    private String launchDate;

    private String status;

    private int maxSol;

    private String maxDate;

    private int totalPhotos;

    private Camera[] cameras;
}
