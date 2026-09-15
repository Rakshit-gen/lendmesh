"use client";

import { ReactNode } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  AppBar,
  Toolbar,
  Typography,
  Box,
  Button,
  Chip,
  Stack,
  Container,
} from "@mui/material";
import { motion } from "framer-motion";
import HubIcon from "@mui/icons-material/Hub";
import { useAuth } from "@/lib/auth";

const NAV_LINKS = [
  { href: "/marketplace", label: "Marketplace" },
  { href: "/apply", label: "Apply for a loan" },
  { href: "/portfolio", label: "My portfolio" },
  { href: "/wallet", label: "Wallet" },
];

export default function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const { me, logout } = useAuth();

  return (
    <Box sx={{ minHeight: "100vh", display: "flex", flexDirection: "column" }}>
      <AppBar position="sticky" elevation={0} sx={{ background: "rgba(11,15,26,0.65)", backdropFilter: "blur(16px)" }}>
        <Toolbar sx={{ gap: 3 }}>
          <Stack direction="row" alignItems="center" gap={1} component={Link} href="/marketplace" sx={{ color: "inherit" }}>
            <HubIcon color="secondary" />
            <Typography variant="h6" fontWeight={700}>
              LendMesh
            </Typography>
          </Stack>

          <Stack direction="row" gap={1} sx={{ flexGrow: 1, display: { xs: "none", md: "flex" } }}>
            {NAV_LINKS.map((link) => {
              const active = pathname?.startsWith(link.href);
              return (
                <Button
                  key={link.href}
                  component={Link}
                  href={link.href}
                  color={active ? "primary" : "inherit"}
                  sx={{
                    opacity: active ? 1 : 0.7,
                    fontWeight: active ? 700 : 500,
                  }}
                >
                  {link.label}
                </Button>
              );
            })}
          </Stack>

          <Box sx={{ flexGrow: 1, display: { md: "none" } }} />

          {me ? (
            <Stack direction="row" alignItems="center" gap={1.5}>
              <Chip
                component={motion.div}
                initial={{ opacity: 0, y: -4 }}
                animate={{ opacity: 1, y: 0 }}
                label={`$${me.walletBalance.toLocaleString(undefined, { minimumFractionDigits: 2 })}`}
                color="secondary"
                variant="outlined"
                size="small"
              />
              <Typography variant="body2" sx={{ display: { xs: "none", sm: "block" }, opacity: 0.75 }}>
                {me.displayName}
              </Typography>
              <Button size="small" onClick={logout} color="inherit">
                Sign out
              </Button>
            </Stack>
          ) : (
            <Stack direction="row" gap={1}>
              <Button component={Link} href="/login" color="inherit">
                Sign in
              </Button>
              <Button component={Link} href="/register" variant="contained" color="primary">
                Get started
              </Button>
            </Stack>
          )}
        </Toolbar>
      </AppBar>

      <Container component="main" maxWidth="lg" sx={{ flexGrow: 1, py: 4 }}>
        {children}
      </Container>
    </Box>
  );
}
