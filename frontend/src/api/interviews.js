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

  getSession: async (id) => {
    const response = await axiosInstance.get(`/interviews/${id}/session`)
    return response.data
  },

  saveAnswer: async (id, questionId, payload) => {
    const response = await axiosInstance.put(`/interviews/${id}/questions/${questionId}/answer`, payload)
    return response.data
  },

  finish: async (id) => {
    const response = await axiosInstance.post(`/interviews/${id}/finish`)
    return response.data
  },

  // Sprint 5 — AI evaluation & report.
  // evaluate() overrides the default 15s axios timeout: a batched Gemini evaluation
  // legitimately runs longer (backend eval timeout is 60s).
  evaluate: async (id) => {
    const response = await axiosInstance.post(`/interviews/${id}/evaluate`, null, { timeout: 90000 })
    return response.data
  },

  getEvaluation: async (id) => {
    const response = await axiosInstance.get(`/interviews/${id}/evaluation`)
    return response.data
  },

  getReport: async (id) => {
    const response = await axiosInstance.get(`/interviews/${id}/report`)
    return response.data
  },
}
