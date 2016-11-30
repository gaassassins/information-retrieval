package bashimquotes.utils;

import bashimquotes.models.Quote;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {

    public static Quote readFile(File file) {
        StringBuilder stringJSON = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                stringJSON.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Quote quote = new Quote();
        JSONObject news_json = new JSONObject(stringJSON.toString());
        if (news_json.has("id") && news_json.has("date") && news_json.has("body") ) {
            quote.setId(news_json.getString("id"));
            quote.setDate(news_json.getString("date"));
            quote.setText(news_json.getString("body"));
            return quote;
        } else {
            return null;
        }
    }

}
