import React from 'react'
import { scoreTone } from '../../utils/evaluation'

/** Circular 0–100 progress ring — used for the headline overall score. */
const ScoreRing = ({ score, size = 150, label, suffix = '/100' }) => {
  const pct = Math.max(0, Math.min(100, score ?? 0))
  const tone = scoreTone(pct)
  const stroke = 10
  const r = (size - stroke) / 2
  const circumference = 2 * Math.PI * r
  const offset = circumference - (pct / 100) * circumference

  return (
    <div className="relative inline-flex items-center justify-center" style={{ width: size, height: size }}>
      <svg width={size} height={size} className="-rotate-90">
        <circle cx={size / 2} cy={size / 2} r={r} strokeWidth={stroke} className="fill-none stroke-white/10" />
        <circle
          cx={size / 2} cy={size / 2} r={r} strokeWidth={stroke} strokeLinecap="round" fill="none"
          stroke={tone.stroke} strokeDasharray={circumference} strokeDashoffset={offset}
          style={{ transition: 'stroke-dashoffset 1s ease-out' }}
        />
      </svg>
      <div className="absolute flex flex-col items-center leading-none">
        <span className={`font-display font-bold ${tone.text}`} style={{ fontSize: size * 0.3 }}>
          {Math.round(pct)}
        </span>
        <span className="text-[10px] text-slate-500 mt-1">{suffix}</span>
        {label && <span className="text-[10px] uppercase tracking-wide text-slate-500 mt-0.5">{label}</span>}
      </div>
    </div>
  )
}

export default ScoreRing
