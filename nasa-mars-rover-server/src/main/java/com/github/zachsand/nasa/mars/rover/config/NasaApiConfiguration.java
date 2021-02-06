package com.github.zachsand.nasa.mars.rover.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * {@link NasaApiConfiguration} configuration for the NASA Mars Rover API. Values are automatically populated from the
 * spring configuration values contained in the prefix {@code nasa.mars.rover.api}.
 */
@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "nasa.mars.rover.api")
public class NasaApiConfiguration {

    private final String baseRoverUrl;

    private final String roverPhotosEndpoint;

    private final String manifestEndpoint;

    private final String roversEndpoint;

    /**
     * {@link NasaApiConfiguration} configuration for the NASA Mars Rover API. Values are automatically populated from the
     *  spring configuration values contained in the prefix {@code nasa.mars.rover.api}.
     *
     * @param baseRoverUrl The base URL for the NASA Mars Rover API.
     * @param roverPhotosEndpoint The rover photos endpoint format for the NASA Mars Rover API.
     * @param manifestEndpoint The manifest endpoint format for the NASA Mars Rover API.
     * @param roversEndpoint The rovers endpoint format for the NASA Mars Rover API.
     */
    public NasaApiConfiguration(final String baseRoverUrl, final String roverPhotosEndpoint, final String manifestEndpoint,
        final String roversEndpoint) {
        this.baseRoverUrl = baseRoverUrl;
        this.roverPhotosEndpoint = roverPhotosEndpoint;
        this.manifestEndpoint = manifestEndpoint;
        this.roversEndpoint = roversEndpoint;
    }
}
