package bashimquotes;

import bashimquotes.search.LuceneSearch;

import java.io.IOException;

public class Server {

    public static void main(String[] args) throws IOException {
        LuceneSearch tester = new LuceneSearch();
        tester.createIndex();
    }
}