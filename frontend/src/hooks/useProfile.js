import { useState } from 'react'
import { usersApi } from '../api/users'
import { useAuth } from '../context/AuthContext'
import toast from 'react-hot-toast'

/**
 * Encapsulates all profile mutation operations.
 * Each action calls refreshUser() on success so global state stays in sync.
 */
export const useProfile = () => {
  const { refreshUser } = useAuth()
  const [isSaving, setIsSaving] = useState(false)
  const [isChangingPassword, setIsChangingPassword] = useState(false)
  const [isUploadingAvatar, setIsUploadingAvatar] = useState(false)
  const [isUploadingCv, setIsUploadingCv] = useState(false)

  const updateProfile = async (data) => {
    setIsSaving(true)
    try {
      await usersApi.updateProfile(data)
      await refreshUser()
      toast.success('Profile saved')
      return true
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save profile'
      toast.error(msg)
      return false
    } finally {
      setIsSaving(false)
    }
  }

  const changePassword = async (data) => {
    setIsChangingPassword(true)
    try {
      await usersApi.changePassword(data)
      toast.success('Password changed successfully')
      return true
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to change password'
      toast.error(msg)
      return false
    } finally {
      setIsChangingPassword(false)
    }
  }

  const updateAvatar = async (base64DataUrl) => {
    setIsUploadingAvatar(true)
    try {
      await usersApi.updateAvatar(base64DataUrl)
      await refreshUser()
      toast.success('Avatar updated')
      return true
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to update avatar'
      toast.error(msg)
      return false
    } finally {
      setIsUploadingAvatar(false)
    }
  }

  const uploadCv = async (cvText) => {
    setIsUploadingCv(true)
    try {
      await usersApi.updateCv(cvText)
      await refreshUser()
      toast.success('CV uploaded — the AI will use it in your next interview')
      return true
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to upload CV'
      toast.error(msg)
      return false
    } finally {
      setIsUploadingCv(false)
    }
  }

  return {
    isSaving, isChangingPassword, isUploadingAvatar, isUploadingCv,
    updateProfile, changePassword, updateAvatar, uploadCv,
  }
}
