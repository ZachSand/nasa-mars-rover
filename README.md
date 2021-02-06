# nasa-mars-rover
![nasa-mars-rover-server](https://github.com/ZachSand/nasa-mars-rover/workflows/nasa-mars-rover-server/badge.svg)

## nasa-mars-rover-server
The nasa-mars-rover-server uses Spring Boot Webflux. It isn't entirely a reactive application but I wanted to get some experience
using it. The server hosts a REST API that the nasa-mars-rover-ui consumes to display the web UI. 
The server uses the [NASA Mars Rover API](https://github.com/chrisccerami/mars-photo-api) for retrieving photo information about the rovers.
For more info, build instructions, API examples, etc. see the README in that directory.
