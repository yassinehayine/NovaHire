import React, { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { Target, Layers, Clock, Globe, Sparkles, ArrowLeft, Zap } from 'lucide-react'
import LoadingSpinner from '../../components/common/LoadingSpinner'
import SectionHeader from '../../components/ui/SectionHeader'
import { interviewsApi } from '../../api/interviews'

const SummaryRow = ({ icon: Icon, label, value }) => (
  <div className="flex items-start gap-3 px-4 py-3">
    <Icon size={15} className="text-nova-400 flex-shrink-0 mt-0.5" />
    <span className="text-sm text-slate-500 w-24 flex-shrink-0">{label}</span>
    <span className="text-sm text-slate-200 flex-1">{value}</span>
  </div>
)

const InterviewSummaryPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [session, setSession] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [loadError, setLoadError] = useState(null)

  useEffect(() => {
    let active = true
    interviewsApi.getSession(id)
      .then((res) => { if (active) setSession(res.data) })
      .catch((err) => { if (active) setLoadError(err.response?.data?.message || 'Failed to load this interview') })
      .finally(() => { if (active) setIsLoading(false) })
    return () => { active = false }
  }, [id])

  useEffect(() => {
    if (session && session.interview.status !== 'COMPLETED') {
      navigate(`/interview/${id}`, { replace: true })
    }
  }, [session, id, navigate])

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-24">
        <LoadingSpinner size="lg" />
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

  if (!session) return null

  const { interview, questions, answers } = session
  const answersByQuestionId = Object.fromEntries(answers.map((a) => [a.questionId, a.answerText]))

  return (
    <div className="max-w-2xl mx-auto animate-fade-in space-y-8">
      <div>
        <h1 className="font-display text-3xl font-bold text-white">Interview Complete</h1>
        <p className="text-slate-400 text-sm mt-1">Here's a recap of your session</p>
      </div>

      <div className="card">
        <SectionHeader title="Configuration" />
        <div className="bg-surface-2 rounded-xl border border-white/8 divide-y divide-white/5">
          <SummaryRow icon={Target} label="Role" value={interview.targetRole} />
          <SummaryRow icon={Layers} label="Level" value={interview.experienceLevel} />
          <SummaryRow icon={Clock} label="Duration" value={`${interview.durationMinutes} min`} />
          <SummaryRow icon={Globe} label="Language" value={interview.language} />
        </div>
      </div>

      <div className="card">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-nova-600/15 border border-nova-500/25 flex items-center justify-center flex-shrink-0">
            <Sparkles size={18} className="text-nova-400" />
          </div>
          <div>
            <p className="text-sm font-medium text-slate-200">Scoring available in a future update</p>
            <p className="text-xs text-slate-500 mt-0.5">AI evaluation of your answers is coming soon</p>
          </div>
        </div>
      </div>

      <div className="space-y-4">
        <h2 className="font-display text-lg font-semibold text-white">Your Answers</h2>
        {questions.map((q, i) => (
          <div key={q.id} className="card">
            <p className="text-xs text-slate-500 mb-1.5">Question {i + 1}</p>
            <p className="text-sm font-medium text-slate-200 mb-3">{q.text}</p>
            {answersByQuestionId[q.id]?.trim() ? (
              <p className="text-sm text-slate-400 leading-relaxed whitespace-pre-wrap">{answersByQuestionId[q.id]}</p>
            ) : (
              <p className="text-sm text-slate-600 italic">No answer submitted</p>
            )}
          </div>
        ))}
      </div>

      <div className="flex items-center justify-between">
        <Link to="/dashboard" className="btn-secondary flex items-center gap-2">
          <ArrowLeft size={16} />
          Back to Dashboard
        </Link>
        <Link to="/interview/new" className="btn-primary flex items-center gap-2">
          <Zap size={16} />
          Start another interview
        </Link>
      </div>
    </div>
  )
}

export default InterviewSummaryPage
