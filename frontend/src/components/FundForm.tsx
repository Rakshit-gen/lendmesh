"use client";

import { FormEvent, useState } from "react";
import { Box, TextField, Button, Typography, Alert, Stack } from "@mui/material";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api, extractErrorMessage } from "@/lib/api";
import type { ListingDetail } from "@/lib/types";

export default function FundForm({ listing }: { listing: ListingDetail }) {
  const [amount, setAmount] = useState("");
  const queryClient = useQueryClient();
  const remaining = listing.requestedAmount - listing.fundedAmount;

  const mutation = useMutation({
    mutationFn: async () =>
      api.post(`/api/loans/${listing.id}/fund`, { amount: Number(amount) }),
    onSuccess: async () => {
      setAmount("");
      await queryClient.invalidateQueries({ queryKey: ["loan", listing.id] });
      await queryClient.invalidateQueries({ queryKey: ["me"] });
    },
  });

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    mutation.mutate();
  }

  if (listing.status !== "OPEN_FOR_FUNDING") {
    return (
      <Alert severity="info">This listing is no longer accepting new funding.</Alert>
    );
  }

  return (
    <Box component="form" onSubmit={handleSubmit}>
      <Typography variant="subtitle1" fontWeight={700} sx={{ mb: 1.5 }}>
        Fund a slice of this loan
      </Typography>
      {mutation.isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {extractErrorMessage(mutation.error)}
        </Alert>
      )}
      <Stack direction="row" gap={1.5}>
        <TextField
          size="small"
          type="number"
          label="Amount"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          inputProps={{ min: 1, max: remaining, step: "0.01" }}
          helperText={`Up to $${remaining.toFixed(2)} remaining`}
          fullWidth
        />
        <Button type="submit" variant="contained" disabled={mutation.isPending || !amount}>
          {mutation.isPending ? "Funding..." : "Fund"}
        </Button>
      </Stack>
    </Box>
  );
}
