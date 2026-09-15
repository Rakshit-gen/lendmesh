"use client";

import { Box, Typography, Stack, LinearProgress, Tooltip } from "@mui/material";
import type { ScorecardResult } from "@/lib/types";

export default function ScorecardBreakdown({ scorecard }: { scorecard: ScorecardResult }) {
  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="baseline" sx={{ mb: 2 }}>
        <Typography variant="subtitle1" fontWeight={700}>
          How this grade was calculated
        </Typography>
        <Typography variant="h5" fontWeight={800} color="primary.light">
          {scorecard.total}/100
        </Typography>
      </Stack>

      <Stack gap={2}>
        {scorecard.factors.map((factor) => (
          <Box key={factor.label}>
            <Stack direction="row" justifyContent="space-between" sx={{ mb: 0.5 }}>
              <Typography variant="body2">{factor.label}</Typography>
              <Typography variant="body2" color="text.secondary">
                {factor.points}/{factor.maxPoints}
              </Typography>
            </Stack>
            <Tooltip title={factor.detail} placement="top" arrow>
              <LinearProgress
                variant="determinate"
                value={(factor.points / factor.maxPoints) * 100}
                sx={{ cursor: "help" }}
              />
            </Tooltip>
          </Box>
        ))}
      </Stack>
    </Box>
  );
}
