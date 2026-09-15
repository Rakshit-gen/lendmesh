"use client";

import { useQuery } from "@tanstack/react-query";
import { Typography, Grid, Box, Skeleton, Alert } from "@mui/material";
import AppShell from "@/components/AppShell";
import RequireAuth from "@/components/RequireAuth";
import LoanCard from "@/components/LoanCard";
import { api, extractErrorMessage } from "@/lib/api";
import type { ListingSummary } from "@/lib/types";

function MarketplaceContent() {
  const { data, isLoading, error } = useQuery({
    queryKey: ["marketplace"],
    queryFn: async () => (await api.get<{ listings: ListingSummary[] }>("/api/loans")).data.listings,
  });

  return (
    <AppShell>
      <Typography variant="h4" fontWeight={700} sx={{ mb: 0.5 }}>
        The marketplace
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
        Every listing here is still short of its funding target. Fund a slice, and watch the bar move in real time as other lenders join in.
      </Typography>

      {error && <Alert severity="error">{extractErrorMessage(error)}</Alert>}

      <Grid container spacing={2.5}>
        {isLoading &&
          Array.from({ length: 6 }).map((_, i) => (
            <Grid item xs={12} sm={6} md={4} key={i}>
              <Skeleton variant="rounded" height={150} />
            </Grid>
          ))}

        {data?.map((listing) => (
          <Grid item xs={12} sm={6} md={4} key={listing.id}>
            <LoanCard listing={listing} />
          </Grid>
        ))}
      </Grid>

      {data && data.length === 0 && (
        <Box sx={{ textAlign: "center", py: 8 }}>
          <Typography variant="h6" color="text.secondary">
            Nothing open for funding right now
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Check back shortly, or list your own loan to get the marketplace moving.
          </Typography>
        </Box>
      )}
    </AppShell>
  );
}

export default function MarketplacePage() {
  return (
    <RequireAuth>
      <MarketplaceContent />
    </RequireAuth>
  );
}
