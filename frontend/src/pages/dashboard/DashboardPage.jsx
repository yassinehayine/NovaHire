import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Zap, Target, TrendingUp, CheckCircle2, ArrowRight, Sparkles, ChevronRight } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'
import { interviewsApi } from '../../api/interviews'

const StatCard = ({ icon: Icon, label, value, subtext, color = 'nova' }) => (
  <div className="card flex items-start gap-4">
    <div className={`p-2.5 rounded-xl bg-${color}-600/20 border border-${color}-500/20 flex-shrink-0`}>
      <Icon size={20} className={`text-${color}-400`} />
    </div>
    <div>
      <p className="text-2xl font-display font-bold text-white">{value}</p>
      <p className="text-sm font-medium text-slate-300">{label}</p>
      {subtext && <p className="text-xs text-slate-500 mt-0.5">{subtext}</p>}
    </div>
  </div>
)

const StatSkeleton = () => (
  <div className="card flex items-start gap-4 animate-pulse">
    <div className="w-11 h-11 rounded-xl bg-white/5 flex-shrink-0" />
    <div className="space-y-2 pt-1">
      <div className="h-6 w-12 bg-white/5 rounded" />
      <div className="h-3 w-20 bg-white/5 rounded" />
    </div>
  </div>
)

const STATUS_META = {
  COMPLETED:   { label: 'Completed',   cls: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30' },
  IN_PROGRESS: { label: 'In Progress', cls: 'text-nova-400 bg-nova-500/10 border-nova-500/30' },
  CREATED:     { label: 'Not started', cls: 'text-slate-400 bg-white/5 border-white/15' },
  CANCELLED:   { label: 'Cancelled',   cls: 'text-red-400 bg-red-500/10 border-red-500/30' },
}

// Completed sessions open their AI results; anything else resumes the session.
const linkFor = (i) => (i.status === 'COMPLETED' ? `/interview/${i.id}/results` : `/interview/${i.id}`)

const RecentRow = ({ interview }) => {
  const meta = STATUS_META[interview.status] || STATUS_META.CREATED
  const date = interview.createdAt
    ? new Date(interview.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric' })
    : ''
  return (
    <Link
      to={linkFor(interview)}
      className="flex items-center gap-4 px-4 py-3 hover:bg-white/5 transition-colors"
    >
      <div className="w-9 h-9 rounded-lg bg-nova-600/15 border border-nova-500/25 flex items-center justify-center flex-shrink-0">
        <Zap size={16} className="text-nova-400" />
      </div>
      <div className="min-w-0 flex-1">
        <p className="text-sm font-medium text-slate-200 truncate">{interview.targetRole}</p>
        <p className="text-xs text-slate-500 truncate">
          {interview.experienceLevel} · {interview.language}{date ? ` · ${date}` : ''}
        </p>
      </div>
      {interview.status === 'COMPLETED' && interview.score != null && (
        <span className="text-sm font-semibold text-slate-200 flex-shrink-0">
          {interview.score}<span className="text-xs text-slate-500">/100</span>
        </span>
      )}
      <span className={`text-xs px-2 py-0.5 rounded-full border flex-shrink-0 ${meta.cls}`}>{meta.label}</span>
      <ChevronRight size={16} className="text-slate-600 flex-shrink-0" />
    </Link>
  )
}

const DashboardPage = () => {
  const { user } = useAuth()
  const [stats, setStats] = useState(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    let active = true
    setIsLoading(true)
    setError(null)
    interviewsApi.getDashboardStats()
      .then((res) => { if (active) setStats(res.data) })
      .catch((err) => { if (active) setError(err.response?.data?.message || 'Failed to load your dashboard') })
      .finally(() => { if (active) setIsLoading(false) })
    return () => { active = false }
  }, [])

  const greeting = () => {
    const hour = new Date().getHours()
    if (hour < 12) return 'Good morning'
    if (hour < 18) return 'Good afternoon'
    return 'Good evening'
  }

  const hasInterviews = (stats?.totalInterviews ?? 0) > 0
  const avgScore = stats?.averageScore != null ? stats.averageScore : null
  const bestScore = stats?.bestScore != null ? stats.bestScore : null
  const recent = stats?.recentInterviews ?? []

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div>
          <p className="text-slate-400 text-sm mb-1">{greeting()}</p>
          <h1 className="font-display text-3xl font-bold text-white">
            {user?.firstName} {user?.lastName} 👋
          </h1>
          <p className="text-slate-400 mt-2">Ready to practice today?</p>
        </div>
        <Link to="/interview/new" className="btn-primary flex items-center gap-2">
          <Zap size={18} />
          Start Interview
        </Link>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {isLoading ? (
          <>
            <StatSkeleton /><StatSkeleton /><StatSkeleton /><StatSkeleton />
          </>
        ) : error ? (
          <div className="sm:col-span-2 lg:col-span-4 card border-red-500/30 bg-red-500/5 text-red-300 text-sm">
            {error}
          </div>
        ) : (
          <>
            <StatCard
              icon={Zap}
              label="Total Interviews"
              value={stats.totalInterviews}
              subtext={hasInterviews ? 'All sessions' : 'Start your first one!'}
            />
            <StatCard
              icon={CheckCircle2}
              label="Completed"
              value={stats.completedInterviews}
              subtext={stats.inProgressInterviews > 0 ? `${stats.inProgressInterviews} in progress` : 'Finished sessions'}
              color="green"
            />
            <StatCard
              icon={Target}
              label="Avg. Score"
              value={avgScore != null ? avgScore : '—'}
              subtext={avgScore != null ? 'out of 100' : 'No scores yet'}
              color="purple"
            />
            <StatCard
              icon={TrendingUp}
              label="Best Score"
              value={bestScore != null ? bestScore : '—'}
              subtext={bestScore != null ? 'out of 100' : 'Keep practicing'}
              color="yellow"
            />
          </>
        )}
      </div>

      {/* CTA Card */}
      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-nova-900/80 to-purple-900/40 border border-nova-700/30 p-8">
        <div className="absolute top-0 right-0 w-64 h-64 bg-nova-600/10 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2" />
        <div className="relative z-10">
          <div className="flex items-center gap-2 text-nova-400 mb-3">
            <Sparkles size={18} />
            <span className="text-sm font-medium">AI-Powered</span>
          </div>
          <h2 className="font-display text-2xl font-bold text-white mb-2">
            {hasInterviews ? 'Ready for another round?' : 'Ready for your first interview?'}
          </h2>
          <p className="text-slate-400 text-sm mb-6 max-w-md">
            Our AI generates real-world questions based on your target role, evaluates your responses, and gives you actionable feedback to improve.
          </p>
          <Link to="/interview/new" className="btn-primary inline-flex items-center gap-2">
            Start practicing now
            <ArrowRight size={16} />
          </Link>
        </div>
      </div>

      {/* Recent interviews */}
      <div>
        <h2 className="font-display text-lg font-semibold text-white mb-4">Recent Interviews</h2>

        {isLoading ? (
          <div className="card divide-y divide-white/5 p-0 overflow-hidden">
            {[0, 1, 2].map((i) => (
              <div key={i} className="flex items-center gap-4 px-4 py-3 animate-pulse">
                <div className="w-9 h-9 rounded-lg bg-white/5 flex-shrink-0" />
                <div className="flex-1 space-y-2">
                  <div className="h-3.5 w-40 bg-white/5 rounded" />
                  <div className="h-3 w-24 bg-white/5 rounded" />
                </div>
              </div>
            ))}
          </div>
        ) : !error && recent.length > 0 ? (
          <div className="card divide-y divide-white/5 p-0 overflow-hidden">
            {recent.map((interview) => (
              <RecentRow key={interview.id} interview={interview} />
            ))}
          </div>
        ) : (
          <div className="card flex flex-col items-center justify-center py-16 text-center border-dashed">
            <div className="w-16 h-16 rounded-2xl bg-nova-600/10 border border-nova-600/20 flex items-center justify-center mb-4">
              <Zap size={28} className="text-nova-500" />
            </div>
            <p className="text-slate-300 font-medium mb-1">No interviews yet</p>
            <p className="text-slate-500 text-sm">Complete an interview to see your history here</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default DashboardPage
