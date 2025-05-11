import axios from 'axios'
import { Message } from 'element-ui'
/* eslint-disable */

// Create axios instance
const request = axios.create({
  baseURL: 'http://8.155.35.219:8088/', // Use relative path, access backend via proxy
  timeout: 10000, // Set timeout to 10 seconds
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// Request interceptor
request.interceptors.request.use(
  config => {
    // 自动加上token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    console.log('Sending request:', {
      url: config.url,
      method: config.method,
      data: config.data
    })
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  response => {
    // Print response info
    console.log('Received response:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })

    return response.data
  },
  error => {
    // Print detailed error info
    console.error('Request failed:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message
    })

    // Handle different types of errors
    if (error.response) {
      // Server returned an error status code
      switch (error.response.status) {
        case 400:
          Message.error('Request parameter error')
          break
        case 401:
          Message.error('Unauthorized, please log in again')
          break
        case 403:
          Message.error('Access denied, please check permissions')
          break
        case 404:
          Message.error('Requested resource does not exist')
          break
        case 500:
          Message.error('Internal server error')
          break
        default:
          Message.error(`Request failed: ${error.response.status}`)
      }
    } else if (error.request) {
      // Request was sent but no response received
      if (error.message.includes('timeout')) {
        Message.error('Request timed out, please try again later')
      } else if (error.message.includes('Network Error')) {
        Message.error('Network error, please check your connection')
      } else {
        Message.error('Unable to connect to server')
      }
    } else {
      // Request configuration error
      Message.error('Request configuration error')
    }

    return Promise.reject(error)
  }
)

export default request
