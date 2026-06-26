import React, { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { usersApi } from '../api/users'

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [isLoading, setIsLoading] = useState(true)

  const isAuthenticated = Boolean(user)

  const loadUserFromStorage = useCallback(() => {
    try {
      const token = localStorage.getItem('nova_token')
      const storedUser = localStorage.getItem('nova_user')
      if (token && storedUser) {
        setUser(JSON.parse(storedUser))
      }
    } catch {
      localStorage.removeItem('nova_token')
      localStorage.removeItem('nova_user')
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    loadUserFromStorage()
  }, [loadUserFromStorage])

  const login = useCallback((authResponse) => {
    const { accessToken, user: userData } = authResponse.data
    localStorage.setItem('nova_token', accessToken)
    localStorage.setItem('nova_user', JSON.stringify(userData))
    setUser(userData)
  }, [])

  /**
   * Call this after any profile update so the sidebar/header reflect
   * the latest name, avatar, etc. without forcing a full re-login.
   */
  const refreshUser = useCallback(async () => {
    try {
      const response = await usersApi.getMe()
      const userData = response.data
      localStorage.setItem('nova_user', JSON.stringify(userData))
      setUser(userData)
    } catch {
      // Token may be expired — the axios interceptor will redirect to /login
    }
  }, [])

  const logout = useCallback(() => {
    localStorage.removeItem('nova_token')
    localStorage.removeItem('nova_user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, isLoading, login, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
