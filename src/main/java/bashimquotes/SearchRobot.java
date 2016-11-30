package bashimquotes;

import bashimquotes.models.Quote;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.*;
import java.util.*;

public class SearchRobot {

    private List<Quote> quotes = new ArrayList<>();

    private void parseQuotes(String site, Integer ID) throws IOException {
        Document bash = Jsoup.connect(site + "/index/" + ID).timeout(1000000).get(); //парсинг html, получаем страницу
        bash.select(".quote").forEach((elem) -> { //создается класс документ
            String id = get(elem, "a[class=id]");
            String date = get(elem, "span[class=date]");
            String text = get(elem, "div[class=text]");

            if (text != null) {
                quotes.add(new Quote(id, date, text));
            }
        });
    }

    public void saveQuotes() throws IOException { //сохранение распарсенной информации
        for (Quote quote : quotes) {
            JSONObject jsonQuote = new JSONObject();
            jsonQuote.put("id", quote.getId());
            jsonQuote.put("date", quote.getDate());
            jsonQuote.put("body", quote.getText());

            try (Writer output = new BufferedWriter(new PrintWriter(new File("data/id" + quote.getId() + ".json")))) {
                output.write(jsonQuote.toString()); //открывается поток и записывается в файл
                output.flush(); //маленький буфер сразу выкатывается
            }
        }
    }

    private String get(Element element, String cssQuery) {
        final Element result = element.select(cssQuery).first();
        if (result != null) {
            return result.html()
                    .replaceAll("(<br>)\n+(\\1)*", "\n")
                    .replaceAll("<br>", "");
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        SearchRobot docs = new SearchRobot();
        for (int ID = 1100; ID < 1200; ID++) {
            docs.parseQuotes("https://bash.im/", ID); //site + ID of page
        }
        docs.saveQuotes(); // save JSONs
    }
}
