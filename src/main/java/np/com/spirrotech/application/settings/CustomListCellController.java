package np.com.spirrotech.application.settings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;


import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import np.com.spirrotech.dao.DebtListModel;
public class CustomListCellController {

    @FXML
    private Label companyLabel,date,days;

    @FXML
    private ImageView imageView;

    private VBox vbox;

    public CustomListCellController() {
        try {
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("application/settings/customListCell.fxml"));
            loader.setController(this);
            vbox=loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setData(DebtListModel model) {
        companyLabel.setText(model.getCompanyName());
        date.setText("Credit Time: "+model.getDate());
        days.setText("Credit Cycle: "+model.getDays()+" Days");
        if(model.isExpired()) {
            companyLabel.setTextFill(Color.RED);
            date.setTextFill(Color.RED);
            days.setTextFill(Color.RED);
            imageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream("images/warn.png")));
        } else {
            companyLabel.setTextFill(Color.BLACK);
            date.setTextFill(Color.BLACK);
            days.setTextFill(Color.BLACK);
            imageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream("images/true.png")));
        }


    }

    public Node getView() {
        return vbox;
    }

}
