"use client";

import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
} from "@mui/material";
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import type { InstallmentView } from "@/lib/types";

const STATUS_COLOR: Record<string, "default" | "success" | "warning" | "error"> = {
  SCHEDULED: "default",
  PAID: "success",
  MISSED: "warning",
  WRITTEN_OFF: "error",
};

export default function AmortizationPanel({ installments }: { installments: InstallmentView[] }) {
  const chartData = installments.map((i) => ({
    period: i.periodNumber,
    balance: i.remainingBalanceAfter,
  }));

  return (
    <Box>
      <Typography variant="subtitle1" fontWeight={700} sx={{ mb: 1 }}>
        Balance over the life of the loan
      </Typography>
      <Box sx={{ height: 220, mb: 3 }}>
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={chartData} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
            <defs>
              <linearGradient id="balanceFill" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#6C8EFF" stopOpacity={0.5} />
                <stop offset="100%" stopColor="#6C8EFF" stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
            <XAxis dataKey="period" stroke="rgba(255,255,255,0.4)" fontSize={12} />
            <YAxis stroke="rgba(255,255,255,0.4)" fontSize={12} width={70} tickFormatter={(v) => `$${v}`} />
            <Tooltip
              contentStyle={{ background: "#121826", border: "1px solid rgba(255,255,255,0.1)", borderRadius: 8 }}
              formatter={(value) => [`$${Number(value).toFixed(2)}`, "Balance"]}
              labelFormatter={(label) => `Period ${label}`}
            />
            <Area type="monotone" dataKey="balance" stroke="#6C8EFF" fill="url(#balanceFill)" strokeWidth={2} />
          </AreaChart>
        </ResponsiveContainer>
      </Box>

      <TableContainer sx={{ maxHeight: 320 }}>
        <Table size="small" stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>Period</TableCell>
              <TableCell align="right">Principal</TableCell>
              <TableCell align="right">Interest</TableCell>
              <TableCell align="right">Balance after</TableCell>
              <TableCell align="right">Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {installments.map((installment) => (
              <TableRow key={installment.periodNumber}>
                <TableCell>{installment.periodNumber}</TableCell>
                <TableCell align="right">${installment.principalDue.toFixed(2)}</TableCell>
                <TableCell align="right">${installment.interestDue.toFixed(2)}</TableCell>
                <TableCell align="right">${installment.remainingBalanceAfter.toFixed(2)}</TableCell>
                <TableCell align="right">
                  <Chip size="small" label={installment.status} color={STATUS_COLOR[installment.status]} variant="outlined" />
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
