package bashimquotes.models;


import java.util.ArrayList;

public class ResponseList<T extends Quote> {
    private long time;
    private long totalHits;
    private ArrayList<T> quotes;

    public ResponseList() {
    }

    public ResponseList(long time, long totalHits, ArrayList<T> quotes) {
        this.time = time;
        this.totalHits = totalHits;
        this.quotes = quotes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<T> getQuotes() {
        return quotes;
    }

    public void setQuotes(ArrayList<T> quotes) {
        this.quotes = quotes;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }
}
