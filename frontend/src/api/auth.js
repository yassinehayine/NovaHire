import axiosInstance from './axios'

export const authApi = {
  register: async (data) => {
    const response = await axiosInstance.post('/auth/register', data)
    return response.data
  },
  login: async (data) => {
    const response = await axiosInstance.post('/auth/login', data)
    return response.data
  },
  getMe: async () => {
    const response = await axiosInstance.get('/users/me')
    return response.data
  },
}
