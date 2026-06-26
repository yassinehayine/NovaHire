import React from 'react'

const SectionHeader = ({ title, subtitle, action }) => (
  <div className="flex items-start justify-between mb-6">
    <div>
      <h2 className="font-display text-lg font-semibold text-white">{title}</h2>
      {subtitle && <p className="text-slate-400 text-sm mt-0.5">{subtitle}</p>}
    </div>
    {action}
  </div>
)

export default SectionHeader
