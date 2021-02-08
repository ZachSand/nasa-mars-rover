<template>
    <el-select v-if="!loading && roverData" @change="changeRover(roverData)" v-model="roverName" placeholder="Select Rover">
      <el-option
        v-for="rover in roverData"
        :key="rover.name"
        :label="rover.name"
        :value="rover.name">
      </el-option>
    </el-select>
</template>

<script>
import { ref } from 'vue'
import { onMounted } from '@vue/runtime-core'

export default {
  name: 'RoverSelect',
  setup: function () {
    const roverData = ref(null)
    const loading = ref(false)
    const error = ref(null)

    function fetchRovers () {
      loading.value = true
      return fetch(process.env.VUE_APP_ROVER_API_BASE_URL, {
        method: 'get',
        headers: {
          'content-type': 'application/json'
        }
      })
        .then(res => {
          if (!res.ok) {
            const error = new Error(res.statusText)
            error.json = res.json()
            throw error
          }

          return res.json()
        })
        .then(json => {
          roverData.value = json.rovers
        })
        .catch(err => {
          error.value = err
          // In case a custom JSON error response was provided
          if (err.json) {
            return err.json.then(json => {
              // set the JSON response message
              error.value.message = json.message
            })
          }
        })
        .then(() => {
          loading.value = false
        })
    }

    onMounted(() => {
      fetchRovers()
    })

    return {
      roverData,
      loading,
      error
    }
  },
  methods: {
    /**
     * @param {{rovers:object}} object of rovers
     */
    changeRover (roverData) {
      this.$emit('changeRover', roverData.find(rover => rover.name === this.roverName))
    }
  },
  data () {
    return {
      roverName: ''
    }
  }
}
</script>
