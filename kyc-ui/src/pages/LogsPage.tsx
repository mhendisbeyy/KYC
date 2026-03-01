import { useEffect, useMemo, useState } from 'react'
import type { LogEvent, Page } from '../types/log'
import { fetchLogs } from '../api/logsApi'

function Pill({ level }: { level: string }) {
    return <span style={{
        padding: '3px 8px',
        borderRadius: 999,
        border: '1px solid rgba(255,255,255,0.2)',
        fontSize: 12
    }}>{level}</span>
}

export default function LogsPage() {
    const [page, setPage] = useState(0)
    const [size, setSize] = useState(20)
    const [level, setLevel] = useState('')
    const [q, setQ] = useState('')
    const [data, setData] = useState<Page<LogEvent> | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const queryKey = useMemo(() => ({ page, size, level, q }), [page, size, level, q])

    // Detay dialog
    const [selected, setSelected] = useState<LogEvent | null>(null)

    useEffect(() => {
        let cancelled = false
        setLoading(true)
        setError(null)

        fetchLogs({
            page: queryKey.page,
            size: queryKey.size,
            level: queryKey.level || undefined,
            q: queryKey.q || undefined,
        })
            .then((json) => { if (!cancelled) setData(json) })
            .catch((e) => { if (!cancelled) setError(e?.message || String(e)) })
            .finally(() => { if (!cancelled) setLoading(false) })

        return () => { cancelled = true }
    }, [queryKey])

    const canPrev = (data?.number ?? 0) > 0
    const canNext = (data?.number ?? 0) < ((data?.totalPages ?? 1) - 1)

    return (
        <div style={{ maxWidth: 1200, margin: '0 auto', padding: 20 }}>
            <h1>Loglar</h1>
            <p style={{ opacity: 0.8 }}>
                Backend: <code>{import.meta.env.VITE_API_BASE || 'http://localhost:8080'}</code>
            </p>

            {/* Filtreler */}
            <div style={{
                display: 'grid',
                gridTemplateColumns: '1fr 2fr 1fr',
                gap: 12,
                margin: '12px 0'
            }}>
                <label style={{ display: 'grid', gap: 6 }}>
                    Level
                    <select
                        value={level}
                        onChange={(e) => { setPage(0); setLevel(e.target.value) }}
                        style={{ padding: 10, borderRadius: 10 }}
                    >
                        <option value="">Hepsi</option>
                        <option value="TRACE">TRACE</option>
                        <option value="DEBUG">DEBUG</option>
                        <option value="INFO">INFO</option>
                        <option value="WARN">WARN</option>
                        <option value="ERROR">ERROR</option>
                    </select>
                </label>

                <label style={{ display: 'grid', gap: 6 }}>
                    Arama (logger/message)
                    <input
                        value={q}
                        onChange={(e) => { setPage(0); setQ(e.target.value) }}
                        placeholder="payment, kyc, risk..."
                        style={{ padding: 10, borderRadius: 10 }}
                    />
                </label>

                <label style={{ display: 'grid', gap: 6 }}>
                    Sayfa boyutu
                    <select
                        value={size}
                        onChange={(e) => { setPage(0); setSize(Number(e.target.value)) }}
                        style={{ padding: 10, borderRadius: 10 }}
                    >
                        <option value={10}>10</option>
                        <option value={20}>20</option>
                        <option value={50}>50</option>
                        <option value={100}>100</option>
                    </select>
                </label>
            </div>

            {/* Sayfalama */}
            <div style={{ display: 'flex', gap: 10, alignItems: 'center', marginBottom: 10 }}>
                <button disabled={!canPrev || loading} onClick={() => setPage(p => Math.max(0, p - 1))}>◀ Önceki</button>
                <button disabled={!canNext || loading} onClick={() => setPage(p => p + 1)}>Sonraki ▶</button>
                {data && (
                    <span style={{ opacity: 0.8 }}>
                        Toplam <b>{data.totalElements}</b> | Sayfa <b>{data.number + 1}</b>/<b>{data.totalPages}</b>
                    </span>
                )}
            </div>

            {/* Liste */}
            <div style={{ border: '1px solid rgba(0,0,0,0.1)', borderRadius: 12, overflow: 'auto' }}>
                {loading && <div style={{ padding: 14 }}>Yükleniyor…</div>}
                {error && <div style={{ padding: 14, color: 'crimson' }}>Hata: {error}</div>}

                {!loading && !error && data && (
                    <table style={{ width: '100%', borderCollapse: 'collapse', minWidth: 900 }}>
                        <thead>
                            <tr style={{ background: 'rgba(0,0,0,0.03)' }}>
                                <th style={{ textAlign: 'left', padding: 12 }}>Time</th>
                                <th style={{ textAlign: 'left', padding: 12 }}>Level</th>
                                <th style={{ textAlign: 'left', padding: 12 }}>Logger</th>
                                <th style={{ textAlign: 'left', padding: 12 }}>Message</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.content.map((x) => (
                                <tr
                                    key={x.id}
                                    onClick={() => setSelected(x)}
                                    style={{ cursor: 'pointer', borderTop: '1px solid rgba(0,0,0,0.06)' }}
                                >
                                    <td style={{ padding: 12, fontFamily: 'monospace' }}>{x.eventTime}</td>
                                    <td style={{ padding: 12 }}><Pill level={x.level} /></td>
                                    <td style={{ padding: 12, fontFamily: 'monospace' }}>{x.logger}</td>
                                    <td style={{ padding: 12 }}>{x.message}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}

                {!loading && !error && !data && <div style={{ padding: 14 }}>Henüz veri yok.</div>}
            </div>

            {/* Detay */}
            {selected && (
                <div
                    onClick={() => setSelected(null)}
                    style={{
                        position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)',
                        display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16
                    }}
                >
                    <div
                        onClick={(e) => e.stopPropagation()}
                        style={{ width: 'min(900px, 100%)', background: '#fff', borderRadius: 14, padding: 16 }}
                    >
                        <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
                            <h3 style={{ margin: 0 }}>Log Detayı</h3>
                            <button onClick={() => setSelected(null)}>Kapat</button>
                        </div>

                        <div style={{ marginTop: 10, display: 'grid', gap: 8 }}>
                            <div><b>Time:</b> <code>{selected.eventTime}</code></div>
                            <div><b>Level:</b> {selected.level}</div>
                            <div><b>Logger:</b> <code>{selected.logger}</code></div>
                            <div><b>Message:</b> {selected.message}</div>
                            {selected.rawLine && (
                                <div>
                                    <b>Raw Line:</b>
                                    <pre style={{ whiteSpace: 'pre-wrap', background: '#f6f6f6', padding: 10, borderRadius: 10 }}>
                                        {selected.rawLine}
                                    </pre>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}