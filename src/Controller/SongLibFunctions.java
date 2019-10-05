package Controller;

import Model.Song;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

import com.google.gson.*;


public class SongLibFunctions implements Initializable {
    @FXML
    private TextField song;
    @FXML
    private TextField artist;
    @FXML
    private TextField album;
    @FXML
    private TextField year;
    @FXML
    private Label albumLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private ListView<String> songList;
    @FXML
    private CheckBox showDetails;

    // Modify song list fields
    @FXML
    private TextField newSongName;
    @FXML
    private TextField newSongArtist;
    @FXML
    private TextField newSongAlbum;
    @FXML
    private TextField newSongYear;
    @FXML
    private Label newSongNameLabel;
    @FXML
    private Label newSongArtistLabel;
    @FXML
    private Label newSongAlbumLabel;
    @FXML
    private Label newSongYearLabel;
    @FXML
    private Button editListButton;

    private List<String> songs = new ArrayList<>();
    private Map<String, Song> songNameToPOJOMap = new HashMap<>();

    // editValue is used to keep track of what type of edit is being made to the songlist (add song or edit song)
    // Done this way because both of those use same textfields, so used in order to differentiate between the operations
    private String editValue = "";

    private static final String SONG_LIST_FILE_PATH = "src/res/songs.txt";

    /**
     * Song is stored in songs list in format "Song Name - Artist Name"
     * @param songName name of song
     * @param artist name of artist
     * @return String in form of "songName - artist"
     */
    private String getSongIdentifier(String songName, String artist) {
        return String.format("%s - %s", songName, artist);
    }

    /**
     * Adds all songs (if any) listed in songs.txt to list and hashmap to refer to later
     * Songs listed in file are in json array
     */
    private void setup() {
        displayEditFields(false, false);
        Gson gson = new Gson();
        StringBuilder persistedSongs = new StringBuilder();
        showDetails.setSelected(true); 
        try {
            Scanner sc = new Scanner(new File(SONG_LIST_FILE_PATH));
            while (sc.hasNext()) {
                persistedSongs.append(sc.next());
            }
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(persistedSongs.toString());
            JsonArray jsonArray = new JsonArray();
            if (jsonElement.isJsonArray()) {
                jsonArray = jsonElement.getAsJsonArray();
            }
            for (JsonElement el : jsonArray) {
                Song song = gson.fromJson(el, Song.class);
                String songIdentifier = getSongIdentifier(song.getSongName(), song.getArtist());
                songs.add(songIdentifier);
                songNameToPOJOMap.put(songIdentifier.toLowerCase(), song);
            }
            songList.setItems(FXCollections.observableList(songs));
            sortList();

        } catch(FileNotFoundException e){
                System.out.println("File not found");
        }
        if(songList.getItems().size() > 0) {
        	songList.getSelectionModel().select(0);
        	 try {
                 showSongDetails();
             } catch(Exception e) { }
        }
    }

    /**
     * Method is run as soon as program is run, instead of as a listener
     * @param location required param when implementing "Initializable"
     * @param resources required param when implementing "Initializable"
     */
    public void initialize(URL location, ResourceBundle resources) {
        setup();
    }

    /**
     * Sorts ListView list
     */
    private void sortList() {
        // Lambda expression takes place of instantiating new Comparator, which is defined between the brackets
        songList.getItems().sort((o1, o2) -> {
            String[] splitSongIdentifier1 = o1.split("-");
            String[] splitSongIdentifier2 = o2.split("-");
            int songNameComparisonValue = splitSongIdentifier1[0].compareToIgnoreCase(splitSongIdentifier2[0]);
            return (songNameComparisonValue == 0)
                    ? splitSongIdentifier1[1].compareToIgnoreCase(splitSongIdentifier2[1])
                    : songNameComparisonValue;
        });
    }

    /**
     * Triggered once an item in the list is clicked on
     * @param event required for methods that are called by the UI components (called in the View/main.fxml file)
     */
    @FXML
    private void onSongSelection(MouseEvent event) {
        try {
            showSongDetails();
        } catch(Exception e) { }
    }

    /**
     * Displays textfields that show Album name and year song was released
     */
    private void showSongDetails() {
        String songIdentifier = songList.getSelectionModel().getSelectedItem();
        Song selectedSong = getSong(songIdentifier);
        song.setText(selectedSong.getSongName());
        artist.setText(selectedSong.getArtist());
        album.setText(selectedSong.getAlbum());
        year.setText(Integer.toString(selectedSong.getYear()));
    }

