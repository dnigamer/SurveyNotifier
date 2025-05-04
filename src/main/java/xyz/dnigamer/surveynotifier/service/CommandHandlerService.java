package xyz.dnigamer.surveynotifier.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;

@Service
public class CommandHandlerService {

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        switch (commandName) {
            case "ping" -> event.reply("Pong!").queue();
            case "register" -> event.reply("Register command is not implemented yet!").setEphemeral(true).queue();
            case "unregister" -> event.reply("Unregister command is not implemented yet!").setEphemeral(true).queue();
            case "status" -> event.reply("Status command is not implemented yet!").setEphemeral(true).queue();
            case "database" -> event.reply("Database command is not implemented yet!").setEphemeral(true).queue();
            default -> event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }
}
