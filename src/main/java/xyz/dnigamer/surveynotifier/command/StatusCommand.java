package xyz.dnigamer.surveynotifier.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.service.DatabaseHealthService;
import xyz.dnigamer.surveynotifier.service.RedisHealthService;

import java.awt.*;
import java.time.Instant;

@Component
public class StatusCommand {

    private final DatabaseHealthService databaseHealthService;
    private final RedisHealthService redisHealthService;

    public StatusCommand(DatabaseHealthService databaseHealthService, RedisHealthService redisHealthService) {
        this.databaseHealthService = databaseHealthService;
        this.redisHealthService = redisHealthService;
    }

    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return String.format("%d days, %d hours, %d minutes, %d seconds",
                days, hours % 24, minutes % 60, seconds % 60);
    }

    public void execute(SlashCommandInteractionEvent event) {
        String uptime = formatDuration(System.currentTimeMillis() - xyz.dnigamer.surveynotifier.SurveyNotifierApplication.START_TIME);

        long databasePing = databaseHealthService.getDatabasePing();
        long redisPing = redisHealthService.getRedisPing();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Bot Status")
                .addField("Gateway Ping", "```" + event.getJDA().getGatewayPing() + "ms     ```", true)
                .addField("REST Ping", "```" + event.getJDA().getRestPing().complete() + "ms     ```", true)
                .addField("Database Ping", "```" + (databasePing >= 0 ? databasePing + "ms" : "Unreachable") + "     ```", true)
                .addField("Redis", "```" + (redisPing >= 0 ? redisPing + "ms" : "Unreachable") + "     ```", true)
                .addField("Application Version", "```v1.0.0     ```", true)
                .addField("JRE Version", "```" + System.getProperty("java.version") + "     ```", true)
                .addField("JVM Version", "```" + System.getProperty("java.vm.version") + "     ```", true)
                .addField("JVM Vendor", "```" + System.getProperty("java.vm.vendor") + "     ```", true)
                .addField("Uptime", "```" + uptime + "     ```", false)
                .setFooter("Survey Notifier Bot")
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN);

        try {
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("An error occurred while sending the embed!").setEphemeral(true).queue();
            throw new RuntimeException("An error occurred while sending the embed!", e);
        }
    }
}
