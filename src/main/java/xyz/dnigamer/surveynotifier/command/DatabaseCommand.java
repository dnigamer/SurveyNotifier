package xyz.dnigamer.surveynotifier.command;

import com.sun.nio.sctp.Notification;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.repository.ChannelRepository;
import xyz.dnigamer.surveynotifier.repository.NotificationRepository;
import xyz.dnigamer.surveynotifier.repository.SurveyRepository;

@Component
public class DatabaseCommand {

    private final NotificationRepository notificationRepository;
    private final SurveyRepository surveyRepository;
    private final ChannelRepository channelRepository;

    public DatabaseCommand(NotificationRepository notificationRepository,
                           SurveyRepository surveyRepository,
                           ChannelRepository channelRepository) {
        this.notificationRepository = notificationRepository;
        this.surveyRepository = surveyRepository;
        this.channelRepository = channelRepository;
    }

    public void execute(SlashCommandInteractionEvent event) {
        try {
            long notificationCount = notificationRepository.count();
            long surveyCount = surveyRepository.count();
            long channelCount = channelRepository.count();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Database Status")
                    .addField("Total Notifications", "```" + String.valueOf(notificationCount) + "```", true)
                    .addField("Active Notifications", "```" + String.valueOf(notificationRepository.countAllByIsActiveTrue()) + "```", true)
                    .addField("Inactive Notifications", "```" + String.valueOf(notificationRepository.countAllByIsActiveFalse()) + "```", true)
                    .addBlankField(false)
                    .addField("Total Surveys", "```" + String.valueOf(surveyCount) + "```", true)
                    .addField("Total Survey Remuneration", "```" + String.valueOf(surveyRepository.calculateTotalValue()) + " EUR```", true)
                    .addField("Total Survey Time Estimated", "```" + String.valueOf(surveyRepository.calculateTotalDuration()) + " minutes```", true)
                    .addBlankField(false)
                    .addField("Total Channels", "```" + String.valueOf(channelCount) + "```", true)
                    .setColor(0x00FF00);

            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("An error occurred while fetching the database status.").setEphemeral(true).queue();
            throw new RuntimeException(e);
        }
    }

}
