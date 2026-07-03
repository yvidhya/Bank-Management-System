# Bank Account Manager

A desktop banking application built with **Java Swing**, structured around a
layered architecture (model / service / UI) and covered by **JUnit 5** unit
tests. Originally a single monolithic NetBeans-generated file, it has been
rebuilt into a small, testable, Maven-based project suitable for real-world
extension.

## Features

- Create multiple accounts (Savings or Checking), each with an
  auto-generated account number
- Deposit and withdraw funds, with validation against negative/zero amounts
  and insufficient balances
- Per-account transaction history (every deposit/withdrawal is timestamped
  and recorded)
- Live account table showing every account and its current balance
- Monetary values use `BigDecimal` instead of `double`, avoiding floating
  point rounding errors in financial calculations
- Custom checked exceptions (`InsufficientFundsException`,
  `AccountNotFoundException`) so error states are explicit and must be
  handled by callers
- Unit tests for both the domain model and the service layer

## Architecture

```
com.bankapp
├── Main.java                # application entry point
├── model/
│   ├── Account.java          # domain object: owns its own balance + history
│   ├── AccountType.java      # SAVINGS / CHECKING
│   └── Transaction.java      # immutable record of a deposit/withdrawal
├── exception/
│   ├── InsufficientFundsException.java
│   └── AccountNotFoundException.java
├── service/
│   └── BankService.java      # business logic: owns all accounts, generates
│                              # account numbers, orchestrates deposits/withdrawals
└── ui/
    └── BankMainFrame.java     # Swing window; talks only to BankService
```

The UI layer never manipulates account balances directly — it only calls
`BankService`, which is what makes the business logic unit-testable without
ever opening a window. This mirrors how a typical production application
separates presentation from business logic.

## Getting started

### Prerequisites
- JDK 17+
- Maven 3.8+

### Build and run
```bash
mvn clean package
java -jar target/bank-account-manager.jar
```

### Run the tests
```bash
mvn test
```

## Possible next steps

- Persist accounts to a database (e.g. H2 or SQLite) instead of in-memory storage
- Add a login/authentication screen per account holder
- Export transaction history to CSV/PDF
- Replace Swing with a JavaFX or web (Spring Boot) front end

## License

This project is available for personal/educational use.
