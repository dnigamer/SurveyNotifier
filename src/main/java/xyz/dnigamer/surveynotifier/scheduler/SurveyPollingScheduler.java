package xyz.dnigamer.surveynotifier.scheduler;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.entity.Channel;
import xyz.dnigamer.surveynotifier.entity.Config;
import xyz.dnigamer.surveynotifier.entity.Notification;
import xyz.dnigamer.surveynotifier.entity.Survey;
import xyz.dnigamer.surveynotifier.repository.ChannelRepository;
import xyz.dnigamer.surveynotifier.repository.ConfigRepository;
import xyz.dnigamer.surveynotifier.repository.NotificationRepository;
import xyz.dnigamer.surveynotifier.repository.SurveyRepository;
import xyz.dnigamer.surveynotifier.service.AuthenticationService;
import xyz.dnigamer.surveynotifier.service.SurveyParserService;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class SurveyPollingScheduler {

    @Value("${scheduler.default.cron:0 0/5 * * * *}") // Default cron expression (every 5 minutes)
    private String defaultCronExpression;

    private final ConfigRepository configRepository;

    public SurveyPollingScheduler(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @PostConstruct
    public void initializeScheduler() {
        List<Config> configs = configRepository.findAll();
        for (Config config : configs) {
            String cronExpression = config.getCronExpression();
            if (cronExpression == null || cronExpression.isEmpty()) {
                cronExpression = defaultCronExpression;
            }
            startScheduler(cronExpression);
        }
    }

    public void updateScheduler(String newCronExpression) {
        stopScheduler();
        startScheduler(newCronExpression);
    }

    @Autowired
    private TaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledTask;

    public void startScheduler(String cronExpression) {
        stopScheduler();
        logger.debug("Starting the scheduler with cron expression: {}", cronExpression);
        scheduledTask = taskScheduler.schedule(
                this::fetchAndSaveSurveys,
                new CronTrigger(cronExpression)
        );
    }

    public void stopScheduler() {
        if (scheduledTask != null) {
            logger.debug("Stopping the scheduler...");
            scheduledTask.cancel(true);
        }
    }

    @Autowired
    private SurveyParserService surveyParserService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JDABuilder jdaBuilder;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private final Logger logger = LoggerFactory.getLogger(SurveyPollingScheduler.class);

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
                List<Survey> surveys = surveyRepository.findAll();
                deleteExistingNotifications(surveys);
                return;
            }

            List<Survey> newSurveys = new ArrayList<>();

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
                newSurveys.add(survey);
            }

            // Check if any survey disappeared from the list on the website, compared to the database, and if so, delete from discord
            List<Survey> surveysToDelete = surveyRepository.findAllBySurveyLinkNotIn(surveyDataList.stream()
                    .map(data -> data.get("surveyLink"))
                    .toList());
            deleteExistingNotifications(surveysToDelete);

            // Notify the user about new surveys
            if (!newSurveys.isEmpty()) {
                sendNotificationToChannels(newSurveys);
            }
        } catch (Exception e) {
            logger.error("Error fetching surveys: {}", e.getMessage());
            // TODO: Implement error handling and notification
        }
    }

    private void deleteExistingNotifications(List<Survey> surveys) throws InterruptedException {
        for (Survey survey : surveys) {
            List<Notification> notifications = notificationRepository.findAllBySurveyAndIsActiveTrue(survey);
            for (Notification notification : notifications) {
                TextChannel textChannel = jdaBuilder.build().awaitReady().getTextChannelById(notification.getChannelId());
                if (textChannel != null) {
                    textChannel.deleteMessageById(notification.getMessageId()).queue();
                    notification.setActive(false);
                    notificationRepository.save(notification);
                }
            }
        }
    }

    private void sendNotificationToChannels(List<Survey> newSurveys) throws InterruptedException {
        List<Channel> channels = channelRepository.findAll();

        for (Channel channel : channels) {
            TextChannel textChannel = jdaBuilder.build().awaitReady().getTextChannelById(channel.getChannelId());
            if (textChannel != null) {
                for (Survey survey : newSurveys) {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("New Survey Available!")
                            .setColor(Color.BLUE)
                            .setDescription("A new survey is available for you to take!")
                            .setFooter("Survey Notifier Bot")
                            .setTimestamp(java.time.Instant.now());

                    embed.addField(
                            survey.getValue() + " " + survey.getCurrency(),
                            "Duration: " + survey.getDuration() + "\n[Take Survey](" + survey.getSurveyLink() + ")",
                            false
                    );

                    Notification notification = new Notification();
                    notification.setSurvey(survey);
                    notification.setChannelId(textChannel.getId());
                    notification.setMessageId(textChannel.sendMessageEmbeds(embed.build()).complete().getId());
                    notification.setSentAt(new java.util.Date());

                    notificationRepository.save(notification);
                }
            } else {
                logger.warn("Channel not found or bot lacks permissions to send: {}", channel.getChannelId());
            }
        }
    }
}
