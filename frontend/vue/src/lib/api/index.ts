import { type FetchClient } from './client'
import { ExampleApi } from './sdk/example.api'

export { FetchClient, FetchHttpError, type FetchClientOptions } from './client'

export class Api {
    example: ExampleApi

    constructor(private readonly client: FetchClient) {
        this.example = new ExampleApi(this.client)
    }
}
