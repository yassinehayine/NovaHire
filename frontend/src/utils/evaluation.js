/**
 * Shared visual tokens for the evaluation UI. Returns COMPLETE Tailwind class strings (never
 * interpolated fragments) so the JIT compiler emits them, plus raw hex for SVG stroke/fill.
 */

/** Tone for a 0–100 score. Pass question scores as score*10. */
export function scoreTone(pct) {
  const p = Math.max(0, Math.min(100, pct ?? 0))
  if (p >= 80) return { text: 'text-emerald-400', bg: 'bg-emerald-500/10', border: 'border-emerald-500/30', bar: 'bg-emerald-500', stroke: '#10b981' }
  if (p >= 65) return { text: 'text-nova-400',    bg: 'bg-nova-500/10',    border: 'border-nova-500/30',    bar: 'bg-nova-500',    stroke: '#6271f5' }
  if (p >= 45) return { text: 'text-amber-400',   bg: 'bg-amber-500/10',   border: 'border-amber-500/30',   bar: 'bg-amber-500',   stroke: '#f59e0b' }
  return { text: 'text-red-400', bg: 'bg-red-500/10', border: 'border-red-500/30', bar: 'bg-red-500', stroke: '#ef4444' }
}

const RECOMMENDATION_TONES = {
  STRONG_HIRE: { text: 'text-emerald-400', bg: 'bg-emerald-500/10', border: 'border-emerald-500/30', dot: 'bg-emerald-400' },
  HIRE:        { text: 'text-nova-400',    bg: 'bg-nova-500/10',    border: 'border-nova-500/30',    dot: 'bg-nova-400' },
  BORDERLINE:  { text: 'text-amber-400',   bg: 'bg-amber-500/10',   border: 'border-amber-500/30',   dot: 'bg-amber-400' },
  NO_HIRE:     { text: 'text-red-400',     bg: 'bg-red-500/10',     border: 'border-red-500/30',     dot: 'bg-red-400' },
}

export function recommendationTone(recommendation) {
  return RECOMMENDATION_TONES[recommendation] || RECOMMENDATION_TONES.BORDERLINE
}
