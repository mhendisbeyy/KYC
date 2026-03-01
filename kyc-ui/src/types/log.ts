export type LogEvent = {
    id: number
    eventTime: string
    level: 'TRACE' | 'DEBUG' | 'INFO' | 'WARN' | 'ERROR' | string
    logger: string
    message: string
    rawLine?: string
}

export type Page<T> = {
    content: T[]
    number: number
    size: number
    totalElements: number
    totalPages: number
}