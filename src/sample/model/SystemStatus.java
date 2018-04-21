package sample.model;

public class SystemStatus {
    private static float iDiesel,iPetrol,fDiesel,fPetrol,dRate,pRate;

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
}
