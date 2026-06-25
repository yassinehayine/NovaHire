import React, { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { authApi } from '../api/auth'

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

  const logout = useCallback(() => {
    localStorage.removeItem('nova_token')
    localStorage.removeItem('nova_user')
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, isLoading, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used within AuthProvider')
  return context
}
