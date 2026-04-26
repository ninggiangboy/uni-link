export type AriaBinds = {
  id: string;
  'aria-describedby'?: string;
  'aria-invalid'?: boolean;
};

export type FormFieldVmBinds = AriaBinds & {
  name: string;
  value?: any;
  modelValue?: any;
  'onUpdate:modelValue'?: (value: any) => void;
  onInput?: (e: Event | unknown) => void;
  onChange?: (e: Event | unknown) => void;
  onBlur?: (e: Event | unknown) => void;
};

export type FormFieldSlotProps = {
  componentField: {
    modelValue?: any;
    'onUpdate:modelValue'?: (value: any) => void;
    onBlur?: (e: Event) => void;
    onInput?: (e: Event | unknown) => void;
    onChange?: (e: Event | unknown) => void;
  };
  ariaBinds: AriaBinds;
  vmBinds: FormFieldVmBinds;
  formDescriptionId: string;
  formMessageId: string;
  value: unknown;
  errorMessage: string | undefined;
  errors: string[];
  meta: {
    touched: boolean;
    dirty: boolean;
    valid: boolean;
    validated: boolean;
    pending: boolean;
    initialValue?: unknown;
  };
  field: {
    value: unknown;
    onBlur: (e: Event | unknown) => void;
    onInput: (e: Event | unknown) => void;
    onChange: (e: Event | unknown) => void;
  };
  handleChange: (evt: Event | unknown, shouldValidate?: boolean) => void;
  handleBlur: (e: Event | unknown) => void;
  resetField: (state?: { value?: unknown }) => void;
  handleReset: () => void;
  validate: () => Promise<{ errors: string[] }>;
  setTouched: (isTouched: boolean) => void;
};
