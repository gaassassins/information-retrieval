package bashimquotes;

import java.io.*;
import java.util.*;


import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class ZipfLaw {

    private static TreeMap<String, Long> multiset = new TreeMap<>();
    private static ArrayList<String> fields = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        Directory indexDirectory = FSDirectory.open(new File("index").toPath());
        IndexReader reader = DirectoryReader.open(indexDirectory); //поток, который индексирует все

        MultiFields.getFields(reader).forEach(fields::add);
        TermsEnum termEnum = MultiFields.getTerms(reader, "body").iterator();  //разбивается текст на слова
        BytesRef bytesRef;

        while ((bytesRef = termEnum.next()) != null) { //и пока следующее слово не пустое
            String term = bytesRef.utf8ToString(); //получаем слово
            if (!fields.contains(term)) {
                multiset.put(term, termEnum.totalTermFreq()); //слово и общее кол-во вхождений его в date
            }
        }

        List<Map.Entry<String, Long>> l = new ArrayList<>(multiset.entrySet()); //создается массив из слова и кол-ва
        Collections.sort(l, (o1, o2) ->  -1 * o1.getValue().compareTo(o2.getValue()));  // сортируем по кол-ву

        Writer writer = new OutputStreamWriter(new FileOutputStream(new File("Result.csv")), "utf-8");
        String first = "Rank" + "," + "Количество" + "," + "Последовательность" + "\n";
        writer.write(first);
        int rank = 1; //ранг

        for(Map.Entry<String, Long> e : l ){
            String res = rank + "," + e.getValue()  + "," + e.getKey()+ "\n";
            writer.write(res);
            rank++;
        }
        //результат записывается в csv-файл

        writer.flush();
        writer.close();
    }
}