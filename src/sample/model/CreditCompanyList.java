package sample.model;

import sample.model.databases.DatabaseHandler;

import java.util.ArrayList;

public class CreditCompanyList {
    private static ArrayList<DebtListModel> companyList=new ArrayList<>();

    public static void loadData() {
        companyList=DatabaseHandler.getDebtCompany();
    }

    public static void invalidate() {
        companyList.clear();
        loadData();
    }

    public static ArrayList<DebtListModel> getDetailCompanyList() {
        return companyList;
    }

    public static ArrayList<String> getCompanyList() {
        ArrayList<String> nameList=new ArrayList<>();
        for (DebtListModel model:companyList) {
            nameList.add(model.getCompanyName());
        }
        return nameList;
    }
}
