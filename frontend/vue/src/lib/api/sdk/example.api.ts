import { queryOptions } from '@tanstack/vue-query'
import type { FetchClient } from '../client'
import type { ExampleResponse } from './example.type'

export class ExampleApi {
    constructor(private readonly client: FetchClient) {}

    hello() {
        return queryOptions({
            queryKey: ['hello'],
            queryFn: ({ signal }) => this.client.get<ExampleResponse>('/hello', { signal }),
        })
    }
}
