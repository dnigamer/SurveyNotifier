package xyz.dnigamer.surveynotifier.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SurveyParserService {

    public List<Map<String, String>> parseSurveys(String htmlResponse) {
        List<Map<String, String>> surveyList = new ArrayList<>();

        Document document = Jsoup.parse(htmlResponse);
        Elements surveyCards = document.select("div.card");

        // Iterate through each survey card and extract details
        for (Element card : surveyCards) {
            Map<String, String> surveyDetails = new HashMap<>();

            // Value and currency
            surveyDetails.put("value", card.select("strong.principle-color").text());
            surveyDetails.put("currency", card.select("p.principle-color").text());

            // Duration
            surveyDetails.put("duration", Objects.requireNonNull(card.select("div.survey-duration .d-inline").last()).text());

            // Title
            surveyDetails.put("closingDate", Objects.requireNonNull(card.select("div.survey-closing-date .d-inline").last()).text());

            String surveyLink = card.select("button.survey-start-button").attr("onclick")
                    .replace("window.open('", "")
                    .replace("')", "");
            surveyDetails.put("surveyLink", surveyLink);

            // Add to the list
            surveyList.add(surveyDetails);
        }

        // Returns as a list of maps
        return surveyList;
    }
}