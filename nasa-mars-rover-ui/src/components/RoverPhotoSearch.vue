<template>
  <el-carousel v-if="cameraPhotos" trigger="click" indicator-position="none" :autoplay=false height="500px">
    <el-carousel-item v-for="photo in cameraPhotos" :key="photo">
      <h3 class="medium">
        <el-image
          class="image"
          :src=photo.img_src
          :key="photo.id"
        />
      </h3>
    </el-carousel-item>
  </el-carousel>
  <p></p>
  <el-row :gutter="20">
    <el-col :span="6">
      <div class="grid-content">
        <h3>Choose Rover</h3>
        <rover-select @changeRover="changeRover" />
      </div>
    </el-col>
    <el-col :span="3">
      <i class="el-icon-arrow-right" style="color: white; font-size:35px"></i><br/>
    </el-col>
    <el-col :span="6" v-if="maxSol > 0">
      <h3>Choose Date</h3>
      <div class="grid-content">
        <div class="block">
          <el-input-number v-model="sol" @change="changeSol" :disabled="earthDate" :precision="0" :min="0" :max="maxSol" :placeholder="'Enter Sol #'" />
        </div>
        <p>or</p>
        <div class="block">
          <el-date-picker
            @change="changeEarthDate"
            :disabled="sol"
            v-model="earthDate"
            type="date"
            placeholder="Choose earth date"
            :disabled-date="disabledDate"
            :shortcuts="shortcuts"
          >
          </el-date-picker>
        </div>
        <p></p>
        <el-alert v-if="!foundPhoto"
          title="No photos found for the selected options"
          type="warning"
          show-icon
          description="Please choose a different sol or date.">
        </el-alert>
      </div>
    </el-col>
    <el-col :span="3" v-if="maxSol > 0">
      <i class="el-icon-arrow-right" style="color: white; font-size:35px"></i><br/>
    </el-col>
    <el-col :span="6">
      <div class="grid-content" v-if="foundPhoto && cameras">
        <h3>Choose Rover Camera</h3>
        <el-select @change="changeCamera" v-model="camera" placeholder="Select Camera">
          <el-option
            v-for="cameraName in cameras"
            :key="cameraName"
            :label="cameraName"
            :value="cameraName">
          </el-option>
        </el-select>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import RoverSelect from './RoverSelect.vue'

export default {
  name: 'RoverPhotoSearch',
  components: {
    RoverSelect
  },
  methods: {
    changeRover (rover) {
      this.camera = ''
      this.photos = undefined
      this.cameraPhotos = undefined
      this.currentPhotos = undefined
      this.earthDate = undefined
      this.maxSol = rover.max_sol
      this.roverName = rover.name
    },
    changeSol () {
      this.camera = ''
      this.photos = undefined
      this.cameraPhotos = undefined
      this.currentPhotos = undefined
      this.fetchManifest()
    },
    changeEarthDate () {
      this.camera = ''
      this.photos = undefined
      this.cameraPhotos = undefined
      this.currentPhotos = undefined
      this.fetchManifest()
    },
    changeCamera () {
      this.cameraPhotos = undefined
      this.currentPhotos = undefined
      this.fetchPhotos()
    },
    fetchManifest () {
      const queryParams = this.sol ? '?sol=' + this.sol : '?earth_date=' + this.earthDate.toLocaleDateString('en-US')
      return fetch('http://localhost:8080/marsrover/api/rovers/' + this.roverName.toLowerCase() + '/manifest' + queryParams, {
        method: 'get',
        headers: {
          'content-type': 'application/json'
        }
      })
        .then(res => {
          if (!res.ok) {
            const error = new Error(res.statusText)
            error.json = res.json()
            this.foundPhoto = false
            throw error
          }

          return res.json()
        })
        .then(json => {
          this.foundPhoto = true
          this.cameras = json.cameras
        })
        .catch(err => {
          this.foundPhoto = false
          console.log(err)
        })
    },
    fetchPhotos () {
      const queryParams = this.sol ? '?sol=' + this.sol : '?earth_date=' + this.earthDate.toLocaleDateString('en-US')
      return fetch('http://localhost:8080/marsrover/api/rovers/' + this.roverName.toLowerCase() + '/photos' + queryParams, {
        method: 'get',
        headers: {
          'content-type': 'application/json'
        }
      })
        .then(res => {
          if (!res.ok) {
            const error = new Error(res.statusText)
            error.json = res.json()
            this.foundPhoto = false
            throw error
          }

          return res.json()
        })
        .then(json => {
          this.currentPhotos = json.photos
          this.cameraPhotos = json.photos.filter(photo => photo.camera.name === this.camera)
          this.photosTitle = this.roverName
        })
        .catch(err => {
          this.foundPhoto = false
          console.log(err)
        })
    }

  },
  data () {
    return {
      disabledDate (time) {
        return time.getTime() > Date.now()
      },
      shortcuts: [{
        text: 'Today',
        value: new Date()
      }, {
        text: 'Yesterday',
        value: (() => {
          const date = new Date()
          date.setTime(date.getTime() - 3600 * 1000 * 24)
          return date
        })()
      }, {
        text: 'A week ago',
        value: (() => {
          const date = new Date()
          date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
          return date
        })()
      }],
      earthDate: undefined,
      sol: undefined,
      maxSol: 0,
      foundPhoto: true,
      cameras: undefined,
      camera: '',
      currentPhotos: undefined,
      cameraPhotos: undefined
    }
  }
}
</script>
<style scoped lang="scss">
img {
  max-width: 100%;
  max-height: 100%;
}
h3 {
  color: white;
}
p {
  color: white;
}
</style>
