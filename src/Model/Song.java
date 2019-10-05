/*
 *  Mohammed Alnadi (ma1322)
 *  Salman Hashmi (sah285)
 *  
 *  Note: We are using the GSON library in this project. GSON version 2.8.5
 *  Download link: https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.5 
 */

package Model;

public class Song {
    private String songName;
    private String artist;
    private String album;
    private int year;

    public Song(String songName, String artist) {
        this.songName = songName;
        this.artist = artist;
    }

    public Song(String songName, String artist, int year) {
        this(songName, artist);
        this.year = year;

    }

    public Song(String songName, String artist, String album) {
        Song s = new Song(songName, artist);
        this.album = album;
    }

    public Song(String songName, String artist, String album, int year) {
        this(songName, artist);
        this.album = album;
        this.year = year;
    }

    public String getSongName() {
        return this.songName;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public int getYear() {
        return this.year;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setYear(int year) {
        this.year = year;
    }


}
