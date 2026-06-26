import React, { useState, useRef } from 'react'
import { User, Lock, FileText, Camera, Check } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { useProfile } from '../../hooks/useProfile'
import FormField from '../../components/ui/FormField'
import SectionHeader from '../../components/ui/SectionHeader'
import LoadingSpinner from '../../components/common/LoadingSpinner'

// ── Constants ──────────────────────────────────────────────────────────────────

const EXPERIENCE_LEVELS = [
  { value: 'JUNIOR', label: 'Junior (0–2 years)' },
  { value: 'MID',    label: 'Mid-level (2–5 years)' },
  { value: 'SENIOR', label: 'Senior (5–10 years)' },
  { value: 'LEAD',   label: 'Lead / Principal (10+ years)' },
]

const LANGUAGES = [
  { value: 'ENGLISH', label: '🇬🇧 English' },
  { value: 'FRENCH',  label: '🇫🇷 French' },
  { value: 'GERMAN',  label: '🇩🇪 German' },
]

const COUNTRIES = [
  { value: 'MOROCCO',  label: '🇲🇦 Morocco' },
  { value: 'FRANCE',   label: '🇫🇷 France' },
  { value: 'GERMANY',  label: '🇩🇪 Germany' },
  { value: 'CANADA',   label: '🇨🇦 Canada' },
  { value: 'USA',      label: '🇺🇸 United States' },
  { value: 'UAE',      label: '🇦🇪 UAE' },
  { value: 'UK',       label: '🇬🇧 United Kingdom' },
  { value: 'OTHER',    label: '🌍 Other' },
]

// ── Avatar uploader ────────────────────────────────────────────────────────────

const AvatarUploader = ({ user, onUpload, isUploading }) => {
  const fileRef = useRef(null)

  const handleFile = (e) => {
    const file = e.target.files?.[0]
    if (!file) return

    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
    if (!allowedTypes.includes(file.type)) {
      alert('Please select a JPEG, PNG, or WebP image.')
      return
    }
    if (file.size > 2 * 1024 * 1024) {
      alert('Image must be smaller than 2MB.')
      return
    }

    const reader = new FileReader()
    reader.onload = (ev) => onUpload(ev.target.result)
    reader.readAsDataURL(file)
  }

  const initials = `${user?.firstName?.[0] ?? ''}${user?.lastName?.[0] ?? ''}`

  return (
    <div className="flex items-center gap-6">
      <div className="relative group">
        <div className="w-20 h-20 rounded-2xl overflow-hidden border-2 border-white/10">
          {user?.avatarBase64 ? (
            <img src={user.avatarBase64} alt="Avatar" className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full bg-gradient-to-br from-nova-500 to-purple-600 flex items-center justify-center text-white text-2xl font-bold">
              {initials}
            </div>
          )}
        </div>
        <button
          type="button"
          onClick={() => fileRef.current?.click()}
          disabled={isUploading}
          className="absolute inset-0 rounded-2xl bg-black/60 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
        >
          {isUploading ? <LoadingSpinner size="sm" /> : <Camera size={18} className="text-white" />}
        </button>
      </div>
      <div>
        <p className="text-sm font-medium text-slate-200 mb-1">Profile photo</p>
        <p className="text-xs text-slate-500 mb-2">JPEG, PNG, or WebP — max 2MB</p>
        <button
          type="button"
          onClick={() => fileRef.current?.click()}
          disabled={isUploading}
          className="btn-secondary text-xs px-3 py-1.5"
        >
          {isUploading ? 'Uploading…' : 'Change photo'}
        </button>
        <input ref={fileRef} type="file" accept="image/jpeg,image/png,image/webp" className="hidden" onChange={handleFile} />
      </div>
    </div>
  )
}

// ── CV uploader (client-side text extraction) ──────────────────────────────────

