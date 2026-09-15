import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import Providers from "@/components/Providers";
import MeshBackground from "@/components/MeshBackground";

const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "LendMesh",
  description: "A peer-to-peer lending marketplace, fully simulated — no real money changes hands.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={inter.variable}>
      <body>
        <div className="gradient-glow" />
        <MeshBackground />
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
