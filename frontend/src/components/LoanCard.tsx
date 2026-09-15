"use client";

import { useState } from "react";
import Link from "next/link";
import { Card, CardActionArea, Box, Typography, Stack, LinearProgress, Chip } from "@mui/material";
import { motion } from "framer-motion";
import RiskGradeBadge from "./RiskGradeBadge";
import { useFundingSocket } from "@/lib/useFundingSocket";
import type { ListingSummary } from "@/lib/types";

export default function LoanCard({ listing }: { listing: ListingSummary }) {
  const [fundedAmount, setFundedAmount] = useState(listing.fundedAmount);

  useFundingSocket(listing.id, (update) => setFundedAmount(update.fundedAmount));

  const percentFunded = Math.min(100, (fundedAmount / listing.requestedAmount) * 100);

  return (
    <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} whileHover={{ y: -4 }} transition={{ duration: 0.25 }}>
      <Card>
        <CardActionArea component={Link} href={`/loans/${listing.id}`} sx={{ p: 2.5 }}>
          <Stack direction="row" gap={2} alignItems="flex-start">
            <RiskGradeBadge grade={listing.riskGrade} />
            <Box sx={{ flexGrow: 1, minWidth: 0 }}>
              <Typography variant="subtitle1" fontWeight={700} noWrap>
                {listing.purpose}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                ${listing.requestedAmount.toLocaleString()} · {listing.termMonths} mo ·{" "}
                {(listing.interestRate * 100).toFixed(2)}% APR
              </Typography>
            </Box>
            <Chip size="small" label={listing.status.replace(/_/g, " ")} variant="outlined" />
          </Stack>

          <Box sx={{ mt: 2.5 }}>
            <Stack direction="row" justifyContent="space-between" sx={{ mb: 0.5 }}>
              <Typography variant="caption" color="text.secondary">
                ${fundedAmount.toLocaleString()} funded
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {percentFunded.toFixed(0)}%
              </Typography>
            </Stack>
            <LinearProgress variant="determinate" value={percentFunded} />
          </Box>
        </CardActionArea>
      </Card>
    </motion.div>
  );
}
