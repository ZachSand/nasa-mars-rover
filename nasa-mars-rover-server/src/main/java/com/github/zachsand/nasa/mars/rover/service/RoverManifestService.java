package com.github.zachsand.nasa.mars.rover.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.zachsand.nasa.mars.rover.client.NasaRoverClient;
import com.github.zachsand.nasa.mars.rover.model.ManifestPhoto;
import com.github.zachsand.nasa.mars.rover.model.PhotosManifest;
import com.github.zachsand.nasa.mars.rover.model.Rover;
import com.github.zachsand.nasa.mars.rover.util.DateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * {@link RoverManifestService} service for retrieving NASA Mars rover manifest information.
 */
@Service
public class RoverManifestService {

    private final NasaRoverClient nasaRoverClient;

    private final Cache<String, PhotosManifest> photosManifestCache;

    private static final int CACHE_EXPIRE_HOURS = 24;

    /**
     * {@link RoverManifestService} service for retrieving NASA Mars rover manifest information.
     * @param nasaRoverClient {@link NasaRoverClient} The client for making requests to the NASA Mars Rover API.
     */
    public RoverManifestService(final NasaRoverClient nasaRoverClient) {
        this.nasaRoverClient = nasaRoverClient;

        /*
         * Initialize cache for manifest to expire after 24 hours, since new manifest information may be available the
         * next day.
         */
        photosManifestCache = Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(CACHE_EXPIRE_HOURS, TimeUnit.HOURS)
                .build();
    }

    @PostConstruct
    public void fillCache() {
        nasaRoverClient.getRovers()
            .subscribe(roverList -> Arrays.stream(roverList.getRovers())
                .map(Rover::getName)
                .map(nasaRoverClient::getRoverManifest)
                .forEach(photosManifestMono -> photosManifestMono.subscribe(
                    photosManifest -> photosManifestCache.put(photosManifest.getPhotoManifest().getName().toLowerCase(), photosManifest))));
    }

    /**
     * Retrieves a NASA Rover photo manifest for a specific date ({@code sol} or {@code earth_date} are valid date types).
     *
     * @param roverName The name of the NASA Mars rover to get the photo manifest for.
     * @param requestParameters The HTTP query parameters. Should have valid ({@code sol} or {@code earth_date}.
     * @return {@link Mono} of {@link ManifestPhoto}.
     */
    public Mono<ManifestPhoto> getRoverPhotoManifestByDate(String roverName, MultiValueMap<String, String> requestParameters) {
        List<String> sol = requestParameters.get("sol");
        List<String> earthDate = requestParameters.get("earth_date");

        if(CollectionUtils.isEmpty(sol) && CollectionUtils.isEmpty(earthDate)) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid query parameters. Only sol or earth_date are allowed"));
        }

        /* If the application has been running longer than CACHE_EXPIRE_HOURS it is possible the cache is now empty */
        if(photosManifestCache.estimatedSize() == 0) {
            fillCache();
        }

        PhotosManifest photosManifest = photosManifestCache.getIfPresent(roverName.toLowerCase());
        if(photosManifest == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "The rover name was not found"));
        }

        if(CollectionUtils.isEmpty(sol)) {
            Optional<LocalDate> requestedDateInstance = DateUtil.getLocalDateFromDate(earthDate.get(0));
            if(requestedDateInstance.isEmpty()) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid earth_date format"));
            }
            return getManifestForDate(photosManifest, DateUtil.convertDateToNasaFormat(requestedDateInstance.get()));
        } else {
            return getManifestForSol(photosManifest, sol.get(0));
        }
    }

    /**
     * Retrieves the {@link ManifestPhoto} for the sol date.
     *
     * @param photosManifest {@link PhotosManifest} containing all the photo manifest information for the NASA Mars rover.
     * @param sol The sol value to find the specific {@link ManifestPhoto} for.
     * @return {@link Mono} of {@link ManifestPhoto} upon success, or {@link Mono#error(Throwable)} with {@link ResponseStatusException}
     * containing the appropriate {@link HttpStatus} and message for the error.
     */
    private Mono<ManifestPhoto> getManifestForSol(PhotosManifest photosManifest, String sol) {
        try{
            int solNum = Integer.parseInt(sol);
            Optional<ManifestPhoto> photoManifest = Arrays.stream(photosManifest.getPhotoManifest().getPhotos())
                    .filter(manifestPhoto -> manifestPhoto.getSol() == solNum)
                    .findFirst();

            return photoManifest
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No photo manifest found for sol")));

        }catch(NumberFormatException e) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sol format"));
        }
    }

    /**
     * Retrieves the {@link ManifestPhoto} for the {@code earth_date}.
     *
     * @param photosManifest {@link PhotosManifest} containing all the photo manifest information for the NASA Mars rover.
     * @param earthDate The date value to the specific {@link ManifestPhoto} for.
     * @return {@link Mono} of {@link ManifestPhoto} upon success, or {@link Mono#error(Throwable)} with {@link ResponseStatusException}
     * containing the appropriate {@link HttpStatus} and message for the error.
     */
    private Mono<ManifestPhoto> getManifestForDate(PhotosManifest photosManifest, String earthDate) {
        Optional<ManifestPhoto> photoManifest = Arrays.stream(photosManifest.getPhotoManifest().getPhotos())
                .filter(manifestPhoto -> manifestPhoto.getEarthDate().equals(earthDate))
                .findFirst();

        return photoManifest
            .map(Mono::just)
            .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "No photo manifest found for earth_date")));
    }
}
