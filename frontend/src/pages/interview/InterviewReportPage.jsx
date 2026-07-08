import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import {
  ArrowLeft, Printer, Sparkles, AlertTriangle, RotateCcw,
  Target, Layers, Globe, Calendar, CheckCircle2, XCircle, ListChecks,
} from 'lucide-react'
import LoadingSpinner from '../../components/common/LoadingSpinner'
import ScoreRing from '../../components/interview/ScoreRing'
import ScoreBar from '../../components/interview/ScoreBar'
import ScoreRadarChart from '../../components/interview/ScoreRadarChart'
import RecommendationBadge from '../../components/interview/RecommendationBadge'
import { scoreTone } from '../../utils/evaluation'
import { interviewsApi } from '../../api/interviews'

const MetaItem = ({ icon: Icon, label, value }) => (
  <div className="flex items-center gap-2">
    <Icon size={14} className="text-slate-500 flex-shrink-0" />
    <span className="text-xs text-slate-500">{label}:</span>
    <span className="text-xs text-slate-300">{value}</span>
  </div>
)

const InsightBlock = ({ icon: Icon, iconClass, title, items, ordered = false }) => {
  if (!items || items.length === 0) return null
  const ListTag = ordered ? 'ol' : 'ul'
  return (
    <div>
      <div className="flex items-center gap-2 mb-2">
        <Icon size={15} className={iconClass} />
        <h3 className="text-sm font-semibold text-slate-200">{title}</h3>
      </div>
      <ListTag className="space-y-1.5 pl-1">
        {items.map((it, i) => (
          <li key={i} className="flex items-start gap-2 text-sm text-slate-300 leading-relaxed">
            <span className="text-slate-600 flex-shrink-0">{ordered ? `${i + 1}.` : '•'}</span>
            <span>{it}</span>
          </li>
        ))}
      </ListTag>
    </div>
  )
}

