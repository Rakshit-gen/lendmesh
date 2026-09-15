"use client";

import { useQuery } from "@tanstack/react-query";
import {
  Typography,
  Paper,
  Box,
  Skeleton,
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  Chip,
  Stack,
} from "@mui/material";
import AppShell from "@/components/AppShell";
import RequireAuth from "@/components/RequireAuth";
import { api } from "@/lib/api";
import type { LedgerEntry } from "@/lib/types";
import { useAuth } from "@/lib/auth";

const TYPE_COLOR: Record<string, "default" | "success" | "warning" | "error" | "info"> = {
  DEPOSIT: "info",
  LOAN_FUNDING: "warning",
  LOAN_DISBURSEMENT: "success",
  REPAYMENT_RECEIVED: "success",
  REPAYMENT_PRINCIPAL: "success",
  CHARGE_OFF: "error",
};

function WalletContent() {
  const { me } = useAuth();
  const { data, isLoading } = useQuery({
    queryKey: ["wallet", "history"],
    queryFn: async () => (await api.get<LedgerEntry[]>("/api/wallet/history")).data,
  });

  return (
    <AppShell>
      <Stack direction="row" justifyContent="space-between" alignItems="baseline" sx={{ mb: 3 }}>
        <Typography variant="h4" fontWeight={700}>
          Wallet
        </Typography>
        <Typography variant="h5" fontWeight={700} color="secondary.main">
          ${me?.walletBalance.toLocaleString(undefined, { minimumFractionDigits: 2 })}
        </Typography>
      </Stack>

      <Paper sx={{ p: 3 }}>
        <Typography variant="subtitle1" fontWeight={700} sx={{ mb: 2 }}>
          Transaction history
        </Typography>

        {isLoading && <Skeleton variant="rounded" height={200} />}

        {data && data.length === 0 && (
          <Typography variant="body2" color="text.secondary">
            Nothing here yet. Fund a loan or wait for a repayment to see it show up.
          </Typography>
        )}

        {data && data.length > 0 && (
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>When</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Memo</TableCell>
                <TableCell align="right">Amount</TableCell>
                <TableCell align="right">Balance after</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {data.map((entry) => (
                <TableRow key={entry.id}>
                  <TableCell>{new Date(entry.occurredAt).toLocaleString()}</TableCell>
                  <TableCell>
                    <Chip size="small" label={entry.type.replace(/_/g, " ")} color={TYPE_COLOR[entry.type] ?? "default"} variant="outlined" />
                  </TableCell>
                  <TableCell>
                    <Box sx={{ maxWidth: 340, whiteSpace: "normal" }}>{entry.memo}</Box>
                  </TableCell>
                  <TableCell align="right" sx={{ color: entry.amount < 0 ? "error.main" : "success.main" }}>
                    {entry.amount < 0 ? "-" : "+"}${Math.abs(entry.amount).toLocaleString()}
                  </TableCell>
                  <TableCell align="right">${entry.balanceAfter.toLocaleString()}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </Paper>
    </AppShell>
  );
}

export default function WalletPage() {
  return (
    <RequireAuth>
      <WalletContent />
    </RequireAuth>
  );
}
