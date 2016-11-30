package bashimquotes.search;

import bashimquotes.models.Quote;
import bashimquotes.models.ResponseList;
import bashimquotes.utils.Utils;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LuceneSearch {

    private final String indexDir = "index"; //index file
    private final String dataDir = "data"; //json

    public void createIndex() throws IOException { //запускает индексацию, вызывает индексер
        System.out.println("Начало индексации");
        Indexer indexer = new Indexer(indexDir); //передаем путь и где будет хранить файлы с индексами
        long startTime = System.currentTimeMillis();
        int numIndexed = indexer.createIndex(dataDir); //индексируем каждый файл из даты
        long endTime = System.currentTimeMillis();
        System.out.println(numIndexed + " заиндексировано, время: " + (endTime-startTime) + " мс");
        indexer.close();
    }

    public ResponseList<Quote> search(String searchQuery, String ... selections) throws IOException, ParseException {
        //searchQuery - ключ, который мы ищем; String ... selections - по каким полям ищем
        Searcher searcher = new Searcher(indexDir); //передем поисковику индексы
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery, selections); //запускаем поиск по ключу по деревьям признаков
        long endTime = System.currentTimeMillis();
        System.out.println(hits.totalHits + " документов с === "  + searchQuery +  " === найдено. За время : " + (endTime - startTime) + " мс");

        ArrayList<Quote> newsList = new ArrayList<>(); //создается пустой массив

        for(ScoreDoc scoreDoc : hits.scoreDocs) { //идем по выборке
            Document doc = searcher.getDocument(scoreDoc); //из этих документов получаем путь к файлу
            String filepath = doc.get("filepath"); // и считали этот путь

            Quote quote = Utils.readFile(new File(filepath)); //если цитата не пустая, добавляем в лист
            if (quote != null) {
                newsList.add(quote);
            }
            System.out.println("Файл: " + scoreDoc.score + " " + filepath);
        }
        return new ResponseList<>((endTime - startTime), hits.totalHits, newsList);
    }


    private static String search_request = "москва";
    public static void main(String[] args) {
        LuceneSearch tester;
        try {
            tester = new LuceneSearch();
            //tester.createIndex();
            tester.search(search_request, "body", "id", "date");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

}