package net.soluspay.cashq.utils;

public class TrackParser {

    private String track, pan, name, expDate;

    public TrackParser(String track) {
        this.track = track;
    }

    public String getTrack() {
        return track;
    }

    public String getPan() {
        final String[] pan = getTrack().split("\\^");
        return pan[0].substring(2);
    }

    public String getName() {
        final String[] name = getTrack().split("\\^");
        return name[1];
    }

    public String getExpDate() {
        final String[] expDate = getTrack().split("\\^");
        return expDate[2].substring(0,4);
    }

}
