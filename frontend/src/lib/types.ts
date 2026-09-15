export type UserRole = "BORROWER" | "LENDER" | "ADMIN";

export type LoanStatus =
  | "PENDING_REVIEW"
  | "OPEN_FOR_FUNDING"
  | "FUNDED"
  | "ACTIVE"
  | "PAID_OFF"
  | "DEFAULTED"
  | "REJECTED";

export type InstallmentStatus = "SCHEDULED" | "PAID" | "MISSED" | "WRITTEN_OFF";

export interface MeResponse {
  userId: string;
  displayName: string;
  email: string;
  roles: UserRole[];
  walletBalance: number;
}

export interface ScoreFactor {
  label: string;
  points: number;
  maxPoints: number;
  detail: string;
}

export interface ScorecardResult {
  total: number;
  grade: string;
  factors: ScoreFactor[];
}

export interface ListingSummary {
  id: string;
  purpose: string;
  requestedAmount: number;
  fundedAmount: number;
  termMonths: number;
  riskGrade: string;
  interestRate: number;
  status: LoanStatus;
  listedAt: string;
}

export interface ListingDetail extends ListingSummary {
  scorecard: ScorecardResult;
}

export interface InstallmentView {
  periodNumber: number;
  principalDue: number;
  interestDue: number;
  remainingBalanceAfter: number;
  status: InstallmentStatus;
}

export interface PortfolioHolding {
  loanListingId: string;
  riskGrade: string;
  status: LoanStatus;
  principalCommitted: number;
  principalRepaid: number;
  interestRepaid: number;
}

export interface PortfolioSummary {
  totalPrincipalCommitted: number;
  totalPrincipalRepaid: number;
  totalInterestEarned: number;
  outstandingPrincipal: number;
  loansByGrade: Record<string, number>;
  holdings: PortfolioHolding[];
}

export interface LedgerEntry {
  id: string;
  walletId: string;
  type: string;
  amount: number;
  balanceAfter: number;
  relatedLoanId: string | null;
  memo: string;
  occurredAt: string;
}

export interface FundingUpdate {
  loanListingId: string;
  fundedAmount: number;
  requestedAmount: number;
  status: LoanStatus;
}
