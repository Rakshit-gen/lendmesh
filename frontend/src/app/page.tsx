"use client";

import Link from "next/link";
import { Box, Typography, Button, Stack, Grid, Paper } from "@mui/material";
import { motion } from "framer-motion";
import HubIcon from "@mui/icons-material/Hub";
import HandshakeIcon from "@mui/icons-material/Handshake";
import QueryStatsIcon from "@mui/icons-material/QueryStats";
import BoltIcon from "@mui/icons-material/Bolt";
import HeroBackground from "@/components/HeroBackground";
import QuickStartCard from "@/components/QuickStartCard";
import { riskGradeColor } from "@/theme/theme";

const NAV_LINKS = [
  { label: "Marketplace", href: "/marketplace" },
  { label: "How scoring works", href: "/#scoring" },
  { label: "The simulation clock", href: "/#clock" },
];

const SCORE_FACTORS = [
  { label: "Debt-to-income", points: 30, detail: "How much of a borrower's income is already spoken for" },
  { label: "On-time payment history", points: 25, detail: "A track record, not a promise" },
  { label: "Credit utilization", points: 20, detail: "How close to the limit their existing credit runs" },
  { label: "Credit history length", points: 15, detail: "Longer history, fewer surprises" },
  { label: "Open delinquencies", points: 10, detail: "Anything currently past due" },
];

const GRADES = ["A", "B", "C", "D", "E", "F", "G"];

const PILLARS = [
  {
    icon: <QueryStatsIcon color="primary" fontSize="large" />,
    title: "A scorecard you can actually read",
    body: "Every grade traces back to five weighted factors, debt-to-income, payment history, utilization, credit age, delinquencies. No black box, no mystery number.",
  },
  {
    icon: <HandshakeIcon color="secondary" fontSize="large" />,
    title: "Fund in fractions, not whole loans",
    body: "Spread a stake across dozens of listings the way real P2P lending platforms work, and watch each one fill up live as other lenders join in.",
  },
  {
    icon: <BoltIcon color="warning" fontSize="large" />,
    title: "Years of repayment, minutes to watch",
    body: "A simulation clock runs the amortization schedule forward, period by period, so you see how a loan book behaves, good grades and bad, without waiting for real time to pass.",
  },
];

