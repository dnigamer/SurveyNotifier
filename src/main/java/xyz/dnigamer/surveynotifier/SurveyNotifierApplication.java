package xyz.dnigamer.surveynotifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SurveyNotifierApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyNotifierApplication.class, args);
    }

}
