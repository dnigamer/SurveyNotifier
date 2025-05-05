package xyz.dnigamer.surveynotifier.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.repository.ChannelRepository;

import java.util.Objects;

@Component
public class UnregisterChannelCommand {
    private final ChannelRepository channelRepository;

    public UnregisterChannelCommand(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void execute(SlashCommandInteractionEvent event) {
        try {
            if (!event.getChannelType().isGuild()) {
                event.reply("This command can only be used in a server channel!").setEphemeral(true).queue();
                return;
            }
            if (!Objects.requireNonNull(event.getMember()).hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
                return;
            }

            // Get the channel ID from the event if provided or use the current channel
            String channelId = event.getChannel().getId();
            if (event.getOption("channel") != null) {
                channelId = Objects.requireNonNull(event.getOption("channel")).getAsString();
            }

            // Check if the channel exists
            if (!channelRepository.existsByChannelId(channelId)) {
                event.reply("This channel is not registered!").setEphemeral(true).queue();
                return;
            }

            // Delete the channel from the database
            channelRepository.deleteChannelById(channelId);

            event.reply("Channel unregistered successfully!").setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("An error occurred while unregistering the channel.").setEphemeral(true).queue();
            throw new RuntimeException(e);
        }
    }
}