const CvUploader = ({ hasCv, onUpload, isUploading }) => {
  const fileRef = useRef(null)
  const [fileName, setFileName] = useState(null)

  const handleFile = async (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    if (file.type !== 'application/pdf') {
      alert('Please select a PDF file.')
      return
    }
    setFileName(file.name)

    // Read as text via FileReader — simple extraction for plain-text PDFs.
    // For Sprint 7 we can add pdf.js for richer extraction if needed.
    const reader = new FileReader()
    reader.onload = (ev) => {
      const text = ev.target.result
      onUpload(text)
    }
    reader.readAsText(file)
  }

  return (
    <div
      onClick={() => fileRef.current?.click()}
      className="flex items-center gap-4 p-4 rounded-xl border border-dashed border-white/20 hover:border-nova-500/50 hover:bg-nova-600/5 cursor-pointer transition-all group"
    >
      <div className="w-10 h-10 rounded-xl bg-white/5 group-hover:bg-nova-600/10 flex items-center justify-center flex-shrink-0 transition-colors">
        {isUploading ? <LoadingSpinner size="sm" /> : <FileText size={18} className="text-slate-400" />}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-slate-200">
          {fileName ?? (hasCv ? 'CV uploaded — click to replace' : 'Upload your CV / Résumé')}
        </p>
        <p className="text-xs text-slate-500">PDF · The AI uses this to personalise your interview questions</p>
      </div>
      {hasCv && !fileName && <Check size={16} className="text-green-400 flex-shrink-0" />}
      <input ref={fileRef} type="file" accept="application/pdf" className="hidden" onChange={handleFile} />
    </div>
  )
}

// ── Main page ──────────────────────────────────────────────────────────────────