const InterviewReportPage = () => {
  const { id } = useParams()
  const [report, setReport] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)
  const [loadError, setLoadError] = useState(null)

  useEffect(() => {
    let active = true
    interviewsApi.getReport(id)
      .then((res) => { if (active) setReport(res.data) })
      .catch((err) => {
        if (!active) return
        if (err.response?.status === 404) setNotFound(true)
        else setLoadError(err.response?.data?.message || 'Failed to load the report')
      })
      .finally(() => { if (active) setIsLoading(false) })
    return () => { active = false }
  }, [id])

  if (isLoading) {
    return <div className="flex items-center justify-center py-24"><LoadingSpinner size="lg" /></div>
  }

  // Never evaluated → send the user to run it.
  if (notFound) {
    return (
      <div className="max-w-xl mx-auto text-center py-24 space-y-4 animate-fade-in">
        <Sparkles size={32} className="text-nova-400 mx-auto" />
        <p className="text-slate-200 font-medium">This interview hasn't been evaluated yet</p>
        <p className="text-slate-500 text-sm">Run the AI evaluation to generate a professional report.</p>
        <div className="flex items-center justify-center gap-3 pt-2">
          <Link to="/dashboard" className="btn-secondary inline-flex items-center gap-2">
            <ArrowLeft size={16} /> Dashboard
          </Link>
          <Link to={`/interview/${id}/results`} className="btn-primary inline-flex items-center gap-2">
            <Sparkles size={16} /> Evaluate now
          </Link>
        </div>
      </div>
    )
  }

  if (loadError) {
    return (
      <div className="max-w-xl mx-auto text-center py-24 space-y-4">
        <p className="text-red-400">{loadError}</p>
        <Link to="/dashboard" className="btn-secondary inline-flex items-center gap-2">Back to Dashboard</Link>
      </div>
    )
  }

  const { interview, evaluation: e } = report

  // Evaluation exists but failed — surface the state with a retry path to the results page.
  if (e?.status === 'FAILED') {
    return (
      <div className="max-w-xl mx-auto py-20 space-y-6 animate-fade-in">
        <div className="card border-red-500/30 bg-red-500/5">
          <div className="flex items-start gap-3">
            <AlertTriangle size={20} className="text-red-400 flex-shrink-0 mt-0.5" />
            <div>
              <p className="text-sm font-semibold text-slate-100">Report unavailable — evaluation failed</p>
              <p className="text-sm text-slate-400 mt-1">{e.errorMessage || 'The AI evaluation did not complete.'}</p>
            </div>
          </div>
        </div>
        <Link to={`/interview/${id}/results`} className="btn-primary inline-flex items-center gap-2">
          <RotateCcw size={16} /> Go to evaluation
        </Link>
      </div>
    )
  }

  const created = interview.completedAt || interview.createdAt
  const dateStr = created ? new Date(created).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' }) : '—'
  const radarData = [
    { label: 'Technical', score: e.technicalScore },
    { label: 'Communication', score: e.communicationScore },
    { label: 'Problem Solving', score: e.problemSolvingScore },
    { label: 'Confidence', score: e.confidenceScore },
    { label: 'Overall', score: e.overallScore },
  ]

  return (
    <div className="max-w-3xl mx-auto animate-fade-in space-y-6">
      {/* Toolbar (hidden on print) */}
      <div className="flex items-center justify-between print:hidden">
        <Link to={`/interview/${id}/results`} className="btn-secondary flex items-center gap-2">
          <ArrowLeft size={16} /> Back to results
        </Link>
        <button type="button" onClick={() => window.print()} className="btn-primary flex items-center gap-2">
          <Printer size={16} /> Print / Save PDF
        </button>
      </div>

      {/* Report sheet */}
      <div className="card space-y-8">
        {/* Report header */}
        <div className="border-b border-white/8 pb-6">
          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="text-xs uppercase tracking-widest text-nova-400 mb-1">Interview Report</p>
              <h1 className="font-display text-2xl font-bold text-white">{interview.targetRole}</h1>
            </div>
            <RecommendationBadge recommendation={e.recommendation} label={e.recommendationLabel} size="lg" />
          </div>
          <div className="flex flex-wrap gap-x-6 gap-y-2 mt-4">
            <MetaItem icon={Layers} label="Level" value={interview.experienceLevel} />
            <MetaItem icon={Target} label="Style" value={interview.interviewStyle} />
            <MetaItem icon={Globe} label="Language" value={interview.language} />
            <MetaItem icon={Calendar} label="Date" value={dateStr} />
          </div>
        </div>

        {/* Scores */}
        <div className="flex flex-col sm:flex-row items-center gap-8">
          <div className="flex flex-col items-center gap-2">
            <ScoreRing score={e.overallScore} size={140} />
            <span className="text-xs text-slate-500">Overall score</span>
          </div>
          <div className="flex-1 w-full space-y-4">
            <ScoreBar label="Technical" score={e.technicalScore} />
            <ScoreBar label="Communication" score={e.communicationScore} />
            <ScoreBar label="Problem Solving" score={e.problemSolvingScore} />
            <ScoreBar label="Confidence" score={e.confidenceScore} />
          </div>
          <div className="hidden md:block">
            <ScoreRadarChart data={radarData} size={220} />
          </div>
        </div>

        {/* Summary */}
        {e.summary && (
          <div className="border-t border-white/8 pt-6">
            <h3 className="text-sm font-semibold text-slate-200 mb-2">Assessment Summary</h3>
            <p className="text-sm text-slate-300 leading-relaxed">{e.summary}</p>
          </div>
        )}

        {/* Strengths / weaknesses / roadmap */}
        <div className="border-t border-white/8 pt-6 grid grid-cols-1 md:grid-cols-2 gap-6">
          <InsightBlock icon={CheckCircle2} iconClass="text-emerald-400" title="Strengths" items={e.strengths} />
          <InsightBlock icon={XCircle} iconClass="text-red-400" title="Weaknesses" items={e.weaknesses} />
        </div>
        <InsightBlock icon={ListChecks} iconClass="text-nova-400" title="Improvement Roadmap" items={e.improvementRoadmap} ordered />

        {/* Per-question breakdown */}
        <div className="border-t border-white/8 pt-6">
          <h3 className="text-sm font-semibold text-slate-200 mb-4">Per-Question Breakdown</h3>
          <div className="space-y-3">
            {e.questionEvaluations?.map((q, i) => {
              const tone = scoreTone((q.score ?? 0) * 10)
              return (
                <div key={q.questionId} className="flex items-start gap-3">
                  <span className={`px-2 py-0.5 rounded-md text-xs font-bold border flex-shrink-0 ${tone.bg} ${tone.border} ${tone.text}`}>
                    {q.score ?? 0}/10
                  </span>
                  <div className="min-w-0">
                    <p className="text-sm text-slate-300">{i + 1}. {q.questionText}</p>
                    {q.explanation && <p className="text-xs text-slate-500 mt-0.5">{q.explanation}</p>}
                  </div>
                </div>
              )
            })}
          </div>
        </div>

        {evaluation_footer(e)}
      </div>
    </div>
  )
}

/** Small provenance line so the report is self-describing. */
function evaluation_footer(e) {
  if (!e?.evaluatedAt) return null
  const when = new Date(e.evaluatedAt).toLocaleString()
  return (
    <p className="text-[11px] text-slate-600 border-t border-white/5 pt-4">
      Generated by NovaHire AI · {when}
    </p>
  )
}

export default InterviewReportPage
