import { describe, expect, it } from "vitest";
import { extractErrorMessage } from "./api";

describe("extractErrorMessage", () => {
  it("joins field-level validation details when present", () => {
    const error = {
      isAxiosError: true,
      response: {
        data: {
          status: 400,
          error: "Validation Failed",
          message: "One or more fields are invalid",
          details: ["email: must be a well-formed email address", "password: must be at least 8 characters"],
        },
      },
    };

    expect(extractErrorMessage(error)).toBe(
      "email: must be a well-formed email address, password: must be at least 8 characters"
    );
  });

  it("falls back to the message when there are no details", () => {
    const error = {
      isAxiosError: true,
      response: { data: { status: 409, error: "Conflict", message: "Email already in use", details: [] } },
    };

    expect(extractErrorMessage(error)).toBe("Email already in use");
  });

  it("falls back to a generic message for a non-axios error", () => {
    expect(extractErrorMessage(new Error("some unrelated failure"))).toBe(
      "Something went wrong. Please try again."
    );
  });
});
