import React from 'react'
import { Code2, Users } from 'lucide-react'

const CATEGORY_META = {
  TECHNICAL: { icon: Code2, label: 'Technical' },
  BEHAVIORAL: { icon: Users, label: 'Behavioral' },
}

const QuestionCard = ({ question }) => {
  const meta = CATEGORY_META[question.category] ?? CATEGORY_META.TECHNICAL
  const Icon = meta.icon

  return (
    <div className="card">
      <div className="flex items-center gap-2 mb-4">
        <span className="flex items-center gap-1.5 text-xs font-medium text-nova-300 bg-nova-600/15 border border-nova-500/25 px-2 py-1 rounded-md">
          <Icon size={13} />
          {meta.label}
        </span>
        <span className="text-xs font-medium text-slate-400 bg-white/5 border border-white/10 px-2 py-1 rounded-md">
          {question.difficulty}
        </span>
      </div>
      <p className="text-lg text-slate-100 leading-relaxed">{question.text}</p>
    </div>
  )
}

export default QuestionCard
