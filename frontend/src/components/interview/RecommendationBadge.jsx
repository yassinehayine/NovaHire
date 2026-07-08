import React from 'react'
import { Award } from 'lucide-react'
import { recommendationTone } from '../../utils/evaluation'

/** Hiring recommendation pill: Strong Hire / Hire / Borderline / No Hire. */
const RecommendationBadge = ({ recommendation, label, size = 'md' }) => {
  const tone = recommendationTone(recommendation)
  const dims = size === 'lg' ? 'px-4 py-2 text-sm' : 'px-3 py-1 text-xs'
  return (
    <span className={`inline-flex items-center gap-2 rounded-full border font-semibold ${tone.bg} ${tone.border} ${tone.text} ${dims}`}>
      <Award size={size === 'lg' ? 16 : 13} />
      {label || recommendation || 'Not evaluated'}
    </span>
  )
}

export default RecommendationBadge
