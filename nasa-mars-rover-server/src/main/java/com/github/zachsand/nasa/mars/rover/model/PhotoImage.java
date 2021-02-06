package com.github.zachsand.nasa.mars.rover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoImage {

    private String photoName;

    private byte[] imageData;
}