export default function HomePage() {
  return (
    <Box sx={{ p: { xs: 1.5, sm: 2, md: 3 } }}>
      <Box
        sx={{
          position: "relative",
          borderRadius: { xs: 4, sm: 6 },
          overflow: "hidden",
          minHeight: {
            xs: "calc(100vh - 24px)",
            sm: "calc(100vh - 32px)",
            md: "calc(100vh - 48px)",
          },
          bgcolor: "#070a12",
        }}
      >
        <HeroBackground />
        <Box
          sx={{
            position: "absolute",
            inset: 0,
            background: "linear-gradient(180deg, rgba(7,10,18,0.15) 0%, rgba(7,10,18,0.55) 70%, rgba(7,10,18,0.92) 100%)",
          }}
        />

        <Box
          sx={{
            position: "relative",
            zIndex: 1,
            display: "flex",
            flexDirection: "column",
            minHeight: {
              xs: "calc(100vh - 24px)",
              sm: "calc(100vh - 32px)",
              md: "calc(100vh - 48px)",
            },
            p: { xs: 2.5, sm: 3.5, md: 5 },
            gap: 3,
          }}
        >
          {/* Glass pill navbar */}
          <Stack
            direction="row"
            alignItems="center"
            gap={{ xs: 1.5, sm: 3 }}
            sx={{
              bgcolor: "rgba(255,255,255,0.06)",
              backdropFilter: "blur(20px)",
              border: "1px solid rgba(255,255,255,0.08)",
              borderRadius: 6,
              pl: 2,
              pr: 1,
              py: 1,
              width: { xs: "100%", sm: "fit-content" },
            }}
          >
            <Stack direction="row" alignItems="center" gap={1} component={Link} href="/" sx={{ color: "inherit" }}>
              <HubIcon color="secondary" />
              <Typography variant="subtitle1" fontWeight={800}>
                LendMesh
              </Typography>
            </Stack>

            <Stack direction="row" gap={3} sx={{ display: { xs: "none", sm: "flex" } }}>
              {NAV_LINKS.map((link) => (
                <Typography
                  key={link.href}
                  component={Link}
                  href={link.href}
                  variant="body2"
                  sx={{ color: "rgba(255,255,255,0.75)", "&:hover": { color: "white" } }}
                >
                  {link.label}
                </Typography>
              ))}
            </Stack>

            <Button component={Link} href="/register" variant="contained" size="small" sx={{ ml: "auto", borderRadius: 4 }}>
              Start simulating
            </Button>
          </Stack>

          {/* Headline + quick-start card. The headline is a flex child that grows
              to fill whatever the card doesn't use, so the card always sits flush
              against the hero's right edge with no leftover-space math needed. */}
          <Stack
            direction={{ xs: "column", lg: "row" }}
            alignItems={{ lg: "center" }}
            gap={4}
            sx={{ width: "100%", my: "auto" }}
          >
            <Box
              component={motion.div}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              sx={{ flex: { lg: "1 1 auto" }, minWidth: 0 }}
            >
              <Typography
                sx={{
                  color: "white",
                  fontSize: { xs: "2.1rem", sm: "2.6rem", md: "3.2rem" },
                  fontWeight: 600,
                  lineHeight: 1.15,
                  maxWidth: 680,
                  textShadow: "0 8px 30px rgba(0,0,0,0.4)",
                }}
              >
                Practice lending like it&apos;s real,
                <br />
                because every dollar is{" "}
                <Box component="span" sx={{ fontFamily: '"Georgia", serif', fontStyle: "italic", fontWeight: 400 }}>
                  simulated
                </Box>
                .
              </Typography>
              <Typography sx={{ color: "rgba(255,255,255,0.7)", mt: 2, maxWidth: 520 }}>
                List a loan, get graded by a scorecard you can inspect line by line, fund a
                stranger&apos;s listing in fractions, and watch a repayment clock play the whole
                thing out, defaults included.
              </Typography>
            </Box>

            <Box
              component={motion.div}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.15 }}
              sx={{ flexShrink: { lg: 0 }, width: { xs: "100%", lg: "auto" } }}
            >
              <QuickStartCard />
            </Box>
          </Stack>
        </Box>
      </Box>

      <Grid container spacing={3} sx={{ py: 8, maxWidth: 1100, mx: "auto" }}>
        {PILLARS.map((pillar, index) => (
          <Grid item xs={12} md={4} key={pillar.title}>
            <motion.div
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: index * 0.1 }}
              style={{ height: "100%" }}
            >
              <Paper sx={{ p: 3, height: "100%" }}>
                <Box sx={{ mb: 2 }}>{pillar.icon}</Box>
                <Typography variant="h6" sx={{ mb: 1 }}>
                  {pillar.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {pillar.body}
                </Typography>
              </Paper>
            </motion.div>
          </Grid>
        ))}
      </Grid>

      <Box
        id="scoring"
        sx={{ scrollMarginTop: 24, maxWidth: 1100, mx: "auto", py: 8, px: { xs: 1, sm: 0 } }}
      >
        <Typography variant="h4" fontWeight={700} sx={{ mb: 1 }}>
          How scoring works
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 640 }}>
          Every listing gets graded by the same five-factor scorecard, and the math behind it
          is never hidden from you. A grade of A through G falls out of a score from 0 to 100,
          nothing more mysterious than that.
        </Typography>

        <Grid container spacing={2}>
          <Grid item xs={12} md={7}>
            <Paper sx={{ p: 3 }}>
              <Stack divider={<Box sx={{ borderBottom: "1px solid rgba(255,255,255,0.08)" }} />} gap={2}>
                {SCORE_FACTORS.map((factor) => (
                  <Stack key={factor.label} direction="row" alignItems="center" gap={2}>
                    <Box sx={{ flex: 1 }}>
                      <Typography variant="subtitle2" fontWeight={600}>
                        {factor.label}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {factor.detail}
                      </Typography>
                    </Box>
                    <Box
                      sx={{
                        width: 56,
                        height: 56,
                        borderRadius: "50%",
                        border: "2px solid rgba(108,142,255,0.4)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        flexShrink: 0,
                      }}
                    >
                      <Typography variant="subtitle1" fontWeight={700} color="primary.light">
                        {factor.points}
                      </Typography>
                    </Box>
                  </Stack>
                ))}
              </Stack>
            </Paper>
          </Grid>

          <Grid item xs={12} md={5}>
            <Paper sx={{ p: 3, height: "100%" }}>
              <Typography variant="subtitle1" fontWeight={700} sx={{ mb: 2 }}>
                Score to grade
              </Typography>
              <Stack direction="row" gap={1} flexWrap="wrap">
                {GRADES.map((grade) => (
                  <Box
                    key={grade}
                    sx={{
                      width: 40,
                      height: 40,
                      borderRadius: 2,
                      bgcolor: riskGradeColor(grade),
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#0B0F1A",
                      fontWeight: 700,
                    }}
                  >
                    {grade}
                  </Box>
                ))}
              </Stack>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
                The grade is locked the moment a listing goes live, using whatever the borrower’s
                numbers were that day. It doesn’t drift with the market, so what you fund is what
                was actually scored.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Box>

      <Box
        id="clock"
        sx={{ scrollMarginTop: 24, maxWidth: 1100, mx: "auto", py: 8, px: { xs: 1, sm: 0 } }}
      >
        <Typography variant="h4" fontWeight={700} sx={{ mb: 1 }}>
          The simulation clock
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4, maxWidth: 640 }}>
          A real loan plays out over months or years. This one doesn’t make you wait for any of
          it, a background clock advances every active loan one repayment period at a time, so a
          three-year term can finish inside a single sitting.
        </Typography>

        <Grid container spacing={2}>
          <Grid item xs={12} sm={4}>
            <Paper sx={{ p: 3, height: "100%" }}>
              <Typography variant="h5" fontWeight={800} color="secondary.main">
                1 tick
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Every tick of the clock advances each active loan by one installment, due date,
                payment or missed payment, and all.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Paper sx={{ p: 3, height: "100%" }}>
              <Typography variant="h5" fontWeight={800} color="secondary.main">
                Grade-driven odds
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Whether a period gets paid or missed is weighted by the loan’s grade, a G defaults
                far more often than an A, the same gap you’d expect in the real thing.
              </Typography>
            </Paper>
          </Grid>
          <Grid item xs={12} sm={4}>
            <Paper sx={{ p: 3, height: "100%" }}>
              <Typography variant="h5" fontWeight={800} color="secondary.main">
                Live, not a snapshot
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Your portfolio updates as it happens. Watch a loan book age in minutes instead of
                imagining what it might look like in three years.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </Box>
  );
}
