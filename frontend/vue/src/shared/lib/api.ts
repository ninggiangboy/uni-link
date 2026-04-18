import {Api, FetchClient} from "@/lib/api";

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '/api'

export const api = new Api(new FetchClient({ baseURL }))
