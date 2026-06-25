import React from 'react'

const Logo = ({ size = 'md', showText = true }) => {
  const sizes = {
    sm: { icon: 24, text: 'text-lg' },
    md: { icon: 32, text: 'text-xl' },
    lg: { icon: 44, text: 'text-3xl' },
  }
  const s = sizes[size]

  return (
    <div className="flex items-center gap-3">
      <div
        className="relative flex items-center justify-center rounded-xl bg-gradient-to-br from-nova-500 to-purple-600 shadow-lg shadow-nova-900/50 flex-shrink-0"
        style={{ width: s.icon, height: s.icon }}
      >
        <svg viewBox="0 0 24 24" fill="none" style={{ width: s.icon * 0.6, height: s.icon * 0.6 }}>
          <path d="M12 2L2 7l10 5 10-5-10-5z" fill="white" opacity="0.9"/>
          <path d="M2 17l10 5 10-5M2 12l10 5 10-5" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" opacity="0.7"/>
        </svg>
        <div className="absolute inset-0 rounded-xl bg-white/10 blur-sm" />
      </div>
      {showText && (
        <span className={`font-display font-bold ${s.text} tracking-tight`}>
          <span className="gradient-text">Nova</span>
          <span className="text-white">Hire</span>
        </span>
      )}
    </div>
  )
}

export default Logo
