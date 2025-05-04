package xyz.dnigamer.surveynotifier.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.repository.ChannelRepository;

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

            String channelId = event.getChannel().getId();

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
