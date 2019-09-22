package Controller;

import Model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SongLib {
    private static List<Song> testSongs = new ArrayList<>();
    private static List<Song> testSongs2 = new ArrayList<>();

    private static void testSetup() {
        for (int i = 20; i >= 1; i--) {
            testSongs.add(new Song("Song" + i, "Arist" + i, "Album" + i, i));
        }
        for (int i = 20; i >= 1; i--) {
            testSongs2.add(new Song("Song", "Arist" + i, "Album" + i, i));
        }
    }

    private static void printList(List<Song> list) {
        list.forEach(s -> {
            System.out.println(s.getSongName());
            System.out.println(s.getArtist());
            System.out.println(s.getAlbum());
            System.out.println(s.getYear());
            System.out.println();
        });
    }

    private static void sortList(List<Song> list) {
        Collections.sort(list, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                int songNameComparisonValue = o1.getSongName().compareToIgnoreCase(o2.getSongName());
                return (songNameComparisonValue == 0)
                        ? o1.getArtist().compareToIgnoreCase(o2.getArtist())
                        : songNameComparisonValue;
            }
        });
    }

//    public static void main(String[] args) {
//        testSetup();
//
//    }
}
