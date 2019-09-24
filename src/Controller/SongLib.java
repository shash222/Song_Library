package Controller;

import Model.Song;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class SongLib {
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


    private List<String> testSongs = new ArrayList<>();
    private Map<String, Song> songNameToPOJOMap = new HashMap<>();

    @FXML
    private void testSetup(ActionEvent event) {
        for (int i = 20; i >= 1; i--) {
            String songIdentifier = String.format("Song%d - Artist%d", i, i);
            Song song = new Song("Song" + i, "Arist" + i, "Album" + i, i);
            testSongs.add(songIdentifier);
            songNameToPOJOMap.put(songIdentifier.toLowerCase(), song);
        }
        sortList(testSongs);
        songList.setItems(FXCollections.observableList(testSongs));
    }

    private void printList(List<Song> list) {
        list.forEach(s -> {
            System.out.println(s.getSongName());
            System.out.println(s.getArtist());
            System.out.println(s.getAlbum());
            System.out.println(s.getYear());
            System.out.println();
        });
    }

    private void sortList(List<String> list) {
        // Lambda expression takes place of instantiating new Comparator, which is defined between the brackets
        Collections.sort(list, (o1, o2) -> {
            String[] splitSongIdentifier1 = o1.split("-");
            String[] splitSongIdentifier2 = o2.split("-");
            int songNameComparisonValue = splitSongIdentifier1[0].compareToIgnoreCase(splitSongIdentifier2[0]);
            return (songNameComparisonValue == 0)
                    ? splitSongIdentifier1[1].compareToIgnoreCase(splitSongIdentifier2[1])
                    : songNameComparisonValue;
        });
    }

    @FXML
    private void onSongSelection(MouseEvent event) {
        try {
            String songEntry = songList.getSelectionModel().getSelectedItem();
            Song selectedSong = songNameToPOJOMap.get(songEntry.toLowerCase());
            song.setText(selectedSong.getSongName());
            artist.setText(selectedSong.getArtist());
            album.setText(selectedSong.getAlbum());
            year.setText(Integer.toString(selectedSong.getYear()));
        } catch(Exception e) {

        }
    }

    /**
     * Shows song details when CheckBox is marked
     * @param event ActionEvent parameter is required to link fxml to Java code
     */
    @FXML
    private void showSongDetails(ActionEvent event) {
        if (showDetails.isSelected()) {
            album.setVisible(true);
            year.setVisible(true);
            albumLabel.setVisible(true);
            yearLabel.setVisible(true);
        } else {
            album.setVisible(false);
            year.setVisible(false);
            albumLabel.setVisible(false);
            yearLabel.setVisible(false);
        }
    }

}
