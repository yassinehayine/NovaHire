import React from 'react'
import { Link } from 'react-router-dom'
import { Zap, Target, TrendingUp, Clock, ArrowRight, Sparkles } from 'lucide-react'
import { useAuth } from '../../context/AuthContext'

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

const DashboardPage = () => {
  const { user } = useAuth()

  const greeting = () => {
    const hour = new Date().getHours()
    if (hour < 12) return 'Good morning'
    if (hour < 18) return 'Good afternoon'
    return 'Good evening'
  }

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
        <StatCard icon={Zap} label="Total Interviews" value="0" subtext="Start your first one!" />
        <StatCard icon={Target} label="Avg. Score" value="—" subtext="No data yet" color="purple" />
        <StatCard icon={TrendingUp} label="Best Score" value="—" subtext="Keep practicing" color="green" />
        <StatCard icon={Clock} label="Practice Time" value="0m" subtext="This week" color="yellow" />
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
            Ready for your first interview?
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

      {/* Recent — placeholder for Sprint 5 */}
      <div>
        <h2 className="font-display text-lg font-semibold text-white mb-4">Recent Interviews</h2>
        <div className="card flex flex-col items-center justify-center py-16 text-center border-dashed">
          <div className="w-16 h-16 rounded-2xl bg-nova-600/10 border border-nova-600/20 flex items-center justify-center mb-4">
            <Zap size={28} className="text-nova-500" />
          </div>
          <p className="text-slate-300 font-medium mb-1">No interviews yet</p>
          <p className="text-slate-500 text-sm">Complete an interview to see your history here</p>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage
