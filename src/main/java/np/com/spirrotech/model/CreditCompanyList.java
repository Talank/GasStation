package np.com.spirrotech.model;



import java.util.ArrayList;

public class CreditCompanyList {
    private static ArrayList<DebtListModel> companyList=new ArrayList<>();

    private static ArrayList<DebtListModel> expiredCompanyList=new ArrayList<>();


    public static void loadData() {
//        companyList=DatabaseHandler.getDebtCompany();

    }

    public static void setCompanyList(ArrayList<DebtListModel> companyList) {
        CreditCompanyList.companyList=companyList;
    }

    public static ArrayList<DebtListModel> getExpiredCompanyList() {
        return expiredCompanyList;
    }

    public static void setExpiredCompanyList(ArrayList<DebtListModel> expiredCompanyList) {
        CreditCompanyList.expiredCompanyList = expiredCompanyList;
    }

    public static void invalidateExpiredComanyList() {
        expiredCompanyList.clear();
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



    public static DebtListModel getCompany(String companyName) {

        DebtListModel models=null;

        for(DebtListModel model:companyList) {
            if(model.getCompanyName().equals(companyName)) {
                models=model;
            }
        }
        return models;
    }


}
