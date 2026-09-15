import { createTheme } from "@mui/material/styles";

// LendMesh runs on a deep indigo/teal palette, money at night, not a spreadsheet at noon.
// Risk grades get their own fixed colors (see riskGradeColor) so a lender can
// recognize a grade by color alone once they've used the marketplace a few times.
export const theme = createTheme({
  palette: {
    mode: "dark",
    primary: {
      main: "#6C8EFF",
      light: "#9CB3FF",
      dark: "#4A63C4",
    },
    secondary: {
      main: "#37E6C4",
    },
    background: {
      default: "#0B0F1A",
      paper: "#121826",
    },
    success: {
      main: "#3DDC97",
    },
    warning: {
      main: "#F5B942",
    },
    error: {
      main: "#FF6B6B",
    },
    divider: "rgba(255,255,255,0.08)",
  },
  shape: {
    borderRadius: 14,
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica Neue", Arial, sans-serif',
    h1: { fontWeight: 700 },
    h2: { fontWeight: 700 },
    h3: { fontWeight: 700 },
    h4: { fontWeight: 600 },
    h5: { fontWeight: 600 },
    h6: { fontWeight: 600 },
    button: { fontWeight: 600, textTransform: "none" },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          backdropFilter: "blur(20px)",
          backgroundColor: "rgba(18, 24, 38, 0.72)",
          border: "1px solid rgba(255,255,255,0.06)",
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 10,
        },
        contained: {
          boxShadow: "none",
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: "none",
          backdropFilter: "blur(20px)",
          backgroundColor: "rgba(18, 24, 38, 0.72)",
          border: "1px solid rgba(255,255,255,0.06)",
        },
      },
    },
    MuiLinearProgress: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          height: 8,
        },
      },
    },
  },
});

const RISK_GRADE_COLORS: Record<string, string> = {
  A: "#3DDC97",
  B: "#7FD858",
  C: "#C7DC3F",
  D: "#F5B942",
  E: "#F5892E",
  F: "#F1653D",
  G: "#FF6B6B",
};

export function riskGradeColor(grade: string): string {
  return RISK_GRADE_COLORS[grade] ?? "#6C8EFF";
}
