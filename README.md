# nasa-mars-rover
![nasa-mars-rover-server](https://github.com/ZachSand/nasa-mars-rover/workflows/nasa-mars-rover-server/badge.svg)
![nasa-mars-rover-ui](https://github.com/ZachSand/nasa-mars-rover/workflows/nasa-mars-rover-ui/badge.svg)


## server

The [nasa-mars-rover-server](nasa-mars-rover-server) is a Java 11 [Spring Boot Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) 
application that hosts a minimal API based on data from the [NASA Mars Rover API](https://api.nasa.gov/). See the respective project's README for more details.

## UI

The [nasa-mars-rover-ui](nasa-mars-rover-ui) is a simple [Vue 3](https://v3.vuejs.org/) application that consumes the API from the [nasa-mars-rover-server](nasa-mars-rover-server).
It mainly consists of different options for the user to query for photos and then display those photos. The UI is built with [Element Plus](https://element-plus.org/#/en-US), a
popular Vue 3 component library, and it uses the native [JavaScript Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API). 
See the respective project's README for more details.

### Docker Quickstart

To quickly run the server and ui run the [startup](startup.sh) script
```
./startup.sh
```
This will build both applications and wire them together with [docker-compose](docker-compose.yml). After it is complete you can view the application at http://localhost:8081 in your
browser. 

### Motivation 

This project was motivated by the a coding excercise
> Using the API described here (https://api.nasa.gov/api.html ) build a project in GitHub that calls the Mars Rover API and selects a picture on a given day. We want your application to download and store each image locally. 
> * Use list of dates below to pull the images that were captured on that day by reading in a .txt file
>  * 02/27/17
>  * June 2, 2018
>  * Jul-13-2016
>  * April 31, 2018

The [PhotoDownloadE2ETest](nasa-mars-rover-server/src/test/java/com/github/zachsand/nasa/mars/rover/PhotoDownloadE2ETest.java) reads in the 
[photo-date.txt](nasa-mars-rover-server/src/test/resources/photo-dates.txt) and downloads the images. 

The following API endpoint allows for downloading a photo locally. 
- GET a single photo to download: `/marsrover/api/rovers/{roverName}/photo/{id}?earth_date=<some-date>`

The [PhotoDownloadService](nasa-mars-rover-server/src/main/java/com/github/zachsand/nasa/mars/rover/service/PhotoDownloadService.java) contains the implementation for downloading
the photo.
