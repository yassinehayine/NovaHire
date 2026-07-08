import { useState, useEffect, useCallback, useRef } from 'react'
import { interviewsApi } from '../api/interviews'

/**
 * Drives the evaluation lifecycle for the Results page.
 *
 * Phases:
 *   'loading'    — checking whether a stored evaluation already exists
 *   'evaluating' — AI is running (shows the loading screen)
 *   'ready'      — a COMPLETED evaluation is available
 *   'failed'     — server stored status=FAILED (AI failed after retry); manual retry allowed
 *   'error'      — the request itself failed (network / non-404)
 *
 * The trigger is idempotent server-side (pessimistic lock + status check), so React 18 StrictMode
 * double-invoking this effect can never launch two real evaluations.
 */
export const useInterviewEvaluation = (interviewId) => {
  const [evaluation, setEvaluation] = useState(null)
  const [phase, setPhase] = useState('loading')
  const [error, setError] = useState(null)
  const mounted = useRef(true)

  useEffect(() => {
    mounted.current = true
    return () => { mounted.current = false }
  }, [])

  const runEvaluate = useCallback(async () => {
    if (mounted.current) { setPhase('evaluating'); setError(null) }
    try {
      const res = await interviewsApi.evaluate(interviewId)
      if (!mounted.current) return
      const data = res.data
      setEvaluation(data)
      setPhase(data.status === 'COMPLETED' ? 'ready' : 'failed')
    } catch (err) {
      if (!mounted.current) return
      setError(err.response?.data?.message || 'Evaluation failed. Please try again.')
      setPhase('error')
    }
  }, [interviewId])

  useEffect(() => {
    let active = true
    setPhase('loading')
    setError(null)

    interviewsApi.getEvaluation(interviewId)
      .then((res) => {
        if (!active) return
        const data = res.data
        setEvaluation(data)
        if (data.status === 'COMPLETED') setPhase('ready')
        else if (data.status === 'FAILED') setPhase('failed')
        else runEvaluate() // PENDING / IN_PROGRESS — resume (idempotent)
      })
      .catch((err) => {
        if (!active) return
        if (err.response?.status === 404) {
          runEvaluate() // never evaluated → start now
        } else {
          setError(err.response?.data?.message || 'Failed to load evaluation')
          setPhase('error')
        }
      })

    return () => { active = false }
  }, [interviewId, runEvaluate])

  return { evaluation, phase, error, retry: runEvaluate }
}
