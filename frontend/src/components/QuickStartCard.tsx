"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { Box, Typography, TextField, Stack, Button, Chip, Divider } from "@mui/material";
import type { UserRole } from "@/lib/types";

const ROLE_OPTIONS: { value: UserRole; label: string }[] = [
  { value: "BORROWER", label: "Borrow" },
  { value: "LENDER", label: "Lend" },
];

export default function QuickStartCard() {
  const router = useRouter();
  const [displayName, setDisplayName] = useState("");
  const [email, setEmail] = useState("");
  const [roles, setRoles] = useState<UserRole[]>(["LENDER"]);
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  function toggleRole(role: UserRole) {
    setRoles((prev) => (prev.includes(role) ? prev.filter((r) => r !== role) : [...prev, role]));
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    await new Promise((resolve) => setTimeout(resolve, 700));
    setSubmitting(false);
    setDone(true);

    const params = new URLSearchParams({
      name: displayName,
      email,
      roles: roles.join(","),
    });
    setTimeout(() => router.push(`/register?${params.toString()}`), 800);
  }

  return (
    <Box
      sx={{
        width: { xs: "100%", md: "min(440px, 45%)" },
        flexShrink: 0,
        bgcolor: "background.paper",
        border: "1px solid rgba(255,255,255,0.08)",
        borderRadius: 4,
        p: { xs: 3, sm: 3.5 },
        backdropFilter: "blur(24px)",
        boxShadow: "0 24px 60px rgba(0,0,0,0.35)",
      }}
    >
      {done ? (
        <Stack alignItems="center" justifyContent="center" textAlign="center" py={3} gap={1.5}>
          <Box
            sx={{
              width: 48,
              height: 48,
              borderRadius: "50%",
              bgcolor: "rgba(61,220,151,0.12)",
              color: "success.main",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: 22,
            }}
          >
            ✓
          </Box>
          <Typography variant="subtitle1" fontWeight={700}>
            You&apos;re set — one step left
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Taking you to set a password now.
          </Typography>
        </Stack>
      ) : (
        <Stack component="form" onSubmit={handleSubmit} gap={2}>
          <Typography variant="h6" fontWeight={700}>
            Get a simulated account in seconds
          </Typography>

          <Box sx={{ bgcolor: "rgba(255,255,255,0.03)", borderRadius: 3, px: 2, py: 1.25 }}>
            <Typography variant="caption" color="text.secondary">
              Every new account starts with $10,000 in simulated cash
            </Typography>
          </Box>

          <Stack direction="row" alignItems="center" gap={1.5}>
            <Divider sx={{ flex: 1 }} />
            <Typography variant="caption" color="text.secondary">
              tell us a bit about you
            </Typography>
            <Divider sx={{ flex: 1 }} />
          </Stack>

          <Stack direction={{ xs: "column", sm: "row" }} gap={1.5}>
            <TextField
              size="small"
              fullWidth
              label="Name"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              required
            />
            <TextField
              size="small"
              fullWidth
              type="email"
              label="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </Stack>

          <Box>
            <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: "block" }}>
              I want to
            </Typography>
            <Stack direction="row" gap={1}>
              {ROLE_OPTIONS.map((option) => {
                const active = roles.includes(option.value);
                return (
                  <Chip
                    key={option.value}
                    label={option.label}
                    onClick={() => toggleRole(option.value)}
                    color={active ? "primary" : "default"}
                    variant={active ? "filled" : "outlined"}
                    sx={{ fontWeight: 600 }}
                  />
                );
              })}
            </Stack>
          </Box>

          <Button type="submit" variant="contained" size="large" disabled={submitting || roles.length === 0}>
            {submitting ? "One moment..." : "Continue"}
          </Button>
        </Stack>
      )}
    </Box>
  );
}
