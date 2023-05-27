import com.sun.jdi.connect.Transport;
import java.net.*;
import java.io.*;
import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import javax.json.*;
import javax.mail.*;
import javax.mail.Authenticator;
import javax.mail.internet.*;
import javax.activation.*;
import javax.sql.DataSource;

public class SendEmail {
    private static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    private static final String UNITS = "&units=metric";

    public static void main(String [] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your email: ");
        String from = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

        System.out.println("Enter the recipient's email: ");
        String to = scanner.nextLine();

        System.out.println("Enter the city: ");
        String city = scanner.nextLine();

        System.out.println("Enter the city: ");
        String groupNumber = scanner.nextLine();

        System.out.println("Enter the city: ");
        String fullName = scanner.nextLine();

        // get weather data
        String urlString = BASE_URL + city + "&appid=" + API_KEY + UNITS;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        JsonObject json = Json.createReader(new StringReader(result.toString())).readObject();
        JsonObject main = json.getJsonObject("main");
        double temperature = main.getJsonNumber("temp").doubleValue();
        int pressure = main.getInt("pressure");
        int humidity = main.getInt("humidity");
        JsonObject wind = json.getJsonObject("wind");
        double speed = wind.getJsonNumber("speed").doubleValue();
        JsonObject clouds = json.getJsonObject("clouds");
        int cloudiness = clouds.getInt("all");

        String weatherDescription = "Temperature: " + temperature + "°C\nPressure: " + pressure + " hPa\nHumidity: "
                + humidity + "%\nWind speed: " + speed + " m/s\nCloudiness: " + cloudiness + "%";

        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Get the default Session object.
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication passwordAuthentication = new PasswordAuthentication(from, password);
                return passwordAuthentication;
            }
        });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(groupNumber + ", " + fullName);

            // Create the message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            messageBodyPart.setText("Date: " + new Date() + "\nLocation: " + city + "\nWeather: " + weatherDescription);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Attach image
            messageBodyPart = new MimeBodyPart();
            String filename = "E:/my projects/AhmedLaps/cat.jpg";
            DataSource source = (DataSource) new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler((javax.activation.DataSource) source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);


            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Message sent successfully...");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
