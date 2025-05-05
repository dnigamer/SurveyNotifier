package xyz.dnigamer.surveynotifier.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;
import xyz.dnigamer.surveynotifier.entity.Channel;
import xyz.dnigamer.surveynotifier.repository.ChannelRepository;

import java.util.List;
import java.util.Objects;

@Component
public class ListCommand {

    private final ChannelRepository channelRepository;

    public ListCommand(ChannelRepository channelRepository) {
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

            // Fetch all registered channels from the database
            List<Channel> channels = channelRepository.findAllByGuildId(Objects.requireNonNull(event.getGuild()).getId());

            // Check if there are any registered channels
            if (channels.isEmpty()) {
                event.reply("No channels are currently registered.").setEphemeral(true).queue();
                return;
            }

            // Build the response message
            EmbedBuilder response = new EmbedBuilder()
                    .setTitle("Registered Channels")
                    .setDescription("Here are the channels currently registered to receive notifications:")
                    .setColor(0x00FF00);
            int index = 1;
            for (Channel channel : channels) {
                response.addField(index + ". Channel Name", "<#" + channel.getChannelId() + ">", false);
                response.addField("Channel ID", "``" + channel.getChannelId() + "``", false);
                index++;
            }

            event.replyEmbeds(response.build()).setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply("An error occurred while listing the channels.").setEphemeral(true).queue();
            throw new RuntimeException(e);
        }
    }

}
