export class FetchHttpError extends Error {
    constructor(
        readonly status: number,
        readonly body: string,
    ) {
        super(`HTTP ${status}: ${body.slice(0, 200)}`)
        this.name = 'FetchHttpError'
    }
}

export type FetchClientOptions = {
    baseURL?: string
    headers?: HeadersInit
}

function joinBaseAndPath(base: string, path: string): string {
    const b = base.replace(/\/$/, '')
    const p = path.startsWith('/') ? path : `/${path}`
    return `${b}${p}`
}

export class FetchClient {
    constructor(private readonly options: FetchClientOptions = {}) {}

    async request<T>(path: string, init: RequestInit = {}): Promise<T> {
        const base = this.options.baseURL ?? ''
        const url = base ? joinBaseAndPath(base, path) : path

        const headers = new Headers(this.options.headers)
        const extra = init.headers
        if (extra) {
            new Headers(extra).forEach((value, key) => {
                headers.set(key, value)
            })
        }
        if (init.body !== undefined && !headers.has('Content-Type')) {
            headers.set('Content-Type', 'application/json')
        }

        const res = await fetch(url, { ...init, headers })

        const text = await res.text()
        if (!res.ok) {
            throw new FetchHttpError(res.status, text)
        }

        if (!text) {
            return undefined as T
        }

        const contentType = res.headers.get('Content-Type') ?? ''
        if (contentType.includes('application/json')) {
            return JSON.parse(text) as T
        }

        return text as unknown as T
    }

    get<T>(path: string, init?: Omit<RequestInit, 'method' | 'body'>) {
        return this.request<T>(path, { ...init, method: 'GET' })
    }

    post<T>(path: string, body?: unknown, init?: Omit<RequestInit, 'method' | 'body'>) {
        return this.request<T>(path, {
            ...init,
            method: 'POST',
            body: body === undefined ? undefined : JSON.stringify(body),
        })
    }

    put<T>(path: string, body?: unknown, init?: Omit<RequestInit, 'method' | 'body'>) {
        return this.request<T>(path, {
            ...init,
            method: 'PUT',
            body: body === undefined ? undefined : JSON.stringify(body),
        })
    }

    patch<T>(path: string, body?: unknown, init?: Omit<RequestInit, 'method' | 'body'>) {
        return this.request<T>(path, {
            ...init,
            method: 'PATCH',
            body: body === undefined ? undefined : JSON.stringify(body),
        })
    }

    delete<T>(path: string, init?: Omit<RequestInit, 'method' | 'body'>) {
        return this.request<T>(path, { ...init, method: 'DELETE' })
    }
}
