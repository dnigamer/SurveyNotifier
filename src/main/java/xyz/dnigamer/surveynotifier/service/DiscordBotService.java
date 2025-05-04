package xyz.dnigamer.surveynotifier.service;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class DiscordBotService extends ListenerAdapter {

    private final JDABuilder jdaBuilder;
    private final CommandHandlerService commandHandlerService;

    private final Logger logger = Logger.getLogger(DiscordBotService.class.getName());

    public DiscordBotService(JDABuilder jdaBuilder, CommandHandlerService commandHandlerService) {
        this.jdaBuilder = jdaBuilder;
        this.commandHandlerService = commandHandlerService;
    }

    @PostConstruct
    public void startBot() throws Exception {
        JDA jda = jdaBuilder.build();
        jda.addEventListener(this);

        // Register slash commands
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Replies with Pong and other useful information"),
                Commands.slash("register", "Register this channel to receive notifications"),
                Commands.slash("unregister", "Unregister this channel from receiving notifications"),
                Commands.slash("status", "Get the current status of the bot"),
                Commands.slash("database", "Get the current status of the database")
        ).queue();
    }

    // Example event listener
    public void onReady(@NotNull ReadyEvent event) {
        logger.info("Discord bot is ready!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandHandlerService.handleSlashCommand(event);
    }
}