const ProfilePage = () => {
  const { user } = useAuth()
  const { isSaving, isChangingPassword, isUploadingAvatar, isUploadingCv,
          updateProfile, changePassword, updateAvatar, uploadCv } = useProfile()

  // Profile form state
  const [profile, setProfile] = useState({
    firstName:         user?.firstName ?? '',
    lastName:          user?.lastName ?? '',
    email:             user?.email ?? '',
    targetRole:        user?.targetRole ?? '',
    experienceLevel:   user?.experienceLevel ?? '',
    yearsOfExperience: user?.yearsOfExperience ?? '',
    preferredLanguage: user?.preferredLanguage ?? 'ENGLISH',
    targetCountry:     user?.targetCountry ?? '',
  })

  // Password form state
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' })
  const [pwErrors, setPwErrors] = useState({})
  const [showPasswords, setShowPasswords] = useState(false)

  const handleProfileChange = (e) => {
    const { name, value } = e.target
    setProfile(prev => ({ ...prev, [name]: value }))
  }

  const handleProfileSubmit = async (e) => {
    e.preventDefault()
    await updateProfile({
      ...profile,
      yearsOfExperience: profile.yearsOfExperience ? Number(profile.yearsOfExperience) : null,
    })
  }

  const validatePasswords = () => {
    const errors = {}
    if (!passwords.currentPassword) errors.currentPassword = 'Required'
    if (!passwords.newPassword) errors.newPassword = 'Required'
    else if (passwords.newPassword.length < 8) errors.newPassword = 'Minimum 8 characters'
    if (!passwords.confirmPassword) errors.confirmPassword = 'Required'
    else if (passwords.newPassword !== passwords.confirmPassword) errors.confirmPassword = 'Passwords do not match'
    setPwErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handlePasswordSubmit = async (e) => {
    e.preventDefault()
    if (!validatePasswords()) return
    const ok = await changePassword(passwords)
    if (ok) setPasswords({ currentPassword: '', newPassword: '', confirmPassword: '' })
  }

  return (
    <div className="max-w-2xl space-y-8 animate-fade-in">
      <div>
        <h1 className="font-display text-3xl font-bold text-white">Profile</h1>
        <p className="text-slate-400 text-sm mt-1">Manage your account settings and interview preferences</p>
      </div>

      {/* ── Avatar section ─────────────────────────────────────────── */}
      <div className="card">
        <SectionHeader title="Profile Photo" subtitle="Used across your account" />
        <AvatarUploader user={user} onUpload={updateAvatar} isUploading={isUploadingAvatar} />
      </div>

      {/* ── Profile form ───────────────────────────────────────────── */}
      <form onSubmit={handleProfileSubmit} className="card space-y-6">
        <SectionHeader
          title="Personal Information"
          subtitle="Your basic account details"
          action={
            <button type="submit" disabled={isSaving} className="btn-primary flex items-center gap-2 text-sm px-4 py-2">
              {isSaving ? <LoadingSpinner size="sm" /> : <Check size={15} />}
              {isSaving ? 'Saving…' : 'Save'}
            </button>
          }
        />

        <div className="grid grid-cols-2 gap-4">
          <FormField label="First Name" required>
            <input type="text" name="firstName" value={profile.firstName} onChange={handleProfileChange} className="input-field" />
          </FormField>
          <FormField label="Last Name" required>
            <input type="text" name="lastName" value={profile.lastName} onChange={handleProfileChange} className="input-field" />
          </FormField>
        </div>

        <FormField label="Email" required>
          <input type="email" name="email" value={profile.email} onChange={handleProfileChange} className="input-field" />
        </FormField>

        <div className="border-t border-white/5 pt-6">
          <p className="text-sm font-semibold text-slate-300 mb-4 flex items-center gap-2">
            <User size={15} className="text-nova-400" />
            Interview Preferences
            <span className="text-xs font-normal text-slate-500 ml-1">— used by the AI to tailor your sessions</span>
          </p>

          <div className="space-y-4">
            <FormField label="Target Role" hint="e.g. Backend Developer, Data Scientist, Product Manager">
              <input
                type="text"
                name="targetRole"
                value={profile.targetRole}
                onChange={handleProfileChange}
                placeholder="Software Engineer"
                className="input-field"
              />
            </FormField>

            <div className="grid grid-cols-2 gap-4">
              <FormField label="Experience Level">
                <select name="experienceLevel" value={profile.experienceLevel} onChange={handleProfileChange} className="input-field">
                  <option value="">Select level</option>
                  {EXPERIENCE_LEVELS.map(l => <option key={l.value} value={l.value}>{l.label}</option>)}
                </select>
              </FormField>
              <FormField label="Years of Experience">
                <input
                  type="number"
                  name="yearsOfExperience"
                  value={profile.yearsOfExperience}
                  onChange={handleProfileChange}
                  min="0" max="50"
                  placeholder="e.g. 3"
                  className="input-field"
                />
              </FormField>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <FormField label="Preferred Language" hint="Language for AI-generated questions">
                <select name="preferredLanguage" value={profile.preferredLanguage} onChange={handleProfileChange} className="input-field">
                  {LANGUAGES.map(l => <option key={l.value} value={l.value}>{l.label}</option>)}
                </select>
              </FormField>
              <FormField label="Target Country" hint="Hiring norms vary by country">
                <select name="targetCountry" value={profile.targetCountry} onChange={handleProfileChange} className="input-field">
                  <option value="">Select country</option>
                  {COUNTRIES.map(c => <option key={c.value} value={c.value}>{c.label}</option>)}
                </select>
              </FormField>
            </div>
          </div>
        </div>
      </form>

      {/* ── CV upload ──────────────────────────────────────────────── */}
      <div className="card">
        <SectionHeader
          title="CV / Résumé"
          subtitle="The AI reads your CV to ask role-specific follow-up questions"
        />
        <CvUploader hasCv={user?.hasCv} onUpload={uploadCv} isUploading={isUploadingCv} />
      </div>

      {/* ── Password ───────────────────────────────────────────────── */}
      <form onSubmit={handlePasswordSubmit} className="card space-y-5">
        <SectionHeader
          title="Security"
          subtitle="Change your password"
          action={
            <button type="submit" disabled={isChangingPassword} className="btn-secondary flex items-center gap-2 text-sm px-4 py-2">
              {isChangingPassword ? <LoadingSpinner size="sm" /> : <Lock size={15} />}
              {isChangingPassword ? 'Saving…' : 'Update password'}
            </button>
          }
        />

        {[
          { name: 'currentPassword', label: 'Current Password' },
          { name: 'newPassword',     label: 'New Password' },
          { name: 'confirmPassword', label: 'Confirm New Password' },
        ].map(({ name, label }) => (
          <FormField key={name} label={label} error={pwErrors[name]}>
            <input
              type={showPasswords ? 'text' : 'password'}
              name={name}
              value={passwords[name]}
              onChange={e => {
                setPasswords(prev => ({ ...prev, [e.target.name]: e.target.value }))
                if (pwErrors[name]) setPwErrors(prev => ({ ...prev, [name]: '' }))
              }}
              className={`input-field ${pwErrors[name] ? 'border-red-500/50' : ''}`}
            />
          </FormField>
        ))}

        <label className="flex items-center gap-2 cursor-pointer text-sm text-slate-400 hover:text-slate-300">
          <input
            type="checkbox"
            checked={showPasswords}
            onChange={e => setShowPasswords(e.target.checked)}
            className="rounded border-white/20 bg-surface-2"
          />
          Show passwords
        </label>
      </form>
    </div>
  )
}

export default ProfilePage
