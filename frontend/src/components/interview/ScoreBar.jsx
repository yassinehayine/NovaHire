import React from 'react'
import { scoreTone } from '../../utils/evaluation'

/** Labeled horizontal 0–100 bar — used for the four category scores. */
const ScoreBar = ({ label, score, icon: Icon }) => {
  const pct = Math.max(0, Math.min(100, score ?? 0))
  const tone = scoreTone(pct)
  return (
    <div className="space-y-1.5">
      <div className="flex items-center justify-between">
        <span className="flex items-center gap-1.5 text-sm text-slate-300">
          {Icon && <Icon size={14} className="text-slate-500" />}
          {label}
        </span>
        <span className={`text-sm font-semibold ${tone.text}`}>{Math.round(pct)}</span>
      </div>
      <div className="h-2 rounded-full bg-white/10 overflow-hidden">
        <div className={`h-full rounded-full ${tone.bar}`} style={{ width: `${pct}%`, transition: 'width 1s ease-out' }} />
      </div>
    </div>
  )
}

export default ScoreBar
