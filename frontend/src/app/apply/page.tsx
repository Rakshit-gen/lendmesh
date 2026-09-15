"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import {
  Stepper,
  Step,
  StepLabel,
  Paper,
  Typography,
  TextField,
  Button,
  Stack,
  Box,
  Slider,
  Alert,
  Grid,
} from "@mui/material";
import { motion, AnimatePresence } from "framer-motion";
import { useMutation } from "@tanstack/react-query";
import AppShell from "@/components/AppShell";
import RequireAuth from "@/components/RequireAuth";
import ScorecardBreakdown from "@/components/ScorecardBreakdown";
import RiskGradeBadge from "@/components/RiskGradeBadge";
import { api, extractErrorMessage } from "@/lib/api";
import type { ListingDetail } from "@/lib/types";

const STEPS = ["The loan", "Your finances", "Review"];

interface FormState {
  purpose: string;
  requestedAmount: string;
  termMonths: number;
  annualIncome: string;
  existingMonthlyDebt: string;
  onTimePaymentRate: number;
  creditUtilization: number;
  monthsOfCreditHistory: number;
  openDelinquencies: number;
}

const INITIAL: FormState = {
  purpose: "",
  requestedAmount: "",
  termMonths: 24,
  annualIncome: "",
  existingMonthlyDebt: "",
  onTimePaymentRate: 90,
  creditUtilization: 30,
  monthsOfCreditHistory: 36,
  openDelinquencies: 0,
};

