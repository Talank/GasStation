package np.com.spirrotech.model.nepaliPriceFormat;

import java.text.DecimalFormat;

public class Precision {

    private DecimalFormat decimalFormat;

    public Precision() {
        decimalFormat = new DecimalFormat("#.00");
    }

    public String format(float number) {
        return decimalFormat.format(number);
    }


}
