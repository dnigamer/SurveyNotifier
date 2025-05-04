package xyz.dnigamer.surveynotifier.config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JDAConfig {

    @Value("${DISCORD_TOKEN}")
    private String discordToken;

    @Bean
    public JDABuilder jdaBuilder() {
        return JDABuilder.createDefault(discordToken)
                .setActivity(Activity.playing("Survey Notifications"));
    }
}