function ApplyContent() {
  const router = useRouter();
  const [step, setStep] = useState(0);
  const [form, setForm] = useState<FormState>(INITIAL);

  const mutation = useMutation({
    mutationFn: async () =>
      (
        await api.post<ListingDetail>("/api/loans/apply", {
          purpose: form.purpose,
          requestedAmount: Number(form.requestedAmount),
          termMonths: form.termMonths,
          annualIncome: Number(form.annualIncome),
          existingMonthlyDebt: Number(form.existingMonthlyDebt),
          onTimePaymentRate: form.onTimePaymentRate,
          creditUtilization: form.creditUtilization,
          monthsOfCreditHistory: form.monthsOfCreditHistory,
          openDelinquencies: form.openDelinquencies,
        })
      ).data,
  });

  function update<K extends keyof FormState>(key: K, value: FormState[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  const stepOneValid = form.purpose.trim().length > 0 && Number(form.requestedAmount) >= 100;
  const stepTwoValid = Number(form.annualIncome) > 0;

  return (
    <AppShell>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 3 }}>
        Apply for a simulated loan
      </Typography>

      <Stepper activeStep={step} sx={{ mb: 4 }}>
        {STEPS.map((label) => (
          <Step key={label}>
            <StepLabel>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>

      <Paper sx={{ p: 4, minHeight: 380 }}>
        <AnimatePresence mode="wait">
          {mutation.data ? (
            <motion.div key="result" initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}>
              <Stack direction="row" gap={2} alignItems="center" sx={{ mb: 3 }}>
                <RiskGradeBadge grade={mutation.data.riskGrade} size={56} />
                <Box>
                  <Typography variant="h6" fontWeight={700}>
                    Your listing is live
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Graded {mutation.data.riskGrade}, priced at {(mutation.data.interestRate * 100).toFixed(2)}% APR
                  </Typography>
                </Box>
              </Stack>
              <ScorecardBreakdown scorecard={mutation.data.scorecard} />
              <Stack direction="row" gap={2} sx={{ mt: 4 }}>
                <Button variant="contained" onClick={() => router.push(`/loans/${mutation.data!.id}`)}>
                  View listing
                </Button>
                <Button variant="text" onClick={() => router.push("/marketplace")}>
                  Back to marketplace
                </Button>
              </Stack>
            </motion.div>
          ) : (
            <motion.div key={step} initial={{ opacity: 0, x: 16 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: -16 }} transition={{ duration: 0.2 }}>
              {mutation.isError && (
                <Alert severity="error" sx={{ mb: 3 }}>
                  {extractErrorMessage(mutation.error)}
                </Alert>
              )}

              {step === 0 && (
                <Stack gap={3}>
                  <TextField
                    label="What's this loan for?"
                    placeholder="Consolidating credit card debt, a car repair, a small business restock..."
                    value={form.purpose}
                    onChange={(e) => update("purpose", e.target.value)}
                    fullWidth
                  />
                  <TextField
                    label="Amount requested"
                    type="number"
                    value={form.requestedAmount}
                    onChange={(e) => update("requestedAmount", e.target.value)}
                    inputProps={{ min: 100, max: 100000 }}
                    helperText="Between $100 and $100,000"
                    fullWidth
                  />
                  <Box>
                    <Typography gutterBottom>Term: {form.termMonths} months</Typography>
                    <Slider
                      value={form.termMonths}
                      onChange={(_, value) => update("termMonths", value as number)}
                      min={3}
                      max={60}
                      step={3}
                      marks={[{ value: 3, label: "3mo" }, { value: 60, label: "60mo" }]}
                    />
                  </Box>
                </Stack>
              )}

              {step === 1 && (
                <Stack gap={3}>
                  <Typography variant="body2" color="text.secondary">
                    This is simulated financial history, it&apos;s what the scorecard reads, the same way a real
                    credit decision would.
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                      <TextField
                        label="Annual income"
                        type="number"
                        value={form.annualIncome}
                        onChange={(e) => update("annualIncome", e.target.value)}
                        fullWidth
                      />
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <TextField
                        label="Existing monthly debt payments"
                        type="number"
                        value={form.existingMonthlyDebt}
                        onChange={(e) => update("existingMonthlyDebt", e.target.value)}
                        fullWidth
                      />
                    </Grid>
                  </Grid>

                  <Box>
                    <Typography gutterBottom>On-time payment rate: {form.onTimePaymentRate}%</Typography>
                    <Slider value={form.onTimePaymentRate} onChange={(_, v) => update("onTimePaymentRate", v as number)} min={0} max={100} />
                  </Box>
                  <Box>
                    <Typography gutterBottom>Credit utilization: {form.creditUtilization}%</Typography>
                    <Slider value={form.creditUtilization} onChange={(_, v) => update("creditUtilization", v as number)} min={0} max={100} />
                  </Box>
                  <Box>
                    <Typography gutterBottom>Months of credit history: {form.monthsOfCreditHistory}</Typography>
                    <Slider value={form.monthsOfCreditHistory} onChange={(_, v) => update("monthsOfCreditHistory", v as number)} min={0} max={240} />
                  </Box>
                  <Box>
                    <Typography gutterBottom>Open delinquencies: {form.openDelinquencies}</Typography>
                    <Slider value={form.openDelinquencies} onChange={(_, v) => update("openDelinquencies", v as number)} min={0} max={10} />
                  </Box>
                </Stack>
              )}

              {step === 2 && (
                <Stack gap={2}>
                  <Typography variant="subtitle1" fontWeight={700}>
                    Ready to list?
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    Requesting <strong>${Number(form.requestedAmount).toLocaleString()}</strong> over{" "}
                    <strong>{form.termMonths} months</strong> for &ldquo;{form.purpose}&rdquo;. Your grade and rate
                    are calculated the moment you submit.
                  </Typography>
                </Stack>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </Paper>

      {!mutation.data && (
        <Stack direction="row" justifyContent="space-between" sx={{ mt: 3 }}>
          <Button disabled={step === 0} onClick={() => setStep((s) => s - 1)}>
            Back
          </Button>
          {step < STEPS.length - 1 ? (
            <Button
              variant="contained"
              onClick={() => setStep((s) => s + 1)}
              disabled={(step === 0 && !stepOneValid) || (step === 1 && !stepTwoValid)}
            >
              Continue
            </Button>
          ) : (
            <Button variant="contained" onClick={() => mutation.mutate()} disabled={mutation.isPending}>
              {mutation.isPending ? "Submitting..." : "Submit application"}
            </Button>
          )}
        </Stack>
      )}
    </AppShell>
  );
}

export default function ApplyPage() {
  return (
    <RequireAuth>
      <ApplyContent />
    </RequireAuth>
  );
}
