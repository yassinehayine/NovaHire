import React from 'react'

const ProgressBar = ({ current, total }) => (
  <div>
    <p className="text-xs text-slate-500 mb-1.5">Question {current + 1} of {total}</p>
    <div className="h-1.5 rounded-full bg-white/10 overflow-hidden">
      <div
        className="h-full bg-nova-500 rounded-full transition-all duration-300"
        style={{ width: `${total > 0 ? ((current + 1) / total) * 100 : 0}%` }}
      />
    </div>
  </div>
)

export default ProgressBar
