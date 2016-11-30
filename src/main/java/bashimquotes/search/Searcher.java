package bashimquotes.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Searcher {

    private static final int MAX_SEARCH = 200; //максимальное количество результатов в поиске

    private IndexSearcher indexSearcher; // создается поле

    public Searcher(String indexDirectoryPath) throws IOException{
        // пришел путь к индекс директории
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath()); //получаем/открываем директорию
        indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
        //создается индекс серчер и передается директория (чтобы получить индесные файлы из директории)
    }

    protected TopDocs search(String searchQuery, String ... selections) throws IOException, ParseException {
        //этот метод возвращает объект этого класса, выборка из документов, которую мы найдем
        ArrayList<QueryParser> queryParsers = new ArrayList<>(); //пустой список запросов

        for (String selection : selections) { //создаем запросы
            queryParsers.add(new QueryParser(selection, new SynonymAnalyzer())); //добавляем в пустой лист запрос,
            //основанный на параметре и синониме
        }

        BooleanQuery.Builder result_query = new BooleanQuery.Builder(); //результирующая выборка
        for (QueryParser query : queryParsers) //идем по запросам
            result_query.add(query.parse(searchQuery), BooleanClause.Occur.SHOULD);

        return indexSearcher.search(result_query.build(), MAX_SEARCH); //выдаем выборку и ограничиваем лимитом
    }

    protected Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }


}