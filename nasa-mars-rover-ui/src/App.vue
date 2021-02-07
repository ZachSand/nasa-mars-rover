<template>
  <el-header>
    <h1 style="color: white">NASA Mars Rover Photo Viewer</h1>
  </el-header>
  <el-main>
    <RoverPhotoSearch />
  </el-main>
</template>

<script>
import RoverPhotoSearch from './components/RoverPhotoSearch.vue'

export default {
  name: 'App',
  components: {
    RoverPhotoSearch
  }
}
</script>

<style lang="scss">
body {
  background: url('./assets/mars-background.jpg') no-repeat center center fixed;
  -webkit-background-size: cover;
  -moz-background-size: cover;
  -o-background-size: cover;
  background-size: cover;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
