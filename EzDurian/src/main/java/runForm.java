public class runForm {

    public static void main(String[] args) throws Exception {
        startApp start = new startApp();
        telegramBot bot = new telegramBot();

        try
        {
            start.SeleniumForm();
            bot.sendToTelegram("Form triggered");
        }
        catch (Exception e) {

            System.out.println(e);
            System.exit(0);
        }
    }

}
