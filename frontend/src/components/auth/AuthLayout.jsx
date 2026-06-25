import React from 'react'
import Logo from '../common/Logo'

const AuthLayout = ({ children, title, subtitle }) => {
  return (
    <div className="min-h-screen flex bg-surface">
      {/* Left panel — branding */}
      <div className="hidden lg:flex flex-col justify-between w-[480px] flex-shrink-0 bg-nova-mesh bg-surface-1 p-12 border-r border-white/5 relative overflow-hidden">
        {/* Background orbs */}
        <div className="absolute top-0 left-0 w-full h-full pointer-events-none">
          <div className="absolute -top-24 -left-24 w-96 h-96 rounded-full bg-nova-600/10 blur-3xl" />
          <div className="absolute bottom-0 right-0 w-80 h-80 rounded-full bg-purple-600/10 blur-3xl" />
        </div>

        <Logo size="md" />

        <div className="relative z-10 space-y-8">
          <div>
            <h2 className="font-display text-4xl font-bold text-white leading-tight mb-4">
              Practice smarter.<br />
              <span className="gradient-text">Get hired faster.</span>
            </h2>
            <p className="text-slate-400 text-lg leading-relaxed">
              NovaHire uses AI to generate real interview questions, evaluate your answers, and give you the feedback you need to land your dream job.
            </p>
          </div>

          <div className="space-y-4">
            {[
              { icon: '🎯', text: 'Tailored questions for your target role' },
              { icon: '🤖', text: 'AI-powered answer evaluation & scoring' },
              { icon: '📊', text: 'Detailed feedback to improve each session' },
            ].map((item, i) => (
              <div key={i} className="flex items-center gap-3 text-slate-300">
                <span className="text-xl">{item.icon}</span>
                <span className="text-sm">{item.text}</span>
              </div>
            ))}
          </div>
        </div>

        <p className="text-slate-600 text-xs">© 2024 NovaHire. All rights reserved.</p>
      </div>

      {/* Right panel — form */}
      <div className="flex-1 flex items-center justify-center p-6">
        <div className="w-full max-w-md animate-slide-up">
          {/* Mobile logo */}
          <div className="lg:hidden mb-8 flex justify-center">
            <Logo size="md" />
          </div>

          <div className="card nova-glow">
            <div className="mb-8">
              <h1 className="font-display text-2xl font-bold text-white mb-2">{title}</h1>
              <p className="text-slate-400 text-sm">{subtitle}</p>
            </div>
            {children}
          </div>
        </div>
      </div>
    </div>
  )
}

export default AuthLayout
