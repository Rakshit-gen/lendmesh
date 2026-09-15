"use client";

import { useQuery } from "@tanstack/react-query";
import {
  Typography,
  Grid,
  Paper,
  Box,
  Skeleton,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Chip,
} from "@mui/material";
import AppShell from "@/components/AppShell";
import RequireAuth from "@/components/RequireAuth";
import RiskGradeBadge from "@/components/RiskGradeBadge";
import GradeDonut from "@/components/GradeDonut";
import { api } from "@/lib/api";
import type { PortfolioSummary } from "@/lib/types";

function StatCard({ label, value, color }: { label: string; value: string; color?: string }) {
  return (
    <Paper sx={{ p: 2.5 }}>
      <Typography variant="caption" color="text.secondary">
        {label}
      </Typography>
      <Typography variant="h5" fontWeight={700} sx={{ mt: 0.5, color }}>
        {value}
      </Typography>
    </Paper>
  );
}

function PortfolioContent() {
  const { data, isLoading } = useQuery({
    queryKey: ["portfolio"],
    queryFn: async () => (await api.get<PortfolioSummary>("/api/portfolio")).data,
  });

  if (isLoading || !data) {
    return (
      <AppShell>
        <Skeleton variant="rounded" height={300} />
      </AppShell>
    );
  }

  return (
    <AppShell>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 3 }}>
        My portfolio
      </Typography>

      <Grid container spacing={2.5} sx={{ mb: 3 }}>
        <Grid item xs={6} md={3}>
          <StatCard label="Principal committed" value={`$${data.totalPrincipalCommitted.toLocaleString()}`} />
        </Grid>
        <Grid item xs={6} md={3}>
          <StatCard label="Principal repaid" value={`$${data.totalPrincipalRepaid.toLocaleString()}`} color="success.main" />
        </Grid>
        <Grid item xs={6} md={3}>
          <StatCard label="Interest earned" value={`$${data.totalInterestEarned.toLocaleString()}`} color="secondary.main" />
        </Grid>
        <Grid item xs={6} md={3}>
          <StatCard label="Outstanding principal" value={`$${data.outstandingPrincipal.toLocaleString()}`} />
        </Grid>
      </Grid>

      <Grid container spacing={2.5}>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="subtitle1" fontWeight={700} sx={{ mb: 2 }}>
              Grade mix
            </Typography>
            <GradeDonut loansByGrade={data.loansByGrade} />
          </Paper>
        </Grid>

        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="subtitle1" fontWeight={700} sx={{ mb: 2 }}>
              Holdings
            </Typography>
            {data.holdings.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                You haven&apos;t funded any loans yet. Head to the marketplace to get your first stake.
              </Typography>
            ) : (
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Grade</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell align="right">Committed</TableCell>
                    <TableCell align="right">Repaid</TableCell>
                    <TableCell align="right">Interest</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {data.holdings.map((holding) => (
                    <TableRow key={holding.loanListingId}>
                      <TableCell>
                        <Box sx={{ display: "flex" }}>
                          <RiskGradeBadge grade={holding.riskGrade} size={28} />
                        </Box>
                      </TableCell>
                      <TableCell>
                        <Chip size="small" variant="outlined" label={holding.status.replace(/_/g, " ")} />
                      </TableCell>
                      <TableCell align="right">${holding.principalCommitted.toLocaleString()}</TableCell>
                      <TableCell align="right">${holding.principalRepaid.toLocaleString()}</TableCell>
                      <TableCell align="right">${holding.interestRepaid.toLocaleString()}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </Paper>
        </Grid>
      </Grid>
    </AppShell>
  );
}

export default function PortfolioPage() {
  return (
    <RequireAuth>
      <PortfolioContent />
    </RequireAuth>
  );
}
