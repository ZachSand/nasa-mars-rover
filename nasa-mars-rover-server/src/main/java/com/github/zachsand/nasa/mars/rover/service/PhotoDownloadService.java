package com.github.zachsand.nasa.mars.rover.service;

import com.github.zachsand.nasa.mars.rover.client.NasaRoverClient;
import com.github.zachsand.nasa.mars.rover.model.Photo;
import com.github.zachsand.nasa.mars.rover.model.PhotoImage;
import com.github.zachsand.nasa.mars.rover.util.DateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * {@link PhotoDownloadService} service for downloading a photo from the NASA Mars Rover API.
 */
@Service
public class PhotoDownloadService {

    private final NasaRoverClient nasaRoverClient;

    private final WebClient webClient;

    /**
     * {@link PhotoDownloadService} service for downloading a photo from the NASA Mars Rover API.
     *
     * @param nasaRoverClient {@link NasaRoverClient} The client for making requests to the NASA Mars Rover API.
     * @param webClientBuilder {@link WebClient.Builder} Builder for the {@link WebClient} used to query the image source url.
     */
    public PhotoDownloadService(final NasaRoverClient nasaRoverClient, WebClient.Builder webClientBuilder) {
        this.nasaRoverClient = nasaRoverClient;
        this.webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(
                /* Some photos redirect 301/307 from HTTP to HTTPS, so follow them */
                HttpClient.create().followRedirect(true)
        ))
        .build();
    }

    /**
     * Retrieves photo data by retrieving the {@link Photo} for with the requested {@code id} and {@code earth_date}. Then
     * uses {@link Photo#getImgSrc()} to retrieve the raw image data to return as a downloadable image.
     *
     * @param roverName The name of the NASA Mars rover to find a photo for.
     * @param id The id of the {@link Photo} to find the raw image data for.
     * @param queryParams The HTTP query parameters, used to get the photo and {@code earth_date}.
     * @return {@link Mono} of {@link PhotoImage}.
     */
    public Mono<PhotoImage> downloadPhoto(String roverName, String id, MultiValueMap<String, String> queryParams) {
        List<String> earthDates = queryParams.get("earth_date");
        if(CollectionUtils.isEmpty(earthDates)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid query parameters. Only earth_date is allowed"));
        }

        Optional<LocalDate> requestedDateInstance = DateUtil.getLocalDateFromDate(earthDates.get(0));
        if(requestedDateInstance.isEmpty()) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid earth_date format"));
        }

        try {
            /*
             * Since the NASA Mars Rover API doesn't have an endpoint to find a photo by ID, first query for all the photos
             * with the given query parameters.
             * Then find the photo, if it exists, with the given id.
             * Then make a GET request to that image source to get the raw bytes of the image to store in the PhotoImage.
             */
            return nasaRoverClient.getRoverPhotos(roverName, queryParams)
                .flatMap(photoList ->
                    webClient.get().uri(
                        Arrays.stream(photoList.getPhotos())
                            .filter(photo -> photo.getId() == Integer.parseInt(id))
                            .findFirst()
                            .orElseThrow()
                            .getImgSrc()
                    )
                    .retrieve()
                    .bodyToMono(byte[].class)
                    /* All NASA Mars Rover photos are in the JPEG format */
                    .flatMap(imageBytes -> Mono.just(new PhotoImage(roverName + "_" + id + ".jpg", imageBytes)))
                );
        }catch(Exception e) {
            /* Lots of things could go wrong, so generally just catch any exception and give the general 404 */
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find photo to download " +
                    System.lineSeparator() + e.getMessage()));
        }
    }
}
