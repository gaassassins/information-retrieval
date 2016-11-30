package bashimquotes.search;

import bashimquotes.models.Quote;
import bashimquotes.utils.Utils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Indexer {

    private IndexWriter writer; //поток, который индексирует файлики

    public Indexer(String indexDirectoryPath) throws IOException{ //конструктор-класс
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        //путь к папке в которой индексированные
        //файлы, открывается директория

        IndexWriterConfig indexConfig = new IndexWriterConfig(new SynonymAnalyzer());
        //создается индекс с синоним.анализатором

        indexConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE); //даем права на создания
        writer = new IndexWriter(indexDirectory,  indexConfig);
        //создаем поток, передаем директорию и конфиг (как и куда записывать)
    }

    private Document getDocument(File file) throws IOException{
        Document document = new Document(); //создаем новый документ

        Quote quote = Utils.readFile(file); //вставляем цитаточку, смотрим, что внутри

        //создаем структуру для индекса в виде документа
        Field contentField = new TextField("content", new FileReader(file)); //все содержимое внутри документа
        Field idField = new TextField("id", quote.getId(), Field.Store.YES);
        Field dateField = new TextField("date", quote.getDate(), Field.Store.YES);
        Field textField = new TextField("body", quote.getText(), Field.Store.YES); //тело цитаты
        Field fileNameField = new TextField("filename", file.getName(), Field.Store.YES);
        Field filePathField = new TextField("filepath", file.getCanonicalPath(), Field.Store.YES);

        document.add(contentField);
        document.add(idField);
        document.add(dateField);
        document.add(textField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    protected int createIndex(String dataDirPath) throws IOException{
        File[] files = new File(dataDirPath).listFiles(); //заходим в data и берем список всех джейсонов файлов

        for (File file : files) {
            if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead()){
                indexFile(file); //проверка на доступность файла
            }
        }
        return writer.numDocs(); //можно взять из потока кол-во, которое мы проиндексировали
    }


    private void indexFile(File file) throws IOException{
        Document document = getDocument(file); //получаем документ, который прочли
        writer.addDocument(document); //и записываем его в поток
    }

    protected void close() throws IOException{
        writer.close();
    }
}