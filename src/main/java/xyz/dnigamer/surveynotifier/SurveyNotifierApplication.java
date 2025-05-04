package xyz.dnigamer.surveynotifier;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SurveyNotifierApplication {

    public static final Long START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DISCORD_TOKEN", dotenv.get("DISCORD_TOKEN"));
        SpringApplication.run(SurveyNotifierApplication.class, args);
    }

}
