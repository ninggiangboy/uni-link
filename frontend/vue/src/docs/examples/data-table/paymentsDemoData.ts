/** Subset of doc payments fixture for DataTable demos (mirrors frontend-sample payments.data). */
export type DemoPayment = {
  id: string
  amount: string
  status: 'pending' | 'processing' | 'success' | 'failed'
  email: string
  paymentMethod: 'credit_card' | 'debit_card' | 'paypal' | 'bank_transfer'
  transactionDate: string
  paymentReference: string
}

export const demoPayments: DemoPayment[] = [
  {
    id: '4185ed4b-9dcd-4e7a-8497-9b90bfd05e87',
    amount: '495',
    status: 'pending',
    email: 'Eliezer.Greenholt-Ernser38@gmail.com',
    paymentMethod: 'debit_card',
    transactionDate: '2025-08-30',
    paymentReference: 'PAY-TPR3EC',
  },
  {
    id: '34caaea9-44ee-4519-a6e8-4061f916d4fe',
    amount: '218',
    status: 'processing',
    email: 'Robbie28@gmail.com',
    paymentMethod: 'credit_card',
    transactionDate: '2025-08-30',
    paymentReference: 'PAY-RIZWBE',
  },
  {
    id: '50cb9a9b-c322-42b9-8e12-e9a323589fb9',
    amount: '359',
    status: 'success',
    email: 'Geo80@hotmail.com',
    paymentMethod: 'credit_card',
    transactionDate: '2025-09-06',
    paymentReference: 'PAY-XTKBMD',
  },
  {
    id: '2760a2ea-8e25-48cc-9288-4bd6dcbc87b9',
    amount: '383',
    status: 'success',
    email: 'Jovanny_Dibbert61@gmail.com',
    paymentMethod: 'debit_card',
    transactionDate: '2025-08-29',
    paymentReference: 'PAY-GGBKHT',
  },
  {
    id: 'b5b5949a-2243-4e1d-92f4-6b7396da89a5',
    amount: '333',
    status: 'pending',
    email: 'Connor29@yahoo.com',
    paymentMethod: 'credit_card',
    transactionDate: '2025-09-03',
    paymentReference: 'PAY-0OHZTW',
  },
  {
    id: 'e9d04e2b-bed9-4262-ab75-9640da3fca47',
    amount: '701',
    status: 'failed',
    email: 'Mackenzie19@gmail.com',
    paymentMethod: 'debit_card',
    transactionDate: '2025-08-24',
    paymentReference: 'PAY-R3UEAT',
  },
  {
    id: '48fe3e82-1f31-4e6e-a561-8739b09f56ff',
    amount: '484',
    status: 'pending',
    email: 'Bernita_Pacocha-Rogahn@gmail.com',
    paymentMethod: 'debit_card',
    transactionDate: '2025-08-25',
    paymentReference: 'PAY-9CLJ44',
  },
  {
    id: 'b41fc404-aa8b-4cdd-a0c6-db1d282179ab',
    amount: '577',
    status: 'failed',
    email: 'Abbigail17@gmail.com',
    paymentMethod: 'debit_card',
    transactionDate: '2025-09-15',
    paymentReference: 'PAY-48BLLR',
  },
  {
    id: '02f99389-7c1d-49c9-b600-8f9c9a449253',
    amount: '281',
    status: 'pending',
    email: 'Ivah_Nader28@hotmail.com',
    paymentMethod: 'bank_transfer',
    transactionDate: '2025-09-05',
    paymentReference: 'PAY-FEANLY',
  },
  {
    id: '0e5d07f6-881b-45b2-b98d-838ceed62ddf',
    amount: '905',
    status: 'success',
    email: 'Kadin.Witting44@gmail.com',
    paymentMethod: 'credit_card',
    transactionDate: '2025-08-24',
    paymentReference: 'PAY-OQE1T6',
  },
]
