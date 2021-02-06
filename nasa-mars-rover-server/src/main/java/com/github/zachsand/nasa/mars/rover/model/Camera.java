package com.github.zachsand.nasa.mars.rover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Camera {

    private int id;

    private String name;

    private int roverId;

    private String fullName;
}
