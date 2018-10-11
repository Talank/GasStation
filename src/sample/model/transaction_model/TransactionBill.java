package sample.model.transaction_model;

import java.util.ArrayList;

/**
 * @deprecated
 */

public class TransactionBill {

    protected ArrayList<Transaction> transactions;

    public TransactionBill(ArrayList<Transaction> transactions) {
        this.transactions=transactions;
    }
}
