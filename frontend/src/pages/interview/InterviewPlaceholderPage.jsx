import React from 'react'
import { useParams, Link } from 'react-router-dom'
import { Zap, ArrowLeft, Clock } from 'lucide-react'

/**
 * Sprint 2 placeholder for the interview session page.
 * Shows the created interview ID and a status card.
 * Sprint 3 replaces this with the full AI-question flow.
 */
const InterviewPlaceholderPage = () => {
  const { id } = useParams()

  return (
    <div className="max-w-xl mx-auto animate-fade-in">
      <Link to="/dashboard" className="flex items-center gap-2 text-slate-400 hover:text-slate-200 text-sm mb-8 transition-colors">
        <ArrowLeft size={15} />
        Back to Dashboard
      </Link>

      <div className="card text-center py-16 space-y-6">
        <div className="w-16 h-16 rounded-2xl bg-nova-600/20 border border-nova-500/30 flex items-center justify-center mx-auto">
          <Zap size={28} className="text-nova-400" />
        </div>

        <div>
          <p className="text-xs text-slate-500 font-mono mb-2">Interview #{id}</p>
          <h1 className="font-display text-2xl font-bold text-white mb-2">
            Session created!
          </h1>
          <p className="text-slate-400 text-sm leading-relaxed max-w-sm mx-auto">
            Your interview configuration is saved. The AI-powered question engine is coming in <span className="text-nova-400 font-medium">Sprint 3</span>.
          </p>
        </div>

        <div className="flex items-center justify-center gap-2 text-slate-500 text-xs">
          <Clock size={13} />
          <span>Sprint 3 · Gemini API integration</span>
        </div>

        <Link to="/interview/new" className="btn-secondary inline-flex items-center gap-2 mx-auto">
          Create another
        </Link>
      </div>
    </div>
  )
}

export default InterviewPlaceholderPage
