import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class telegramBot {

    public void sendToTelegram(String text) throws Exception {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=HTML";

        //Add Telegram token (given Token is fake)
        String apiToken = "1274132052:AAG-pR735h24p2HQkWvXXYiroERsAo9j2Gg";

        //Add chatId (given chatId is fake)
        String chatId = "-1001233026149";
        //String text = "hello I spawned";
        
        
        urlString = String.format(urlString, apiToken, chatId, text);

        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            InputStream is = new BufferedInputStream(conn.getInputStream());
        } catch (IOException e) {
            
            System.out.println("Errors please check the server");
            
            e.printStackTrace();
            //sendToTelegramPre("Errors : "+ e.toString());
        }
    }
    
    public void sendToTelegramPre(String text) throws Exception {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=HTML";

        //Add Telegram token (given Token is fake)
        String apiToken = "1274132052:AAG-pR735h24p2HQkWvXXYiroERsAo9j2Gg";

        //Add chatId (given chatId is fake)
        String chatId = "-481662745";
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
