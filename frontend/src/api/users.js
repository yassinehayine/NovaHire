import axiosInstance from './axios'

export const usersApi = {
  getMe: async () => {
    const response = await axiosInstance.get('/users/me')
    return response.data
  },

  updateProfile: async (data) => {
    const response = await axiosInstance.put('/users/me', data)
    return response.data
  },

  changePassword: async (data) => {
    const response = await axiosInstance.put('/users/me/password', data)
    return response.data
  },

  updateAvatar: async (avatarBase64) => {
    const response = await axiosInstance.put('/users/me/avatar', { avatarBase64 })
    return response.data
  },

  updateCv: async (cvText) => {
    const response = await axiosInstance.put('/users/me/cv', { cvText })
    return response.data
  },
}
