package com.bankapp.ui;

import com.bankapp.exception.AccountNotFoundException;
import com.bankapp.exception.InsufficientFundsException;
import com.bankapp.model.Account;
import com.bankapp.model.AccountType;
import com.bankapp.model.Transaction;
import com.bankapp.service.BankService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * The single Swing window for the application. This class only handles
 * presentation: input collection, dialogs, and refreshing the account
 * table. All the real business logic (validation of balances, generating
 * account numbers, etc.) lives in {@link BankService}, which keeps this
 * class small and easy to test independently.
 */
public class BankMainFrame extends JFrame {

    private final BankService bankService = new BankService();

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"Account #", "Holder", "Type", "Balance"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    private final JTable accountTable = new JTable(tableModel);

    public BankMainFrame() {
        super("Bank Account Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildAccountTablePanel(), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(640, 420));
        pack();
        setLocationRelativeTo(null);
    }

    private JComponent buildHeader() {
        JLabel title = new JLabel("Bank Account Manager", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        return title;
    }

    private JComponent buildAccountTablePanel() {
        accountTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Accounts"));
        return scrollPane;
    }

    private JComponent buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton createButton = new JButton("Create Account");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton balanceButton = new JButton("Check Balance");
        JButton historyButton = new JButton("Transaction History");
        JButton exitButton = new JButton("Exit");

        createButton.addActionListener(e -> onCreateAccount());
        depositButton.addActionListener(e -> onDeposit());
        withdrawButton.addActionListener(e -> onWithdraw());
        balanceButton.addActionListener(e -> onCheckBalance());
        historyButton.addActionListener(e -> onShowHistory());
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(createButton);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(balanceButton);
        panel.add(historyButton);
        panel.add(exitButton);

        return panel;
    }

    // ---------------------------------------------------------------
    // Action handlers
    // ---------------------------------------------------------------

    private void onCreateAccount() {
        String name = JOptionPane.showInputDialog(this, "Account holder name:", "Create Account",
                JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isBlank()) {
            return; // user cancelled or left it empty
        }

        AccountType type = (AccountType) JOptionPane.showInputDialog(this, "Account type:", "Create Account",
                JOptionPane.PLAIN_MESSAGE, null, AccountType.values(), AccountType.SAVINGS);
        if (type == null) {
            return;
        }

        Account account = bankService.createAccount(name, type);
        refreshTable();
        JOptionPane.showMessageDialog(this,
                "Account created successfully.\nAccount number: " + account.getAccountNumber(),
                "Create Account", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDeposit() {
        Long accountNumber = promptForAccountNumber();
        if (accountNumber == null) {
            return;
        }
        BigDecimal amount = promptForAmount("Amount to deposit:");
        if (amount == null) {
            return;
        }
        try {
            bankService.deposit(accountNumber, amount);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Deposit successful.", "Deposit",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (AccountNotFoundException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onWithdraw() {
        Long accountNumber = promptForAccountNumber();
        if (accountNumber == null) {
            return;
        }
        BigDecimal amount = promptForAmount("Amount to withdraw:");
        if (amount == null) {
            return;
        }
        try {
            bankService.withdraw(accountNumber, amount);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Withdrawal successful.", "Withdraw",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (AccountNotFoundException | InsufficientFundsException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onCheckBalance() {
        Long accountNumber = promptForAccountNumber();
        if (accountNumber == null) {
            return;
        }
        try {
            BigDecimal balance = bankService.getBalance(accountNumber);
            JOptionPane.showMessageDialog(this, "Current balance: " + balance, "Balance",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (AccountNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void onShowHistory() {
        Long accountNumber = promptForAccountNumber();
        if (accountNumber == null) {
            return;
        }
        try {
            Account account = bankService.getAccount(accountNumber);
            List<Transaction> history = account.getHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No transactions yet.", "Transaction History",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Transaction t : history) {
                sb.append(t).append('\n');
            }
            JTextArea textArea = new JTextArea(sb.toString(), 15, 45);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Transaction History",
                    JOptionPane.PLAIN_MESSAGE);
        } catch (AccountNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private Long promptForAccountNumber() {
        String input = JOptionPane.showInputDialog(this, "Account number:");
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(input.trim());
        } catch (NumberFormatException ex) {
            showError("Account number must be a whole number.");
            return null;
        }
    }

    private BigDecimal promptForAmount(String message) {
        String input = JOptionPane.showInputDialog(this, message);
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(input.trim()).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            showError("Amount must be a valid number.");
            return null;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Account account : bankService.listAccounts()) {
            tableModel.addRow(new Object[]{
                    account.getAccountNumber(),
                    account.getAccountHolder(),
                    account.getType(),
                    account.getBalance()
            });
        }
    }
}
