package com.github.zachsand.nasa.mars.rover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoRover {

    private int id;

    private String name;

    private String landingDate;

    private String launchDate;

    private String status;
}
