import { describe, expect, it } from "vitest";
import { riskGradeColor } from "./theme";

describe("riskGradeColor", () => {
  it("returns a distinct color for each known grade", () => {
    const grades = ["A", "B", "C", "D", "E", "F", "G"];
    const colors = new Set(grades.map(riskGradeColor));

    expect(colors.size).toBe(grades.length);
  });

  it("falls back to the default color for an unrecognized grade", () => {
    expect(riskGradeColor("Z")).toBe("#6C8EFF");
  });
});
