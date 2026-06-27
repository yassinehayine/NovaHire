import { useEffect, useState } from 'react'

/**
 * Purely client-side countdown to `deadlineMs` (epoch ms). No backend polling —
 * the deadline itself (startedAt + durationMinutes) comes from the server once.
 */
export const useCountdown = (deadlineMs) => {
  const [secondsRemaining, setSecondsRemaining] = useState(() =>
    deadlineMs ? Math.max(0, Math.round((deadlineMs - Date.now()) / 1000)) : 0
  )

  useEffect(() => {
    if (!deadlineMs) return

    const tick = () => setSecondsRemaining(Math.max(0, Math.round((deadlineMs - Date.now()) / 1000)))
    tick()
    const interval = setInterval(tick, 1000)
    return () => clearInterval(interval)
  }, [deadlineMs])

  return { secondsRemaining, isExpired: secondsRemaining <= 0 }
}
