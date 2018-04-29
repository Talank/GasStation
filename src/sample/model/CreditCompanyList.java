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

    public static int[] getID(String companyName) {
        int[] arr={-1,-1};
        for(DebtListModel model:companyList) {
            if(model.getCompanyName().equals(companyName)) {
                arr[0]=model.getId();
                arr[1]=model.getTransactionID();
                return arr;
            }
        }
        return arr;
    }

}
