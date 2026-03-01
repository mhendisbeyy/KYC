import type { LogEvent, Page } from '../types/log'

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

export async function fetchLogs(params: {
    page: number
    size: number
    level?: string
    q?: string
}): Promise<Page<LogEvent>> {
    const sp = new URLSearchParams()
    sp.set('page', String(params.page))
    sp.set('size', String(params.size))
    if (params.level) sp.set('level', params.level)
    if (params.q) sp.set('q', params.q)

    const res = await fetch(`${API_BASE}/api/logs?${sp.toString()}`)
    if (!res.ok) throw new Error(`HTTP ${res.status}`)
    return res.json()
}