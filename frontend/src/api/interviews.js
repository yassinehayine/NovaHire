import axiosInstance from './axios'

export const interviewsApi = {
  create: async (data) => {
    const response = await axiosInstance.post('/interviews', data)
    return response.data
  },

  getAll: async () => {
    const response = await axiosInstance.get('/interviews')
    return response.data
  },

  getById: async (id) => {
    const response = await axiosInstance.get(`/interviews/${id}`)
    return response.data
  },

  getDashboardStats: async () => {
    const response = await axiosInstance.get('/interviews/stats/dashboard')
    return response.data
  },
}
