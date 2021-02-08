import { shallowMount } from '@vue/test-utils'
import App from '@/App.vue'

describe('Greeting.vue', () => {
  it('renders a greeting', () => {
    const wrapper = shallowMount(App)

    expect(wrapper.text()).toMatch('NASA Mars Rover Photo Viewer')
  })
})
