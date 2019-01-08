package np.com.spirrotech.dao.transaction_model;

public class Transaction {

        private static String name;
        private static float total;

        private String type,vehicleNo;

        private float rate,volume,price;


        private int quantity,mode;

        public void oilTransaction(String type,String vehicleNo,float volume,float price,int mode) {
            this.type=type;
            this.vehicleNo=vehicleNo;
            this.volume=volume;
            this.price=price;
            this.mode=mode;
        }

        public void extraTransaction(String itemName,float rate,int quantity) {
            this.type=itemName;
            this.rate=rate;
            this.quantity=quantity;
            this.mode=2;
        }

        public void calcExtraPrice() {
            price=rate*quantity;
        }

        public static String getName() {
            return name;
        }

        public static void setName(String name) {
            Transaction.name = name;
        }

        public static float getTotal() {
            return total;
        }

        public static void setTotal(float total) {
            Transaction.total = total;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVehicleNo() {
            return vehicleNo;
        }

        public void setVehicleNo(String vehicleNo) {
            this.vehicleNo = vehicleNo;
        }


        public float getRate() {
            return rate;
        }

        public void setRate(float rate) {
            this.rate = rate;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
