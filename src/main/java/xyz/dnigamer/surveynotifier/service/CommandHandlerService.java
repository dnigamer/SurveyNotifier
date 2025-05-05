package xyz.dnigamer.surveynotifier.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Service;
import xyz.dnigamer.surveynotifier.command.ListCommand;
import xyz.dnigamer.surveynotifier.command.RegisterChannelCommand;
import xyz.dnigamer.surveynotifier.command.StatusCommand;
import xyz.dnigamer.surveynotifier.command.UnregisterChannelCommand;

@Service
public class CommandHandlerService {

    private final RegisterChannelCommand registerChannelCommand;
    private final UnregisterChannelCommand unregisterChannelCommand;
    private final StatusCommand statusCommand;
    private final ListCommand listCommand;

    public CommandHandlerService(RegisterChannelCommand registerChannelCommand,
                                 UnregisterChannelCommand unregisterChannelCommand,
                                    StatusCommand statusCommand,
                                    ListCommand listCommand) {
        this.registerChannelCommand = registerChannelCommand;
        this.unregisterChannelCommand = unregisterChannelCommand;
        this.statusCommand = statusCommand;
        this.listCommand = listCommand;
    }

    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        switch (commandName) {
            case "register" -> registerChannelCommand.execute(event);
            case "unregister" -> unregisterChannelCommand.execute(event);
            case "list" -> listCommand.execute(event);
            case "status" -> statusCommand.execute(event);
            case "database" -> event.reply("Database command is not implemented yet!").setEphemeral(true).queue();
            case "config" -> event.reply("Config command is not implemented yet!").setEphemeral(true).queue();
            case "survey-stats" ->
                    event.reply("Survey stats command is not implemented yet!").setEphemeral(true).queue();
            default -> event.reply("Unknown command!").setEphemeral(true).queue();
        }
    }
}
