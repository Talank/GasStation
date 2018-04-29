package sample.model.transaction_model;

import java.util.ArrayList;

public class TransactionBill {

    protected ArrayList<Transaction> transactions;

    public TransactionBill(ArrayList<Transaction> transactions) {
        this.transactions=transactions;
    }
}
