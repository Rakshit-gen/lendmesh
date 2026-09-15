"use client";

import { useEffect, useRef } from "react";

interface Ribbon {
  offset: number;
  amplitude: number;
  frequency: number;
  speed: number;
  y: number;
  color: string;
  width: number;
}

interface FundingNode {
  x: number;
  y: number;
  radius: number;
  phase: number;
  speed: number;
}

/**
 * The hero's backdrop, built around the thing LendMesh actually does: money
 * flowing in from many lenders until a loan fills up. Ribbons drift like
 * capital moving across the mesh, and each node fills its ring on a loop,
 * the same visual language as the funding bar on a listing card.
 */
export default function HeroBackground() {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const dpr = window.devicePixelRatio || 1;
    let width = 0;
    let height = 0;
    let ribbons: Ribbon[] = [];
    let nodes: FundingNode[] = [];
    let animationFrame = 0;
    let t = 0;

    function resize() {
      if (!canvas) return;
      width = canvas.width = canvas.offsetWidth * dpr;
      height = canvas.height = canvas.offsetHeight * dpr;

      ribbons = Array.from({ length: 5 }, (_, i) => ({
        offset: Math.random() * 1000,
        amplitude: (26 + Math.random() * 54) * dpr,
        frequency: 0.002 + Math.random() * 0.0022,
        speed: 0.35 + Math.random() * 0.5,
        y: (height / 6) * (i + 1),
        color: i % 2 === 0 ? "108, 142, 255" : "55, 230, 196",
        width: (1.1 + Math.random() * 1.5) * dpr,
      }));

      const density = Math.min(13, Math.max(6, Math.floor((canvas.offsetWidth * canvas.offsetHeight) / 65000)));
      nodes = Array.from({ length: density }, () => ({
        x: Math.random() * width,
        y: Math.random() * height,
        radius: (9 + Math.random() * 15) * dpr,
        phase: Math.random(),
        speed: 0.0018 + Math.random() * 0.0026,
      }));
    }

    function step() {
      if (!ctx) return;
      ctx.clearRect(0, 0, width, height);
      t += 1;

      for (const ribbon of ribbons) {
        const gradient = ctx.createLinearGradient(0, 0, width, 0);
        gradient.addColorStop(0, `rgba(${ribbon.color}, 0)`);
        gradient.addColorStop(0.5, `rgba(${ribbon.color}, 0.4)`);
        gradient.addColorStop(1, `rgba(${ribbon.color}, 0)`);
        ctx.strokeStyle = gradient;
        ctx.lineWidth = ribbon.width;
        ctx.beginPath();
        for (let x = 0; x <= width; x += 6 * dpr) {
          const y = ribbon.y + Math.sin(x * ribbon.frequency + ribbon.offset + t * 0.01 * ribbon.speed) * ribbon.amplitude;
          if (x === 0) ctx.moveTo(x, y);
          else ctx.lineTo(x, y);
        }
        ctx.stroke();
      }

      for (const node of nodes) {
        node.phase = (node.phase + node.speed) % 1;
        const fill = (Math.sin(node.phase * Math.PI * 2) + 1) / 2;

        const glow = ctx.createRadialGradient(node.x, node.y, 0, node.x, node.y, node.radius * 2.6);
        glow.addColorStop(0, `rgba(108, 142, 255, ${0.16 * fill})`);
        glow.addColorStop(1, "rgba(108, 142, 255, 0)");
        ctx.fillStyle = glow;
        ctx.beginPath();
        ctx.arc(node.x, node.y, node.radius * 2.6, 0, Math.PI * 2);
        ctx.fill();

        ctx.beginPath();
        ctx.arc(node.x, node.y, node.radius, 0, Math.PI * 2);
        ctx.strokeStyle = "rgba(255,255,255,0.14)";
        ctx.lineWidth = 1.2 * dpr;
        ctx.stroke();

        ctx.beginPath();
        ctx.arc(node.x, node.y, node.radius, -Math.PI / 2, -Math.PI / 2 + fill * Math.PI * 2);
        ctx.strokeStyle = "rgba(55, 230, 196, 0.8)";
        ctx.lineWidth = 1.6 * dpr;
        ctx.stroke();
      }

      animationFrame = requestAnimationFrame(step);
    }

    resize();
    step();
    window.addEventListener("resize", resize);
    return () => {
      window.removeEventListener("resize", resize);
      cancelAnimationFrame(animationFrame);
    };
  }, []);

  return (
    <canvas
      ref={canvasRef}
      aria-hidden
      style={{
        position: "absolute",
        inset: 0,
        width: "100%",
        height: "100%",
      }}
    />
  );
}
