"use client";

import { Box, Typography, Stack } from "@mui/material";
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from "recharts";
import { riskGradeColor } from "@/theme/theme";

export default function GradeDonut({ loansByGrade }: { loansByGrade: Record<string, number> }) {
  const data = Object.entries(loansByGrade).map(([grade, count]) => ({ grade, count }));
  const total = data.reduce((sum, d) => sum + d.count, 0);

  if (total === 0) {
    return (
      <Typography variant="body2" color="text.secondary">
        Fund a loan to see your grade mix here.
      </Typography>
    );
  }

  return (
    <Stack direction="row" alignItems="center" gap={2}>
      <Box sx={{ width: 140, height: 140, flexShrink: 0 }}>
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie data={data} dataKey="count" nameKey="grade" innerRadius={40} outerRadius={65} paddingAngle={3}>
              {data.map((entry) => (
                <Cell key={entry.grade} fill={riskGradeColor(entry.grade)} />
              ))}
            </Pie>
            <Tooltip
              contentStyle={{ background: "#121826", border: "1px solid rgba(255,255,255,0.1)", borderRadius: 8 }}
              formatter={(value, _name, item) => [`${value} loan${value === 1 ? "" : "s"}`, `Grade ${item.payload.grade}`]}
            />
          </PieChart>
        </ResponsiveContainer>
      </Box>
      <Stack gap={0.75}>
        {data.map((entry) => (
          <Stack direction="row" alignItems="center" gap={1} key={entry.grade}>
            <Box sx={{ width: 10, height: 10, borderRadius: "50%", bgcolor: riskGradeColor(entry.grade) }} />
            <Typography variant="body2">
              Grade {entry.grade} · {entry.count}
            </Typography>
          </Stack>
        ))}
      </Stack>
    </Stack>
  );
}
