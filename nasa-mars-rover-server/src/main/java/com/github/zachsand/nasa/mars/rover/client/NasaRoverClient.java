package com.github.zachsand.nasa.mars.rover.client;

import com.github.zachsand.nasa.mars.rover.config.NasaApiConfiguration;
import com.github.zachsand.nasa.mars.rover.model.PhotoList;
import com.github.zachsand.nasa.mars.rover.model.PhotosManifest;
import com.github.zachsand.nasa.mars.rover.model.RoverList;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * {@link NasaRoverClient} Client for querying the NASA Mars Rover API.
 *
 * @see <a href="https://github.com/chrisccerami/mars-photo-api"/>
 */
@Component
public class NasaRoverClient {

    private final NasaApiConfiguration nasaApiConfiguration;

    private final WebClient nasaRoverWebClient;

    /**
     * {@link NasaRoverClient} Client for querying the NASA Mars Rover API.
     *
     * @param nasaApiConfiguration {@link NasaApiConfiguration} Configuration for NASA Mars Rover API values.
     * @param webClientBuilder {@link WebClient.Builder} Builder for the {@link WebClient} used to query the NASA Mars
     *                                                  Rover API.
     */
    public NasaRoverClient(final NasaApiConfiguration nasaApiConfiguration, WebClient.Builder webClientBuilder) {
        this.nasaApiConfiguration = nasaApiConfiguration;

        this.nasaRoverWebClient = webClientBuilder
            .baseUrl(nasaApiConfiguration.getBaseRoverUrl())
            .build();
    }

    /**
     * Retrieves a NASA Mars Rover Manifest from the NASA Mars Rover API.
     *
     * @param roverName The name of the NASA Mars rover.
     * @return {@link Mono} of {@link PhotosManifest} with rover manifest data populated from the NASA Mars Rover Manifest API.
     */
    public Mono<PhotosManifest> getRoverManifest(String roverName) {
        return nasaRoverWebClient.get()
            .uri(String.format(nasaApiConfiguration.getManifestEndpoint(), roverName))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError, NasaRoverClient::handleError)
            .bodyToMono(PhotosManifest.class)
            .single();
    }

    /**
     * Retrieves all possible Mars Rovers from the NASA Mars Rover API in the form of {@link RoverList}.
     *
     * @return {@link Mono} of {@link RoverList} with rover data from the NASA Mars Rover API.
     */
    public Mono<RoverList> getRovers() {
        return nasaRoverWebClient.get()
                .uri(nasaApiConfiguration.getRoversEndpoint())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, NasaRoverClient::handleError)
                .bodyToMono(RoverList.class)
                .single();
    }

    /**
     * Retrieves all photo metadata for the NASA Mars rover in the form of {@link PhotoList}, filtered by the query parameters.
     * Acceptable query parameters are {@code earth_date}, {@code sol}, {@code page}, {@code per_page}.
     *
     * @param roverName The name of the NASA Mars rover.
     * @param queryParams HTTP query parameters from the original API request, used to be more specific about what photos to find.
     * @return {@link Mono} of {@link PhotoList} with photo metadata from the NASA Mars Rover API.
     */
    public Mono<PhotoList> getRoverPhotos(String roverName, MultiValueMap<String, String> queryParams) {
        return nasaRoverWebClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path(String.format(nasaApiConfiguration.getRoverPhotosEndpoint(), roverName))
                        .queryParams(queryParams)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, NasaRoverClient::handleError)
                .bodyToMono(PhotoList.class)
                .single();
    }

    /**
     * Handles when {@link HttpStatus#isError()} is {@code true} by responding with an appropriate error.
     *
     * @param clientResponse {@link ClientResponse} the response from the client.
     * @return {@link Mono#error(Throwable)} with {@link ResponseStatusException} with {@link HttpStatus#BAD_GATEWAY} and message.
     */
    private static Mono<? extends Throwable> handleError(ClientResponse clientResponse) {
        return Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error retrieving information from the " +
                "NASA API"));
    }
}
