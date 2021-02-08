# nasa-mars-rover-ui
The [nasa-mars-rover-ui](nasa-mars-rover-ui) is a simple [Vue 3](https://v3.vuejs.org/) application that consumes the API from the [nasa-mars-rover-server](nasa-mars-rover-server).
It mainly consists of different options for the user to query for photos and then display those photos. The UI is built with [Element Plus](https://element-plus.org/#/en-US), a
popular Vue 3 component library, and it uses the native [JavaScript Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API). 

At the time of writing this I didn't have a lot of front-end development experience. I didn't separate my 
components well and essentially just reused whatever I could get to work instead of doing it the *right* way. 
That being said, it is a functional UI application that meets the simple purpose of querying for and viewing the 
NASA Mars rover photos. It may not be the prettiest thing you've seen today, but it does its job. 

It uses [Jest](https://jestjs.io/) for unit testing. [Cypress](https://www.cypress.io/) is setup for end to end testing 
I didn't get the time to write those tests yet. 

There is a Dockerfile here, and the application can be run standalone with it but it isn't functional without the API 
server, so I didn't want to call it out below. 


## Project setup
**For functionality the [API server](nasa-mars-rover-server) needs to be running, otherwise the application won't do much.**
```
npm install
```

### Compile and hot-reload for development
```
npm run serve
```

### Compile and minify for production
```
npm run build
```

### Run unit tests with Jest
```
npm run test:unit
```

### Needs Improvement
- More Tests
- Separate logic into more components
- Generally utilize the Vue framework more in the way it was intended
- Better UI look and feel
