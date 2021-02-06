package com.github.zachsand.nasa.mars.rover;

import com.github.zachsand.nasa.mars.rover.client.NasaRoverClient;
import com.github.zachsand.nasa.mars.rover.model.PhotoImage;
import com.github.zachsand.nasa.mars.rover.service.PhotoDownloadService;
import com.github.zachsand.nasa.mars.rover.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = NasaMarsRoverApplication.class)
public class PhotoDownloadE2ETest {

    @Autowired
    private PhotoDownloadService photoDownloadService;

    @Autowired
    private NasaRoverClient nasaRoverClient;

    /**
     * Reads an input file of different date formats, processes the valid ones, finds a photo for that date and downloads
     * it. Then verifies that the photo is a valid jpeg image.
     */
    @Test
    public void whenParseTextDatesFromFile_shouldRetrievePhotos() throws IOException {
        Files.readAllLines(Paths.get("src", "test", "resources", "photo-dates.txt"))
            .stream()
            .map(String::trim)
            .map(DateUtil::getLocalDateFromDate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(DateUtil::convertDateToNasaFormat)
            .collect(Collectors.toList())
            .forEach(date -> {
                LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
                queryParams.put("earth_date", Collections.singletonList(date));
                PhotoImage photoImage = photoDownloadService.downloadPhoto("curiosity", String.valueOf(
                    Arrays.stream(Objects.requireNonNull(
                            nasaRoverClient.getRoverPhotos("curiosity", queryParams).block()).getPhotos()
                    )
                    .findFirst()
                    .orElseThrow()
                    .getId()), queryParams).block();
                assertNotNull(photoImage);
                assertNotNull(photoImage.getPhotoName());
                assertNotNull(photoImage.getImageData());
                assertTrue(photoImage.getImageData().length > 100);

                /* Valid JPEG starts with 0xFF, 0xD8 and ends with 0xFF, 0xD9 */
                assertEquals((byte)0xFF, photoImage.getImageData()[0]);
                assertEquals((byte)0xD8, photoImage.getImageData()[1]);
                assertEquals((byte)0xFF, photoImage.getImageData()[photoImage.getImageData().length - 2]);
                assertEquals((byte)0xD9, photoImage.getImageData()[photoImage.getImageData().length - 1]);
            });




    }
}
