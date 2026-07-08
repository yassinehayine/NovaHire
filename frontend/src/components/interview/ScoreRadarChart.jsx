import React from 'react'

/**
 * Pure-SVG radar chart of the five category scores (0–100) — no chart dependency, matching the
 * project's hand-rolled visual components. `data` is [{ label, score }].
 */
const ScoreRadarChart = ({ data = [], size = 260 }) => {
  if (data.length < 3) return null
  const cx = size / 2
  const cy = size / 2
  const radius = size * 0.34
  const n = data.length
  const angle = (i) => (Math.PI * 2 * i) / n - Math.PI / 2
  const point = (i, r) => [cx + r * Math.cos(angle(i)), cy + r * Math.sin(angle(i))]
  const poly = (r) => data.map((_, i) => point(i, r).join(',')).join(' ')
  const valuePoly = data
    .map((d, i) => point(i, radius * (Math.max(0, Math.min(100, d.score ?? 0)) / 100)).join(','))
    .join(' ')

  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} className="max-w-full h-auto">
      {[0.25, 0.5, 0.75, 1].map((f) => (
        <polygon key={f} points={poly(radius * f)} className="fill-none stroke-white/10" />
      ))}
      {data.map((_, i) => {
        const [x, y] = point(i, radius)
        return <line key={i} x1={cx} y1={cy} x2={x} y2={y} className="stroke-white/10" />
      })}
      <polygon points={valuePoly} fill="rgba(98,113,245,0.25)" stroke="#6271f5" strokeWidth="2" strokeLinejoin="round" />
      {data.map((d, i) => {
        const [vx, vy] = point(i, radius * (Math.max(0, Math.min(100, d.score ?? 0)) / 100))
        return <circle key={`v${i}`} cx={vx} cy={vy} r="3" fill="#8196fa" />
      })}
      {data.map((d, i) => {
        const [x, y] = point(i, radius * 1.16)
        return (
          <text key={`l${i}`} x={x} y={y} textAnchor="middle" dominantBaseline="middle" className="fill-slate-400" style={{ fontSize: 10 }}>
            {d.label}
          </text>
        )
      })}
    </svg>
  )
}

export default ScoreRadarChart
