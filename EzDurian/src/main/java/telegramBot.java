import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class telegramBot {

    public void sendToTelegram(String text) {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

        //Add Telegram token (given Token is fake)
        String apiToken = "1274132052:AAG-pR735h24p2HQkWvXXYiroERsAo9j2Gg";

        //Add chatId (given chatId is fake)
        String chatId = "-469058163";
        //String text = "hello I spawned";

        urlString = String.format(urlString, apiToken, chatId, text);

        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            InputStream is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

}
