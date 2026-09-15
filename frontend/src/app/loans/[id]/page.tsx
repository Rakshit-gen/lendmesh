"use client";

import { use, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Typography, Grid, Paper, Stack, Box, LinearProgress, Skeleton } from "@mui/material";
import AppShell from "@/components/AppShell";
import RequireAuth from "@/components/RequireAuth";
import RiskGradeBadge from "@/components/RiskGradeBadge";
import ScorecardBreakdown from "@/components/ScorecardBreakdown";
import AmortizationPanel from "@/components/AmortizationPanel";
import FundForm from "@/components/FundForm";
import { api } from "@/lib/api";
import { useFundingSocket } from "@/lib/useFundingSocket";
import type { ListingDetail, InstallmentView } from "@/lib/types";

function LoanDetailContent({ id }: { id: string }) {
  const { data: listing, isLoading } = useQuery({
    queryKey: ["loan", id],
    queryFn: async () => (await api.get<ListingDetail>(`/api/loans/${id}`)).data,
  });

  const { data: installments } = useQuery({
    queryKey: ["loan", id, "installments"],
    queryFn: async () => (await api.get<InstallmentView[]>(`/api/loans/${id}/installments`)).data,
    enabled: !!listing && listing.status !== "OPEN_FOR_FUNDING" && listing.status !== "PENDING_REVIEW",
  });

  const [liveFunded, setLiveFunded] = useState<number | null>(null);
  useFundingSocket(id, (update) => setLiveFunded(update.fundedAmount));

  if (isLoading || !listing) {
    return (
      <AppShell>
        <Skeleton variant="rounded" height={300} />
      </AppShell>
    );
  }

  const fundedAmount = liveFunded ?? listing.fundedAmount;
  const percentFunded = Math.min(100, (fundedAmount / listing.requestedAmount) * 100);

  return (
    <AppShell>
      <Grid container spacing={3}>
        <Grid item xs={12} md={7}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Stack direction="row" gap={2} alignItems="center" sx={{ mb: 2 }}>
              <RiskGradeBadge grade={listing.riskGrade} size={48} />
              <Box>
                <Typography variant="h5" fontWeight={700}>
                  {listing.purpose}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  ${listing.requestedAmount.toLocaleString()} over {listing.termMonths} months at{" "}
                  {(listing.interestRate * 100).toFixed(2)}% APR
                </Typography>
              </Box>
            </Stack>

            <Box sx={{ mb: 1 }}>
              <Stack direction="row" justifyContent="space-between" sx={{ mb: 0.5 }}>
                <Typography variant="body2">${fundedAmount.toLocaleString()} funded</Typography>
                <Typography variant="body2">{percentFunded.toFixed(0)}%</Typography>
              </Stack>
              <LinearProgress variant="determinate" value={percentFunded} />
            </Box>
          </Paper>

          <Paper sx={{ p: 3 }}>
            <ScorecardBreakdown scorecard={listing.scorecard} />
          </Paper>
        </Grid>

        <Grid item xs={12} md={5}>
          <Paper sx={{ p: 3, mb: 3 }}>
            <FundForm listing={{ ...listing, fundedAmount }} />
          </Paper>

          {installments && installments.length > 0 && (
            <Paper sx={{ p: 3 }}>
              <AmortizationPanel installments={installments} />
            </Paper>
          )}
        </Grid>
      </Grid>
    </AppShell>
  );
}

export default function LoanDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  return (
    <RequireAuth>
      <LoanDetailContent id={id} />
    </RequireAuth>
  );
}