    /**
     * Shows song details when CheckBox is marked
     * @param event Event parameter is required to link fxml to Java code
     */
    @FXML
    private void showSongDetailFields(ActionEvent event) {
        boolean showDetailsValue = showDetails.isSelected();
        album.setVisible(showDetailsValue);
        year.setVisible(showDetailsValue);
        albumLabel.setVisible(showDetailsValue);
        yearLabel.setVisible(showDetailsValue);
    }

    /**
     * Refreshes list after it has been edited (either by adding or editing a song)
     */
    private void refreshList() {
        sortList();
        songList.refresh();
        showSongDetails();
    }

    /**
     * Shows edit fields if "Add" or "Edit" is selected
     * @param display boolean whether to display fields or not
     * @param populate boolean whether to populate fields with selected song details or not
     *                 (populates for "Edit" but not for "Add")
     */
    private void displayEditFields(boolean display, boolean populate) {
        newSongName.setVisible(display);
        newSongArtist.setVisible(display);
        newSongAlbum.setVisible(display);
        newSongYear.setVisible(display);
        newSongNameLabel.setVisible(display);
        newSongArtistLabel.setVisible(display);
        newSongAlbumLabel.setVisible(display);
        newSongYearLabel.setVisible(display);
        editListButton.setVisible(display);
        if (populate) {
            Song song = getSong(songList.getSelectionModel().getSelectedItem());
            newSongName.setText(song.getSongName());
            newSongArtist.setText(song.getArtist());
            newSongAlbum.setText(song.getAlbum());
            newSongYear.setText(Integer.toString(song.getYear()));

        } else {
            newSongName.setText("");
            newSongArtist.setText("");
            newSongAlbum.setText("");
            newSongYear.setText("");
        }
    }

    /**
     * Assigns "add" to editValue to be used when saving changes, and displays edit list fields
     * @param event Event parameter is required to link fxml to Java code
     */
    @FXML
    private void addSong(ActionEvent event) {
        displayEditFields(true, false);
        editValue = "add";
    }

    /**
     * Displays popup with a given message
     * Prints out for now for testing purposes
     * TODO create popups (or error messages can be displayed in the same window, either  works)
     * @param message
     */
    private void displayPopup(String message) {
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Error");
    	alert.setHeaderText("Hmm, we have an error.");
    	alert.setContentText(message);
    	alert.showAndWait();
    }

    /**
     * Assigns "edit" to editValue to be used when saving changes, and displays edit list fields
     * @param event Event parameter is required to link fxml to Java code
     */
    @FXML
    private void editSong(ActionEvent event) {
        if (songList.getSelectionModel().getSelectedItem() == null) {
            displayPopup("No song is selected to be edited");
            // TODO display popup saying nothing was selected
            return;
        }
        editValue = "edit";
        displayEditFields(true, true);
    }

    /**
     * Checks if the song already exists by checking map, compares to see if any other song
     * matches song name and artist exactly
     * @param songName name of song
     * @param artist name of artist
     * @return whether song already exists
     */
    private boolean songExists(String songName, String artist) {
        if (songNameToPOJOMap.containsKey(getSongIdentifier(songName, artist).toLowerCase())) {
            displayPopup("Song already exists");
            return true;
        }
        return false;
    }

    /**
     * Modifies the properties of a given song instance
     * @param song song instance to modify
     * @param field field of song to modify
     * @param editValue value to change song instance field to
     * @return return modified song instance
     */
    private Song editSong(Song song, String field, String editValue) {
        if (!editValue.isEmpty()) {
            switch (field) {
                case "songName":
                    song.setSongName(editValue);
                    break;
                case "artist":
                    song.setArtist(editValue);
                    break;
                case "album":
                    song.setAlbum(editValue);
                    break;
                case "year":
                    song.setYear(Integer.parseInt(editValue));
                    break;
            }
        }
        return song;
    }

    /**
     * Gets song identifier that is currently selected in ListView
     * @return return song identifier of currently selected song
     */
    @FXML
    private String getSelectedSongIdentifier() {
        return songList.getSelectionModel().getSelectedItem();
    }

