import React from 'react'
import { Clock } from 'lucide-react'

const formatTime = (totalSeconds) => {
  const minutes = Math.floor(totalSeconds / 60)
  const seconds = totalSeconds % 60
  return `${minutes}:${String(seconds).padStart(2, '0')}`
}

const TimerBadge = ({ secondsRemaining, isExpired }) => {
  const isLow = !isExpired && secondsRemaining <= 60

  return (
    <div className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-mono font-medium border
      ${isExpired
        ? 'bg-red-500/10 border-red-500/30 text-red-400'
        : isLow
          ? 'bg-amber-500/10 border-amber-500/30 text-amber-400'
          : 'bg-white/5 border-white/10 text-slate-300'}`}
    >
      <Clock size={14} />
      {isExpired ? "Time's up" : formatTime(secondsRemaining)}
    </div>
  )
}

export default TimerBadge
