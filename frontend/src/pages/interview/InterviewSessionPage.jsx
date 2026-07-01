import React, { useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { ChevronLeft, ChevronRight, Flag, AlertTriangle } from 'lucide-react'
import LoadingSpinner from '../../components/common/LoadingSpinner'
import ProgressBar from '../../components/interview/ProgressBar'
import TimerBadge from '../../components/interview/TimerBadge'
import QuestionCard from '../../components/interview/QuestionCard'
import { useInterviewSession } from '../../hooks/useInterviewSession'
import { useCountdown } from '../../hooks/useCountdown'

const InterviewSessionPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()

  const {
    session, isLoading, loadError,
    questions, currentQuestion, currentIndex,
    currentAnswerText, setCurrentAnswerText,
    isFirst, isLast, goNext, goPrev,
    isSaving, isFinishing, finishInterview,
    deadline,
  } = useInterviewSession(id)

  const { secondsRemaining, isExpired } = useCountdown(deadline)

  useEffect(() => {
    if (session?.interview?.status === 'COMPLETED') {
      navigate(`/interview/${id}/summary`, { replace: true })
    }
  }, [session?.interview?.status, id, navigate])

  const handleFinish = async () => {
    const ok = await finishInterview()
    if (ok) navigate(`/interview/${id}/summary`)
  }

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center py-24 space-y-4">
        <div className="w-10 h-10 border-2 border-nova-500/20 border-t-nova-500 rounded-full animate-spin" />
        <p className="text-slate-300 text-sm font-medium">Generating your personalized questions…</p>
        <p className="text-slate-500 text-xs">AI is tailoring the interview to your profile</p>
      </div>
    )
  }

  if (loadError) {
    return (
      <div className="max-w-xl mx-auto text-center py-24 space-y-4">
        <p className="text-red-400">{loadError}</p>
        <Link to="/dashboard" className="btn-secondary inline-flex items-center gap-2">
          Back to Dashboard
        </Link>
      </div>
    )
  }

  if (!currentQuestion) {
    return null
  }

  return (
    <div className="max-w-2xl mx-auto animate-fade-in space-y-6">
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1">
          <ProgressBar current={currentIndex} total={questions.length} />
        </div>
        <TimerBadge secondsRemaining={secondsRemaining} isExpired={isExpired} />
      </div>

      <QuestionCard question={currentQuestion} />

      <div>
        <textarea
          className="input-field min-h-[180px] resize-y"
          placeholder="Type your answer here…"
          value={currentAnswerText}
          onChange={(e) => setCurrentAnswerText(e.target.value)}
          disabled={isExpired}
        />
        <div className="flex items-center justify-between mt-2">
          <p className="text-xs text-slate-500">
            {isExpired ? '' : isSaving ? 'Saving…' : currentAnswerText.trim() ? 'Saved' : ''}
          </p>
        </div>
      </div>

      {isExpired && (
        <div className="flex items-center gap-3 p-4 rounded-xl bg-red-500/10 border border-red-500/30 text-red-300 text-sm">
          <AlertTriangle size={18} className="flex-shrink-0" />
          Time's up — review your answers and click Finish to submit.
        </div>
      )}

      <div className="flex items-center justify-between">
        <button
          type="button"
          onClick={goPrev}
          disabled={isFirst}
          className="btn-secondary flex items-center gap-2 disabled:opacity-30"
        >
          <ChevronLeft size={16} />
          Previous
        </button>

        {isExpired || isLast ? (
          <button
            type="button"
            onClick={handleFinish}
            disabled={isFinishing}
            className="btn-primary flex items-center gap-2"
          >
            {isFinishing ? (
              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            ) : (
              <Flag size={16} />
            )}
            {isFinishing ? 'Finishing…' : 'Finish Interview'}
          </button>
        ) : (
          <button
            type="button"
            onClick={goNext}
            className="btn-primary flex items-center gap-2"
          >
            Next
            <ChevronRight size={16} />
          </button>
        )}
      </div>
    </div>
  )
}

export default InterviewSessionPage