    /**
     * Performs list modification logic, checks to see conditions are sufficiently met and
     * either adds songs or edits the selected song
     * Checks if song already exists and if any of the required fields (song name or artist)
     * are missing
     * TODO allow the user to cancel the operation if desired
     * @param event Event parameter is required to link fxml to Java code
     */
    @FXML
    private void saveChanges(ActionEvent event) {
        if (editValue.equals("add")) {
            if (newSongName.getText().isEmpty() || newSongArtist.getText().isEmpty()) {
                displayPopup("Missing song name or artist");
                // TODO add missing data logic
                return;
            }
            if (songExists(newSongName.getText(), newSongArtist.getText())) {
                displayPopup("Song already exists");
                // TODO add repeated song logic
            } else {
                int newSongYearValue = 0;
                String newSongYearAsString = newSongYear.getText();
                try {
                    if (!newSongYearAsString.isEmpty()) {
                        newSongYearValue = Integer.parseInt(newSongYearAsString);
                    }
                } catch (NumberFormatException e) {
                    displayPopup("Invalid year entered. Song will be added, however.");
                    // TODO add  popup  saying invalid year entered, will replace with 0, can edit song with year in proper format if desired
                }

                String newSongIdentifier = getSongIdentifier(newSongName.getText(), newSongArtist.getText());
                songList.getItems().add(newSongIdentifier);
                songNameToPOJOMap.put(newSongIdentifier.toLowerCase(), new Song(newSongName.getText(), newSongArtist.getText(),
                        newSongAlbum.getText(), newSongYearValue));
                songList.getSelectionModel().select(newSongIdentifier);
                displayEditFields(false, false);
            }
        } else if (editValue.equals("edit")) {
            String selectedSongIdentifier = getSelectedSongIdentifier();
            Song selectedSong =getSong(selectedSongIdentifier);
            String[] splitSelectedSongIdentifier = selectedSongIdentifier.split(" - ");
            Song modifiedSong = editSong(selectedSong, "songName", newSongName.getText());
            modifiedSong = editSong(selectedSong, "artist", newSongArtist.getText());
            modifiedSong = editSong(selectedSong, "album", newSongAlbum.getText());
            modifiedSong = editSong(selectedSong, "year", newSongYear.getText());
            String modifiedSongIdentifier = getSongIdentifier(modifiedSong.getSongName(), modifiedSong.getArtist());

            // checks to see if the new song id is the same as the selected, if so, then update that song because the other fields could have been modified
            // otherwise, if it exists then display pop-up saying that the song already exists
            if (!modifiedSongIdentifier.equals(selectedSongIdentifier) && songNameToPOJOMap.containsKey(modifiedSongIdentifier)) {
                displayPopup("Song already exists");
                // TODO Handle case when song already exists
                return;
            } else {
                songList.getItems().remove(selectedSongIdentifier);
                songList.getItems().add(modifiedSongIdentifier);
                songNameToPOJOMap.remove(selectedSongIdentifier.toLowerCase());
                songNameToPOJOMap.put(modifiedSongIdentifier.toLowerCase(), modifiedSong);
                songList.getSelectionModel().select(modifiedSongIdentifier);
                displayEditFields(false, false);
            }
        }
        refreshList();
        updateFile();
        editValue = "";
    }

    /**
     * Removes song from list and selects next item in list
     * TODO when item is removed, next item is selected as desired, but the song details fields don't get populated with new values unless window is clicked again
     * @param event Event parameter is required to link fxml to Java code
     */
    @FXML
    private void removeSong(ActionEvent event) {
        if (songList.getSelectionModel().getSelectedItem() == null) {
            displayPopup("No song is selected to be removed");
            return;
        }
        String songIdentifier = getSelectedSongIdentifier();
        int selectedSongIndex = songList.getSelectionModel().getSelectedIndex();
        songList.getItems().remove(songIdentifier);
        songNameToPOJOMap.remove(songIdentifier);
        if (selectedSongIndex < songList.getItems().size() - 1) {
            songList.getSelectionModel().select(selectedSongIndex);
            showSongDetails();
        }
        updateFile();
    }

    /**
     * Gets the song instance marked by song identifier
     * Map stores identifier in all lower case
     * @param songIdentifier song identifier to get desired song instance
     * @return desired song instance
     */
    private Song getSong(String songIdentifier) {
        return songNameToPOJOMap.get(songIdentifier.toLowerCase());
    }

    /**
     * Updates songs.txt to reflect any changes to the list
     * File is re-written every time a song is added, removed or edited
     */
    private void updateFile() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(SONG_LIST_FILE_PATH));
            JsonArray jsonArray = new JsonArray();
            for (String songIdentifier : songNameToPOJOMap.keySet()) {
                Song song = getSong(songIdentifier);
                jsonArray.add(gson.toJsonTree(getSong(songIdentifier)));
            }
            System.out.println(gson.toJson(jsonArray));
            bw.write(gson.toJson(jsonArray));
            bw.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFound");
        } catch (IOException e) {
            System.out.println("IOFound");
        }
    }
}
