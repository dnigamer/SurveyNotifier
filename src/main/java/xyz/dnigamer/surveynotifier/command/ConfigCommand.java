package xyz.dnigamer.surveynotifier.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.entity.Config;
import xyz.dnigamer.surveynotifier.repository.ConfigRepository;
import xyz.dnigamer.surveynotifier.scheduler.SurveyPollingScheduler;

import java.util.Objects;

@Component
public class ConfigCommand {

    private final SurveyPollingScheduler surveyPollingScheduler;
    private final ConfigRepository configRepository;

    public ConfigCommand(SurveyPollingScheduler surveyPollingScheduler, ConfigRepository configRepository) {
        this.surveyPollingScheduler = surveyPollingScheduler;
        this.configRepository = configRepository;
    }

    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getChannelType().isGuild()) {
            event.reply("This command can only be used in a server channel!").setEphemeral(true).queue();
            return;
        }
        if (!Objects.requireNonNull(event.getMember()).hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
            event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            return;
        }

        // Get the interval option from the event
        if (event.getOption("interval") != null) {
            long interval = Objects.requireNonNull(event.getOption("interval")).getAsLong();
            if (interval < 1) {
                event.reply("Interval must be greater than 0!").setEphemeral(true).queue();
                return;
            }

            // Calculate the cron expression based on the interval
            String cronExpression = interval < 60 ? "*/" + interval + " * * * * *" : "0 */" + (interval / 60) + " * * * *";

            // Update the scheduler with the new interval
            surveyPollingScheduler.updateScheduler(cronExpression);

            // Save the new interval to the database
            Config config = new Config();
            config.setGuildId(Objects.requireNonNull(event.getGuild()).getId());
            config.setInterval(interval);
            config.setCronExpression(cronExpression);

            // Check if the config already exists
            if (configRepository.existsByGuildId(config.getGuildId())) {
                Config existingConfig = configRepository.findByGuildId(config.getGuildId());
                existingConfig.setInterval(interval);
                existingConfig.setCronExpression(cronExpression);
                configRepository.save(existingConfig);
            } else {
                configRepository.save(config);
            }

            // Send a confirmation embed message
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Configuration Updated");
            embedBuilder.setDescription("The survey polling interval has been updated.");
            embedBuilder.addField("New Interval", interval + " seconds", false);
            embedBuilder.addField("Cron Expression", cronExpression, false);
            embedBuilder.setColor(0x00FF00);
            embedBuilder.setFooter("Survey Notifier Bot", event.getJDA().getSelfUser().getAvatarUrl());
            embedBuilder.setTimestamp(event.getTimeCreated());
            
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        } else {
            // If no interval is provided, show the current configuration
            Config config = configRepository.findByGuildId(Objects.requireNonNull(event.getGuild()).getId());
            if (config != null) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Current Configuration");
                embedBuilder.addField("Interval", config.getInterval() + " seconds", false);
                embedBuilder.addField("Cron Expression", config.getCronExpression(), false);
                embedBuilder.setColor(0x00FF00);
                embedBuilder.setFooter("Survey Notifier Bot", event.getJDA().getSelfUser().getAvatarUrl());
                embedBuilder.setTimestamp(event.getTimeCreated());

                event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
            } else {
                event.reply("No configuration found for this server.").setEphemeral(true).queue();
            }
        }
    }
}
