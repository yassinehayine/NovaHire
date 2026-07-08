import React from 'react'
import { useParams, Link } from 'react-router-dom'
import {
  ArrowLeft, FileText, RotateCcw, AlertTriangle, Sparkles,
  CheckCircle2, XCircle, ListChecks, Cpu, MessageSquare, Lightbulb, Gauge,
} from 'lucide-react'
import SectionHeader from '../../components/ui/SectionHeader'
import ScoreRing from '../../components/interview/ScoreRing'
import ScoreBar from '../../components/interview/ScoreBar'
import ScoreRadarChart from '../../components/interview/ScoreRadarChart'
import RecommendationBadge from '../../components/interview/RecommendationBadge'
import QuestionReviewCard from '../../components/interview/QuestionReviewCard'
import EvaluationLoadingScreen from '../../components/interview/EvaluationLoadingScreen'
import { useInterviewEvaluation } from '../../hooks/useInterviewEvaluation'

const InsightList = ({ icon: Icon, iconClass, title, items, ordered = false }) => {
  if (!items || items.length === 0) return null
  const ListTag = ordered ? 'ol' : 'ul'
  return (
    <div className="card">
      <div className="flex items-center gap-2 mb-3">
        <Icon size={16} className={iconClass} />
        <h3 className="text-sm font-semibold text-slate-200">{title}</h3>
      </div>
      <ListTag className="space-y-2">
        {items.map((it, i) => (
          <li key={i} className="flex items-start gap-2.5 text-sm text-slate-300 leading-relaxed">
            {ordered
              ? <span className="flex-shrink-0 w-5 h-5 rounded-full bg-nova-600/20 border border-nova-500/30 text-nova-300 text-xs flex items-center justify-center mt-0.5">{i + 1}</span>
              : <span className={`flex-shrink-0 w-1.5 h-1.5 rounded-full mt-2 bg-current ${iconClass}`} />}
            <span>{it}</span>
          </li>
        ))}
      </ListTag>
    </div>
  )
}

const InterviewResultsPage = () => {
  const { id } = useParams()
  const { evaluation, phase, error, retry } = useInterviewEvaluation(id)

  if (phase === 'loading' || phase === 'evaluating') {
    return <EvaluationLoadingScreen />
  }

  if (phase === 'error') {
    return (
      <div className="max-w-xl mx-auto text-center py-24 space-y-4">
        <AlertTriangle size={32} className="text-red-400 mx-auto" />
        <p className="text-red-400">{error}</p>
        <div className="flex items-center justify-center gap-3">
          <Link to="/dashboard" className="btn-secondary inline-flex items-center gap-2">
            <ArrowLeft size={16} /> Back to Dashboard
          </Link>
          <button type="button" onClick={retry} className="btn-primary inline-flex items-center gap-2">
            <RotateCcw size={16} /> Try again
          </button>
        </div>
      </div>
    )
  }

  if (phase === 'failed') {
    return (
      <div className="max-w-xl mx-auto py-20 space-y-6 animate-fade-in">
        <div className="card border-red-500/30 bg-red-500/5">
          <div className="flex items-start gap-3">
            <AlertTriangle size={20} className="text-red-400 flex-shrink-0 mt-0.5" />
            <div>
              <p className="text-sm font-semibold text-slate-100">Evaluation didn't complete</p>
              <p className="text-sm text-slate-400 mt-1">
                {evaluation?.errorMessage || 'The AI evaluation failed. Your answers are safe — you can retry.'}
              </p>
              {evaluation?.attemptCount > 0 && (
                <p className="text-xs text-slate-600 mt-2">Attempts: {evaluation.attemptCount}</p>
              )}
            </div>
          </div>
        </div>
        <div className="flex items-center justify-between">
          <Link to={`/interview/${id}/summary`} className="btn-secondary flex items-center gap-2">
            <ArrowLeft size={16} /> Back to Summary
          </Link>
          <button type="button" onClick={retry} className="btn-primary flex items-center gap-2">
            <RotateCcw size={16} /> Retry evaluation
          </button>
        </div>
      </div>
    )
  }

  // phase === 'ready'
  const e = evaluation
  const radarData = [
    { label: 'Technical', score: e.technicalScore },
    { label: 'Communication', score: e.communicationScore },
    { label: 'Problem Solving', score: e.problemSolvingScore },
    { label: 'Confidence', score: e.confidenceScore },
    { label: 'Overall', score: e.overallScore },
  ]

  return (
    <div className="max-w-3xl mx-auto animate-fade-in space-y-8">
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-nova-400 mb-1">
            <Sparkles size={16} />
            <span className="text-xs font-medium">AI Evaluation</span>
          </div>
          <h1 className="font-display text-3xl font-bold text-white">Interview Results</h1>
        </div>
        <Link to={`/interview/${id}/report`} className="btn-secondary flex items-center gap-2 flex-shrink-0">
          <FileText size={16} /> Full report
        </Link>
      </div>

      {/* Overall + recommendation + summary */}
      <div className="card">
        <div className="flex flex-col sm:flex-row items-center gap-6">
          <ScoreRing score={e.overallScore} />
          <div className="flex-1 text-center sm:text-left space-y-3">
            <RecommendationBadge recommendation={e.recommendation} label={e.recommendationLabel} size="lg" />
            {e.summary && <p className="text-sm text-slate-300 leading-relaxed">{e.summary}</p>}
          </div>
        </div>
      </div>

      {/* Category scores + radar */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="card">
          <SectionHeader title="Category Scores" />
          <div className="space-y-4">
            <ScoreBar label="Technical" score={e.technicalScore} icon={Cpu} />
            <ScoreBar label="Communication" score={e.communicationScore} icon={MessageSquare} />
            <ScoreBar label="Problem Solving" score={e.problemSolvingScore} icon={Lightbulb} />
            <ScoreBar label="Confidence" score={e.confidenceScore} icon={Gauge} />
          </div>
        </div>
        <div className="card flex items-center justify-center">
          <ScoreRadarChart data={radarData} />
        </div>
      </div>

      {/* Strengths / Weaknesses / Roadmap */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <InsightList icon={CheckCircle2} iconClass="text-emerald-400" title="Strengths" items={e.strengths} />
        <InsightList icon={XCircle} iconClass="text-red-400" title="Weaknesses" items={e.weaknesses} />
      </div>
      <InsightList icon={ListChecks} iconClass="text-nova-400" title="Improvement Roadmap" items={e.improvementRoadmap} ordered />

      {/* Question-by-question */}
      <div className="space-y-4">
        <h2 className="font-display text-lg font-semibold text-white">Question-by-Question Review</h2>
        {e.questionEvaluations?.map((item, i) => (
          <QuestionReviewCard key={item.questionId} item={item} index={i} defaultOpen={i === 0} />
        ))}
      </div>

      <div className="flex items-center justify-between pt-2">
        <Link to="/dashboard" className="btn-secondary flex items-center gap-2">
          <ArrowLeft size={16} /> Back to Dashboard
        </Link>
        <Link to={`/interview/${id}/report`} className="btn-primary flex items-center gap-2">
          <FileText size={16} /> View full report
        </Link>
      </div>
    </div>
  )
}

export default InterviewResultsPage
