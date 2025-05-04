package xyz.dnigamer.surveynotifier.scheduler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.entity.Survey;
import xyz.dnigamer.surveynotifier.repository.SurveyRepository;
import xyz.dnigamer.surveynotifier.service.AuthenticationService;
import xyz.dnigamer.surveynotifier.service.SurveyParserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SurveyPollingScheduler {

    @Autowired
    private SurveyParserService surveyParserService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private final Logger logger = LoggerFactory.getLogger(SurveyPollingScheduler.class);

    // Run every 5 minutes
    @Scheduled(cron = "0 * * * * *")
    public void fetchAndSaveSurveys() {
        try {
            String token = authenticationService.getAuthToken();

            // Fetch the HTML from the website
            Document document = Jsoup.connect("https://app.synopanel.com/surveys/list")
                    .cookie("SFSESSID", token)
                    .get();

            // Check if the response is valid
            String htmlResponse = document.html();

            // Parse the HTML to extract survey data
            List<Map<String, String>> surveyDataList = surveyParserService.parseSurveys(htmlResponse);
            if (surveyDataList.isEmpty()) {
                logger.debug("No surveys found.");
                return;
            }

            List<Map<String, String>> newSurveys = new ArrayList<>();

            // Save each survey to the database
            for (Map<String, String> surveyData : surveyDataList) {
                String surveyLink = surveyData.get("surveyLink");
                if (surveyRepository.existsBySurveyLink(surveyLink)) {
                    continue;
                }
                Survey survey = new Survey();
                survey.setValue(surveyData.get("value"));
                survey.setCurrency(surveyData.get("currency"));
                survey.setDuration(surveyData.get("duration"));
                survey.setClosingDate(surveyData.get("closingDate"));
                survey.setSurveyLink(surveyLink);
                surveyRepository.save(survey);
                newSurveys.add(surveyData);
            }

            // Notify the user about new surveys
            if (!newSurveys.isEmpty()) {
                // TODO: Implement notification logic
                logger.debug("New surveys found: {}", newSurveys);
            } else {
                logger.debug("No new surveys found.");
            }

            // Log the number of surveys saved
            logger.debug("Total surveys saved: {}", surveyRepository.count());
        } catch (Exception e) {
            logger.error("Error fetching surveys: {}", e.getMessage());
            // TODO: Implement error handling and notification
        }
    }
}
