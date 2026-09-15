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

const NAV_LINKS = [
  { label: "Marketplace", href: "/marketplace" },
  { label: "How scoring works", href: "/#scoring" },
  { label: "The simulation clock", href: "/#clock" },
];

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

          <Box sx={{ flexGrow: 1, minHeight: 32 }} />

          {/* Headline + quick-start card */}
          <Stack
            direction={{ xs: "column", lg: "row" }}
            justifyContent="space-between"
            alignItems={{ lg: "flex-end" }}
            gap={4}
            sx={{ width: "100%", maxWidth: 1180, mx: "auto" }}
          >
            <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.6 }}>
              <Typography
                sx={{
                  color: "white",
                  fontSize: { xs: "2.1rem", sm: "2.6rem", md: "3.2rem" },
                  fontWeight: 600,
                  lineHeight: 1.15,
                  maxWidth: 620,
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
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 0.15 }}
              style={{ width: "100%", maxWidth: 480 }}
            >
              <QuickStartCard />
            </motion.div>
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
    </Box>
  );
}
