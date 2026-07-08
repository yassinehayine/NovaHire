import React from 'react'
import { Sparkles } from 'lucide-react'

/** Full-panel loading state shown while the AI evaluates the interview (can take 10–30s). */
const EvaluationLoadingScreen = () => (
  <div className="flex flex-col items-center justify-center py-24 space-y-5 animate-fade-in text-center">
    <div className="relative w-16 h-16 flex items-center justify-center">
      <div className="absolute inset-0 rounded-full border-2 border-nova-500/20 border-t-nova-500 animate-spin" />
      <Sparkles size={24} className="text-nova-400 animate-pulse-slow" />
    </div>
    <div>
      <p className="text-slate-200 text-sm font-medium">AI is evaluating your answers…</p>
      <p className="text-slate-500 text-xs mt-1">Scoring each response and preparing your professional report</p>
    </div>
  </div>
)

export default EvaluationLoadingScreen
