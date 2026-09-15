"use client";

import { FormEvent, Suspense, useState } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Box, Paper, TextField, Typography, Button, Stack, Alert, Chip } from "@mui/material";
import { motion } from "framer-motion";
import { useAuth } from "@/lib/auth";
import { extractErrorMessage } from "@/lib/api";
import type { UserRole } from "@/lib/types";

const ROLE_OPTIONS: { value: UserRole; label: string }[] = [
  { value: "BORROWER", label: "Borrow" },
  { value: "LENDER", label: "Lend" },
];

function RegisterForm() {
  const { register } = useAuth();
  const searchParams = useSearchParams();

  const [displayName, setDisplayName] = useState(searchParams.get("name") ?? "");
  const [email, setEmail] = useState(searchParams.get("email") ?? "");
  const [password, setPassword] = useState("");
  const [roles, setRoles] = useState<UserRole[]>(() => {
    const fromQuery = searchParams.get("roles");
    const parsed = fromQuery?.split(",").filter((r): r is UserRole => r === "BORROWER" || r === "LENDER");
    return parsed && parsed.length > 0 ? parsed : ["LENDER"];
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function toggleRole(role: UserRole) {
    setRoles((prev) => (prev.includes(role) ? prev.filter((r) => r !== role) : [...prev, role]));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    if (roles.length === 0) {
      setError("Pick at least one: borrow, lend, or both.");
      return;
    }
    setSubmitting(true);
    try {
      await register(displayName, email, password, roles);
    } catch (err) {
      setError(extractErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Box sx={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", p: 2 }}>
      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 0.4 }}>
        <Paper sx={{ p: 4, width: 420, maxWidth: "90vw" }}>
          <Typography variant="h5" fontWeight={700} sx={{ mb: 0.5 }}>
            Create your account
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            You&apos;ll start with $10,000 in simulated cash. Nothing here is real money.
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Stack component="form" onSubmit={handleSubmit} gap={2}>
            <TextField
              label="Full name"
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              required
              fullWidth
            />
            <TextField
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              fullWidth
            />
            <TextField
              label="Password"
              type="password"
              helperText="At least 8 characters"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              fullWidth
            />

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

            <Button type="submit" variant="contained" size="large" disabled={submitting}>
              {submitting ? "Creating account..." : "Create account"}
            </Button>
          </Stack>

          <Typography variant="body2" color="text.secondary" sx={{ mt: 3, textAlign: "center" }}>
            Already have an account?{" "}
            <Typography component={Link} href="/login" variant="body2" color="primary.light" sx={{ fontWeight: 600 }}>
              Sign in
            </Typography>
          </Typography>
        </Paper>
      </motion.div>
    </Box>
  );
}

export default function RegisterPage() {
  return (
    <Suspense fallback={null}>
      <RegisterForm />
    </Suspense>
  );
}
