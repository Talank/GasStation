package sample.model;

public class SystemStatus {
    private static float iDiesel,iPetrol,iCounter,fDiesel,fPetrol,dRate,pRate;

    private static String mainHomePath;

    public static void initializeInitial(DailyAnalyticsModel model) {
        iDiesel=model.getdInitial();
        iPetrol=model.getpInitial();
        iCounter=model.getcInitial();
    }

    public static float getiDiesel() {
        return iDiesel;
    }

    public static void setiDiesel(float iDiesel) {
        SystemStatus.iDiesel = iDiesel;
    }

    public static float getiPetrol() {
        return iPetrol;
    }

    public static void setiPetrol(float iPetrol) {
        SystemStatus.iPetrol = iPetrol;
    }

    public static float getfDiesel() {
        return fDiesel;
    }

    public static void setfDiesel(float fDiesel) {
        SystemStatus.fDiesel = fDiesel;
    }

    public static float getfPetrol() {
        return fPetrol;
    }

    public static void setfPetrol(float fPetrol) {
        SystemStatus.fPetrol = fPetrol;
    }

    public static float getdRate() {
        return dRate;
    }

    public static void setdRate(float dRate) {
        SystemStatus.dRate = dRate;
    }

    public static float getpRate() {
        return pRate;
    }

    public static void setpRate(float pRate) {
        SystemStatus.pRate = pRate;
    }

    public static float getiCounter() {
        return iCounter;
    }

    public static void setiCounter(float iCounter) {
        SystemStatus.iCounter = iCounter;
    }

    public static String getMainHomePath() {
        return mainHomePath;
    }

    public static void setMainHomePath(String mainHomePath) {
        SystemStatus.mainHomePath = mainHomePath;
    }
}
