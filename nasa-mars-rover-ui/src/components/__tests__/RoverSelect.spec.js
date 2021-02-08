import { shallowMount } from '@vue/test-utils'
import RoverSelect from '@/components/RoverSelect.vue'
import fetchMock from 'jest-fetch-mock'

fetchMock.enableMocks()

const roverName = 'Curiosity'
const roverData = { rovers: [{ name: roverName }] }
process.env.VUE_APP_ROVER_API_BASE_URL = 'http://localhost:8080/marsrover/api/rovers/'

beforeEach(() => {
  fetch.resetMocks()
})

describe('RoverSelect.vue', () => {
  it('should render rovers when ', () => {
    fetch.mockResponseOnce(JSON.stringify(roverData), { status: 200, headers: { 'content-type': 'application/json' } })
    shallowMount(RoverSelect)
    expect(fetch).toHaveBeenCalledTimes(1)
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/marsrover/api/rovers', {
        headers: {
          'content-type': 'application/json'
        },
        method: 'get'
      }
      /* TODO: Verify fetch response gets populated correctly */
    )
  })
})
