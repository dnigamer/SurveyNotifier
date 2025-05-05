package xyz.dnigamer.surveynotifier.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;
import xyz.dnigamer.surveynotifier.command.*;

@Service
public class CommandHandlerService {

    private final RegisterChannelCommand registerChannelCommand;
    private final UnregisterChannelCommand unregisterChannelCommand;
    private final StatusCommand statusCommand;
    private final ListCommand listCommand;
    private final DatabaseCommand databaseCommand;
    private final ConfigCommand configCommand;

    public CommandHandlerService(RegisterChannelCommand registerChannelCommand,
                                 UnregisterChannelCommand unregisterChannelCommand,
                                    StatusCommand statusCommand,
                                    ListCommand listCommand,
                                    DatabaseCommand databaseCommand,
                                    ConfigCommand configCommand) {
        this.registerChannelCommand = registerChannelCommand;
        this.unregisterChannelCommand = unregisterChannelCommand;
        this.statusCommand = statusCommand;
        this.listCommand = listCommand;
        this.databaseCommand = databaseCommand;
        this.configCommand = configCommand;
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        switch (commandName) {
            case "register" -> registerChannelCommand.execute(event);
            case "unregister" -> unregisterChannelCommand.execute(event);
            case "list" -> listCommand.execute(event);
            case "status" -> statusCommand.execute(event);
            case "database" -> databaseCommand.execute(event);
            case "config" -> configCommand.execute(event);
            default -> event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }
}
