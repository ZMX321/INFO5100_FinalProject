package FinalProject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private TextArea info;
    @FXML private ImageView Bigimage;
    @FXML private ImageView Smallimage;
    @FXML private AnchorPane filter;
    @FXML private Button buttonRestore;
    @FXML private Button buttonDesaturate;
    @FXML private AnchorPane app;

    private smallimage simg;
    private  bigimage bimg;

    @FXML
    public void close(){
        Platform.exit();
    }

    @FXML
    public void Github_link(){
        // Check the resource on Github
        String urlString = "https://github.com/ShenYongji/INFO5100_FinalProject";
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Github");
    }

    @FXML
    public void addPicture(ActionEvent actionEvent) {
        // Create a new window that users are allowed to choose images from local
        Stage stage = (Stage) app.getScene().getWindow();
        //Filer selector only allowed picture format
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpeg","*.png", "*.jpg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(stage);
        //Check if the image is valid or not
        if(file != null){
            String path = file.getAbsolutePath();
            //initialize UI
            //Create two class to store image values
            //bimg: the bigger preview image that shows at the left side
            //sim (original image): the smaller preview image that shows at the top right corner.
            filter.setVisible(false);
            Bigimage.setEffect(null);
            Bigimage.setRotate(0);
            //Setting image structure
            bimg = new bigimage(path);
            simg = new smallimage(path);
            //Displaying both images on the frontend at the same time
            Smallimage.setImage(simg.get_Image());
            Bigimage.setImage(bimg.get_Image());
            //get the information of image sim (bimg may be added some filter)
            get_image_info(simg);
            simg.print_current_simg();
            bimg.print_current_bimg();
        }
    }


    @FXML
    public void ConvertFormat(ActionEvent actionEvent) {
        //System.out.println(bimg);
        //Output function
        if (bimg != null){
            //Loading conformation page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(main.class.getResource("Format.fxml"));
            AnchorPane page = null;
            try {
                page = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage convertStage = new Stage();
            convertStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            convertStage.setScene(scene);
            Fcontroller controller = loader.getController();
            //Passing UI and class bigimage
            controller.setConvertStage(convertStage,bimg);
            convertStage.showAndWait();
        }
        else{
            System.out.println("cannot convert");
        }
    }

    @FXML
    public void LeftRotate(ActionEvent actionEvent) {
        //Filter Function
        //Rotate left by 30 degree
        System.out.println("Left Rotate by 30");
        bimg.set_rotate(-30);
        System.out.println(bimg.get_rotate());
        //real-time changing the preview at the frond-end
        Bigimage.setRotate(Bigimage.getRotate() - 30);
    }

    @FXML
    public void RightRotate(ActionEvent actionEvent) {
        //Filter Function
        //Rotate right by 30 degree
        System.out.println("Right Rotate by 30");
        bimg.set_rotate(30);
        System.out.println(bimg.get_rotate());
        //real-time changing the preview at the frond-end
        Bigimage.setRotate(Bigimage.getRotate() + 30);
    }

    @FXML
    public void imageFilter(ActionEvent actionEvent){
        //Displaying the UI of Filter.
        //System.out.println(bimg);
        if (bimg != null) {
            filter.setVisible(!filter.isVisible());
        }
        else{
            System.out.println("cannot launch image filter");
        }
    }


    public void get_image_info(smallimage simg){
        //To get the information of image that user select from image structure.
        info.setText(
                "File Name: " +simg.get_filename_extension() +"\n"+
                        "File Size:  " + simg.get_data().get("File Size")+ "\n" +
                        "Image Height: " + simg.get_height().toString()+" Pixels"+ "\n" +
                        "Image Width: "+simg.get_width().toString()+" Pixels");
        info.setWrapText(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filter.setVisible(false);
    }

    public void setDesaturate(ActionEvent actionEvent) {
        System.out.println("setDesaturate clicked");
        // desaturate on preview image
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1);
        // real-time change at the front-end
        Bigimage.setEffect(colorAdjust);
        Bigimage.setCache(true);
        Bigimage.setCacheHint(CacheHint.SPEED);
        // set the status of desaturation at the backend
        //need this when we convert image as output
        bimg.setdesaturate(true);

        //System.out.println(bimg.get_desaturate());


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulation!");
        alert.setHeaderText(null);
        alert.setContentText("Now you get a desaturated picture!");
        alert.showAndWait();


    }

    public void setRestore(ActionEvent actionEvent) {
        //reset front-end and back-end
        //alert to user as well
        if(bimg.get_desaturate() || bimg.get_rotate()!=0){
            bimg.setdesaturate(false);
            bimg.set_rotate(0);

            Bigimage.setEffect(null);
            Bigimage.setRotate(0);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congratulation!");
            alert.setHeaderText(null);
            alert.setContentText("You have successfully reset the photo！");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Illegal operation!");
            alert.setHeaderText(null);
            alert.setContentText("You did not make any changes on this photo yet!");
            alert.showAndWait();
        }
    }
}
