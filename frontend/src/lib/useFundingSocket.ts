"use client";

import { useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import { API_BASE_URL } from "./api";
import type { FundingUpdate } from "./types";

/**
 * Subscribes to live funding-progress pushes for one loan listing.
 * Connects straight to the SockJS endpoint's raw WebSocket transport
 * (no sockjs-client dependency needed for a browser that already speaks
 * native WebSocket, which is every browser this app targets).
 */
export function useFundingSocket(loanListingId: string | undefined, onUpdate: (update: FundingUpdate) => void) {
  const onUpdateRef = useRef(onUpdate);

  useEffect(() => {
    onUpdateRef.current = onUpdate;
  }, [onUpdate]);

  useEffect(() => {
    if (!loanListingId) return;

    const wsUrl = API_BASE_URL.replace(/^http/, "ws") + "/ws/websocket";
    const client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 4000,
      onConnect: () => {
        client.subscribe(`/topic/marketplace/${loanListingId}`, (message) => {
          try {
            onUpdateRef.current(JSON.parse(message.body) as FundingUpdate);
          } catch {
            // Malformed push, ignore this one, the next tick will self-correct.
          }
        });
      },
    });

    client.activate();
    return () => {
      client.deactivate();
    };
  }, [loanListingId]);
}
