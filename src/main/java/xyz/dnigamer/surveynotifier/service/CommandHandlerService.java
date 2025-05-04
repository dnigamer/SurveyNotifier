package xyz.dnigamer.surveynotifier.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;
import xyz.dnigamer.surveynotifier.command.RegisterChannelCommand;
import xyz.dnigamer.surveynotifier.command.UnregisterChannelCommand;

@Service
public class CommandHandlerService {

    private final RegisterChannelCommand registerChannelCommand;
    private final UnregisterChannelCommand unregisterChannelCommand;

    public CommandHandlerService(RegisterChannelCommand registerChannelCommand,
                                 UnregisterChannelCommand unregisterChannelCommand) {
        this.registerChannelCommand = registerChannelCommand;
        this.unregisterChannelCommand = unregisterChannelCommand;
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        switch (commandName) {
            case "ping" -> event.reply("Pong!").queue();
            case "register" -> registerChannelCommand.execute(event);
            case "unregister" -> unregisterChannelCommand.execute(event);
            case "status" -> event.reply("Status command is not implemented yet!").setEphemeral(true).queue();
            case "database" -> event.reply("Database command is not implemented yet!").setEphemeral(true).queue();
            default -> event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }
}
