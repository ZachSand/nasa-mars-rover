package com.github.zachsand.nasa.mars.rover.router;

import com.github.zachsand.nasa.mars.rover.client.NasaRoverClient;
import com.github.zachsand.nasa.mars.rover.model.*;
import com.github.zachsand.nasa.mars.rover.service.PhotoDownloadService;
import com.github.zachsand.nasa.mars.rover.service.RoverManifestService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Router for handling the API requests to the server, similar to the controller for Spring MVC.
 * Handles routing the valid API requests to the appropriate handlers and services.
 */
@Configuration
public class RoverRouter {

    private final NasaRoverClient nasaRoverClient;

    private final RoverManifestService roverManifestService;

    private final PhotoDownloadService photoDownloadService;

    /**
     * Router for handling the API requests to the server, similar to the controller for Spring MVC.
     * Handles routing the valid API requests to the appropriate handlers and services.
     *
     * @param nasaRoverClient {@link NasaRoverClient} The client for making requests to the NASA Mars Rover API.
     * @param roverManifestService {@link RoverManifestService} service for the rover manifest functionality.
     * @param photoDownloadService {@link PhotoDownloadService} service for the rover photo download functionality.
     */
    public RoverRouter(final NasaRoverClient nasaRoverClient, final RoverManifestService roverManifestService,
                       final PhotoDownloadService photoDownloadService) {
        this.nasaRoverClient = nasaRoverClient;
        this.roverManifestService = roverManifestService;
        this.photoDownloadService = photoDownloadService;
    }

    /**
     * Routes and handles the endpoint for retrieving all Nasa Mars Rover in the form of {@link RoverList}.
     * Essentially a proxy API for the actual NASA Mars Rover API.
     */
    @Bean
    public RouterFunction<ServerResponse> getRovers() {
        return
            route(GET("/api/rovers"),
                req -> ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                    .body(nasaRoverClient.getRovers(), RoverList.class));
    }

    /**
     * Routes and handles the endpoint for retrieving the Nasa Mars Rover manifest for a specific date in the form of
     * {@link PhotosManifest}.
     */
    @Bean
    public RouterFunction<ServerResponse> getRoverPhotoManifestByDate() {
        return
            route(GET("/api/rovers/{roverName}/manifest"),
                req -> ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                    .body(
                        roverManifestService.getRoverPhotoManifestByDate(req.pathVariable("roverName"), req.queryParams()),
                        PhotosManifest.class
                    )
            );
    }

    /**
     * Routes and handles the endpoint for retrieving the Nasa Mars Rover photos for a specific date in the form of
     * {@link PhotoList}.
     * Essentially a proxy API for the actual NASA Mars Rover API.
     */
    @Bean
    public RouterFunction<ServerResponse> getRoverPhotos() {
        return
            route(GET("/api/rovers/{roverName}/photos"),
                req -> ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                    .body(
                        nasaRoverClient.getRoverPhotos(req.pathVariable("roverName"), req.queryParams()),
                        PhotoList.class
                    )
            );
    }

    /**
     * Routes and handles the endpoint for retrieving and downloading the Nasa Mars Rover photo for a specific date and
     * {@link Photo#getId()} in the form of a {@link PhotoImage}.
     */
    @Bean
    public RouterFunction<ServerResponse> downloadRoverPhoto() {
        return
            route(GET("/api/rovers/{roverName}/photo/{id}"),
                req -> photoDownloadService.downloadPhoto(req.pathVariable("roverName"), req.pathVariable("id"), req.queryParams())
                    .flatMap(photoImage -> ServerResponse.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG.toString())
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photoImage.getPhotoName() + "\"")
                        .bodyValue(photoImage.getImageData()))
            );
    }
}
