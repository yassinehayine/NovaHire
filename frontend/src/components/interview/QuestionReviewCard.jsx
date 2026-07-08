import React, { useState } from 'react'
import { ChevronDown, CheckCircle2, XCircle, Lightbulb } from 'lucide-react'
import { scoreTone } from '../../utils/evaluation'

const FeedbackRow = ({ icon: Icon, iconClass, title, text }) => {
  if (!text || !text.trim()) return null
  return (
    <div className="flex items-start gap-2.5">
      <Icon size={15} className={`${iconClass} flex-shrink-0 mt-0.5`} />
      <div>
        <p className="text-xs font-medium text-slate-400">{title}</p>
        <p className="text-sm text-slate-300 leading-relaxed whitespace-pre-wrap">{text}</p>
      </div>
    </div>
  )
}

/** One expandable question verdict: score /10, the answer, and structured feedback. */
const QuestionReviewCard = ({ item, index, defaultOpen = false }) => {
  const [open, setOpen] = useState(defaultOpen)
  const tone = scoreTone((item.score ?? 0) * 10)

  return (
    <div className="card">
      <button
        type="button"
        onClick={() => setOpen((o) => !o)}
        className="w-full flex items-start justify-between gap-3 text-left"
      >
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs text-slate-500">Question {index + 1}</span>
            <span className="text-[10px] uppercase tracking-wide text-slate-600">{item.category}</span>
          </div>
          <p className="text-sm font-medium text-slate-200">{item.questionText}</p>
        </div>
        <div className="flex items-center gap-2 flex-shrink-0">
          <span className={`px-2.5 py-1 rounded-lg text-sm font-bold border ${tone.bg} ${tone.border} ${tone.text}`}>
            {item.score ?? 0}<span className="text-xs font-normal opacity-70">/10</span>
          </span>
          <ChevronDown size={16} className={`text-slate-500 transition-transform ${open ? 'rotate-180' : ''}`} />
        </div>
      </button>

      {open && (
        <div className="mt-4 space-y-4 animate-fade-in">
          <div>
            <p className="text-xs font-medium text-slate-500 mb-1">Your answer</p>
            {item.candidateAnswer?.trim() ? (
              <p className="text-sm text-slate-400 leading-relaxed whitespace-pre-wrap bg-surface-2 rounded-xl border border-white/8 p-3">
                {item.candidateAnswer}
              </p>
            ) : (
              <p className="text-sm text-slate-600 italic">No answer submitted</p>
            )}
          </div>

          <div className="space-y-3 border-t border-white/5 pt-4">
            <FeedbackRow icon={CheckCircle2} iconClass="text-emerald-400" title="Strengths" text={item.strengths} />
            <FeedbackRow icon={XCircle} iconClass="text-red-400" title="Weaknesses" text={item.weaknesses} />
            <FeedbackRow icon={Lightbulb} iconClass="text-amber-400" title="How to improve" text={item.improvementSuggestions} />
            {item.explanation?.trim() && (
              <p className="text-xs text-slate-500 italic leading-relaxed border-l-2 border-white/10 pl-3">
                {item.explanation}
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

export default QuestionReviewCard
