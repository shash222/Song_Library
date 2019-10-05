/*
 *  Mohammed Alnadi (ma1322)
 *  Salman Hashmi (sah285)
 *  
 *  Note: We are using the GSON library in this project. GSON version 2.8.5
 *  Download link: https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5 
 */

package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SongLib extends Application {

    public static void main(String[] args) {
        launch(args);
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Song Library");
        primaryStage.setScene(new Scene(root, 500, 675));
        primaryStage.show();
    }
}
