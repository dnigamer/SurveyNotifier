package xyz.dnigamer.surveynotifier.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.entity.Channel;
import xyz.dnigamer.surveynotifier.repository.ChannelRepository;

import java.util.Objects;

@Component
public class RegisterChannelCommand {

    private final ChannelRepository channelRepository;

    public RegisterChannelCommand(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void execute(SlashCommandInteractionEvent event) {
        try {
            if (!event.getChannelType().isGuild()) {
                event.reply("This command can only be used in a server channel!").setEphemeral(true).queue();
                return;
            }

            // Get the channel ID and name from the event if provided or use the current channel
            String channelId = event.getChannel().getId();
            String channelName = event.getChannel().getName();
            if (event.getOption("channel") != null) {
                channelId = Objects.requireNonNull(event.getOption("channel")).getAsString();
                channelName = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getTextChannelById(channelId)).getName();
            }
            String guildId = Objects.requireNonNull(event.getGuild()).getId();

            // Check if the channel already exists
            if (channelRepository.existsByChannelId(channelId)) {
                event.reply("This channel is already registered!").setEphemeral(true).queue();
                return;
            }

            // Save the channel to the database
            Channel channel = new Channel(channelId, channelName, guildId);
            channelRepository.save(channel);

            event.reply("Channel registered successfully!").setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("An error occurred while registering the channel.").setEphemeral(true).queue();
            throw new RuntimeException(e);
        }
    }
}