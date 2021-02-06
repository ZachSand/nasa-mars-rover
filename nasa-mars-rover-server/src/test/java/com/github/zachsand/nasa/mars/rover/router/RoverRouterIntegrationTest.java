package com.github.zachsand.nasa.mars.rover.router;

import com.github.zachsand.nasa.mars.rover.NasaMarsRoverApplication;
import com.github.zachsand.nasa.mars.rover.client.NasaRoverClient;
import com.github.zachsand.nasa.mars.rover.model.*;
import com.github.zachsand.nasa.mars.rover.service.RoverManifestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = NasaMarsRoverApplication.class)
public class RoverRouterIntegrationTest {

    @MockBean
    private NasaRoverClient nasaRoverClient;

    @MockBean
    private RoverManifestService roverManifestService;

    @Autowired
    private RoverRouter roverRouter;

    @Test
    public void whenGetRovers_shouldReturnRovers() {
        WebTestClient client = WebTestClient
            .bindToRouterFunction(roverRouter.getRovers())
            .build();

        Rover rover = new Rover(1, "name", "01/01/2000", "01/01/2000", "active", 100, "01/01/2000", 1000, new Camera[] {new Camera()});
        RoverList roverList = new RoverList(new Rover[] {
            rover
        });

        given(nasaRoverClient.getRovers()).willReturn(Mono.just(roverList));

        client.get()
            .uri("/api/rovers")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(RoverList.class)
            .isEqualTo(roverList);
    }

    @Test
    public void whenGetRoverPhotoManifest_shouldReturnPhotoManifest() {
        WebTestClient client = WebTestClient
                .bindToRouterFunction(roverRouter.getRoverPhotoManifestByDate())
                .build();

        ManifestPhoto manifestPhoto = new ManifestPhoto(0, "02/01/18", 1000, new String[] {"CAM"});

        given(roverManifestService.getRoverPhotoManifestByDate("curiosity", new LinkedMultiValueMap<>()))
                .willReturn(Mono.just(manifestPhoto));

        client.get()
            .uri("/api/rovers/curiosity/manifest")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(ManifestPhoto.class)
            .isEqualTo(manifestPhoto);
    }

    @Test
    public void whenGetRoverPhotos_shouldReturnPhotos() {
        WebTestClient client = WebTestClient
                .bindToRouterFunction(roverRouter.getRoverPhotos())
                .build();

        PhotoList photoList = new PhotoList(new Photo[] {
            new Photo(1, 1, new Camera(1, "CAM", 5, "CAMERA"), "http://img.com", "01/01/2021",
                new PhotoRover(1, "roverName", "02/01/2021", "02/02/2021", "active")
            )
        });

        given(nasaRoverClient.getRoverPhotos("curiosity", new LinkedMultiValueMap<>()))
                .willReturn(Mono.just(photoList));

        client.get()
                .uri("/api/rovers/curiosity/photos")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PhotoList.class)
                .isEqualTo(photoList);
    }
}
