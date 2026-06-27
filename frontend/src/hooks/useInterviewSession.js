import { useState, useEffect, useMemo, useCallback } from 'react'
import toast from 'react-hot-toast'
import { interviewsApi } from '../api/interviews'
import { useDebouncedCallback } from './useDebouncedCallback'

const AUTO_SAVE_DELAY_MS = 800

/**
 * Loads an interview session and drives the question-by-question flow: navigation,
 * debounced auto-save (flushed immediately on Prev/Next/Finish so keystrokes are never
 * lost), and finishing. Follows the same state-booleans-plus-actions shape as useProfile.js.
 */
export const useInterviewSession = (interviewId) => {
  const [session, setSession] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [loadError, setLoadError] = useState(null)
  const [currentIndex, setCurrentIndex] = useState(0)
  const [answersByQuestionId, setAnswersByQuestionId] = useState({})
  const [isSaving, setIsSaving] = useState(false)
  const [isFinishing, setIsFinishing] = useState(false)

  useEffect(() => {
    let active = true
    setIsLoading(true)
    setLoadError(null)

    interviewsApi.getSession(interviewId)
      .then((res) => {
        if (!active) return
        const data = res.data
        setSession(data)
        const map = {}
        data.answers.forEach((a) => { map[a.questionId] = a.answerText ?? '' })
        setAnswersByQuestionId(map)
      })
      .catch((err) => {
        if (!active) return
        setLoadError(err.response?.data?.message || 'Failed to load this interview')
      })
      .finally(() => { if (active) setIsLoading(false) })

    return () => { active = false }
  }, [interviewId])

  const questions = session?.questions ?? []
  const currentQuestion = questions[currentIndex] ?? null

  const deadline = useMemo(() => {
    if (!session?.startedAt || !session?.durationMinutes) return null
    return new Date(session.startedAt).getTime() + session.durationMinutes * 60000
  }, [session?.startedAt, session?.durationMinutes])

  const persistAnswer = useCallback(async (questionId, text) => {
    setIsSaving(true)
    try {
      await interviewsApi.saveAnswer(interviewId, questionId, { answerText: text })
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to save your answer'
      toast.error(msg)
    } finally {
      setIsSaving(false)
    }
  }, [interviewId])

  const [debouncedPersist, flushPersist] = useDebouncedCallback(persistAnswer, AUTO_SAVE_DELAY_MS)

  const setCurrentAnswerText = useCallback((text) => {
    if (!currentQuestion) return
    const questionId = currentQuestion.id
    setAnswersByQuestionId((prev) => ({ ...prev, [questionId]: text }))
    debouncedPersist(questionId, text)
  }, [currentQuestion, debouncedPersist])

  const goTo = useCallback((index) => {
    flushPersist()
    setCurrentIndex((prev) => {
      const next = Math.max(0, Math.min(questions.length - 1, index))
      return Number.isNaN(next) ? prev : next
    })
  }, [flushPersist, questions.length])

  const goNext = useCallback(() => goTo(currentIndex + 1), [goTo, currentIndex])
  const goPrev = useCallback(() => goTo(currentIndex - 1), [goTo, currentIndex])

  const finishInterview = useCallback(async () => {
    flushPersist()
    setIsFinishing(true)
    try {
      await interviewsApi.finish(interviewId)
      return true
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to finish the interview'
      toast.error(msg)
      return false
    } finally {
      setIsFinishing(false)
    }
  }, [interviewId, flushPersist])

  return {
    session,
    isLoading,
    loadError,
    questions,
    currentQuestion,
    currentIndex,
    currentAnswerText: currentQuestion ? (answersByQuestionId[currentQuestion.id] ?? '') : '',
    setCurrentAnswerText,
    isFirst: currentIndex === 0,
    isLast: currentIndex === questions.length - 1,
    goNext,
    goPrev,
    isSaving,
    isFinishing,
    finishInterview,
    deadline,
  }
}
