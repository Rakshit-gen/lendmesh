"use client";

import { Box, Tooltip } from "@mui/material";
import { riskGradeColor } from "@/theme/theme";

export default function RiskGradeBadge({ grade, size = 36 }: { grade: string; size?: number }) {
  const color = riskGradeColor(grade);
  return (
    <Tooltip title={`Risk grade ${grade}`}>
      <Box
        sx={{
          width: size,
          height: size,
          borderRadius: "50%",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          fontWeight: 800,
          fontSize: size * 0.42,
          color,
          bgcolor: `${color}22`,
          border: `1.5px solid ${color}66`,
          flexShrink: 0,
        }}
      >
        {grade}
      </Box>
    </Tooltip>
  );
}
