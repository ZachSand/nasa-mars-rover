# nasa-mars-rover-server
The [nasa-mars-rover-server](nasa-mars-rover-server) is a Java 11 [Spring Boot Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
application that hosts a minimal API based on data from the [NASA Mars Rover API](https://api.nasa.gov/). I chose to go with Webflux because
I hadn't really used it before, and it was interesting to use Spring in a reactive way. The application isn't a fully 
reactive application, but it does utilize a lot of the features. 

I also tried out a simple [Caffeine Cache](https://github.com/ben-manes/caffeine) for caching the API response for the photo manifest information
because it is a decent amount of data that the UI needs to readily query for. It was probably a bit of overkill for just using it 
in one place, but I can still extend it to be used in other parts of the application. 

## Endpoints
- GET `/marsrover/api/rovers`
  - Retrieves the list of rovers. Proxy pass through API for the actual NASA API endpoint
    

- GET `/marsrover/api/rovers/{roverName}/photos`
  - Retrieves photo data for the specified rover. Proxy pass through API for the actual NASA API endpoint. **This response
    gives the ID needed for the photo download.**
  - Query parameters
      - earth_date: Date on earth the photo was taken 
          - example: `2020-01-01`
      - sol: Number of days from when the rover landed
          - example: `1002`
      - page: The page to return
          - example: `5`
      - per_page: The number of photos per page
          - example: `25`
    

- GET `/marsrover/api/rovers/{roverName}/manifest`
  - Retrieves photo manifest data for the specified rover
  - Query parameters: One of the two must be given
      - earth_date: Date on earth the photo was taken
          - example `2020-01-01`
      - sol: Number of days from when the rover landed
        - example: `1002`


- GET `/marsrover/api/rovers/{roverName}/photo/{id}`
  - Retrieves and downloads a single photo for the specified rover and photo id
  - Query parameters:
    - earth_date **required**. Unfortunately this is required due to the NASA API not having an endpoint to find a photo
      by ID. I could've queried and cached all the IDs or placed them in a database, but at this point I haven't. 
      - example: `2020-01-01`

### Build
```
gradle build
```

### Run Standalone
```
gradle bootRun
```
Access the application API through localhost:8080

### Build with Docker
```
gradle docker
```

### Run with Docker
```
docker run -p 8080:8080 com.github.zachsand/nasa-mars-rover-server
```
Access the application API through localhost:8080

### Needs Improvement
- More testing
- Logging
- Refactor whatever imperative logic there is still to be reactive
- Better use of cache
- Better error handling rather than the global handling currently in place
- Swagger docs or similar for API documentation
