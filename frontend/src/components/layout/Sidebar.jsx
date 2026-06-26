import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import { LayoutDashboard, Zap, User, History, LogOut, ChevronRight } from 'lucide-react'
import Logo from '../common/Logo'
import { useAuth } from '../../context/AuthContext'
import toast from 'react-hot-toast'

const navItems = [
  { icon: LayoutDashboard, label: 'Dashboard', path: '/dashboard' },
  { icon: Zap, label: 'New Interview', path: '/interview/new', badge: 'AI' },
  { icon: User, label: 'Profile', path: '/profile' },
  { icon: History, label: 'History', path: '/history', disabled: true, soon: true },
]

const Sidebar = () => {
  const { user, logout } = useAuth()
  const location = useLocation()

  const handleLogout = () => {
    logout()
    toast.success('Signed out successfully')
  }

  return (
    <aside className="w-64 flex-shrink-0 h-screen bg-surface-1 border-r border-white/5 flex flex-col sticky top-0">
      {/* Logo */}
      <div className="p-6 border-b border-white/5">
        <Logo size="sm" />
      </div>

      {/* Nav */}
      <nav className="flex-1 p-4 space-y-1">
        {navItems.map(({ icon: Icon, label, path, badge, disabled, soon }) => {
          const isActive = location.pathname === path || (path !== '/dashboard' && location.pathname.startsWith(path))
          return (
            <Link
              key={path}
              to={disabled ? '#' : path}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150 group relative
                ${disabled ? 'cursor-not-allowed opacity-40' : 'cursor-pointer'}
                ${isActive
                  ? 'bg-nova-600/20 text-nova-300 border border-nova-600/30'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-white/5'
                }`}
              onClick={e => disabled && e.preventDefault()}
            >
              <Icon size={18} className={isActive ? 'text-nova-400' : ''} />
              <span className="flex-1">{label}</span>
              {badge && (
                <span className="text-xs bg-nova-600/30 text-nova-300 border border-nova-500/30 px-1.5 py-0.5 rounded-md font-mono">
                  {badge}
                </span>
              )}
              {soon && (
                <span className="text-xs bg-white/5 text-slate-500 px-1.5 py-0.5 rounded-md">soon</span>
              )}
              {isActive && <ChevronRight size={14} className="text-nova-500" />}
            </Link>
          )
        })}
      </nav>

      {/* User footer */}
      <div className="p-4 border-t border-white/5">
        <Link
          to="/profile"
          className="flex items-center gap-3 px-3 py-2 mb-1 rounded-xl hover:bg-white/5 transition-colors group"
        >
          <div className="w-8 h-8 rounded-full overflow-hidden border border-white/10 flex-shrink-0">
            {user?.avatarBase64 ? (
              <img src={user.avatarBase64} alt="avatar" className="w-full h-full object-cover" />
            ) : (
              <div className="w-full h-full bg-gradient-to-br from-nova-500 to-purple-600 flex items-center justify-center text-xs font-bold text-white">
                {user?.firstName?.[0]}{user?.lastName?.[0]}
              </div>
            )}
          </div>
          <div className="min-w-0 flex-1">
            <p className="text-sm font-medium text-slate-200 truncate group-hover:text-white transition-colors">{user?.firstName} {user?.lastName}</p>
            <p className="text-xs text-slate-500 truncate">{user?.email}</p>
          </div>
        </Link>
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm text-slate-400 hover:text-red-400 hover:bg-red-500/5 transition-all duration-150"
        >
          <LogOut size={18} />
          Sign out
        </button>
      </div>
    </aside>
  )
}

export default Sidebar
