package xyz.dnigamer.surveynotifier.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    private String channelId;
    private String channelName;
    private String guildId;

    public Channel() {
    }

    public Channel(String channelId, String channelName, String guildId) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.guildId = guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Channel channel)) return false;
        return Objects.equals(getChannelId(), channel.getChannelId()) && Objects.equals(getChannelName(), channel.getChannelName()) && Objects.equals(getGuildId(), channel.getGuildId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannelId(), getChannelName(), getGuildId());
    }

    @Override
    public String
    toString() {
        return "Channel{" +
                "channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", guildId='" + guildId + '\'' +
                '}';
    }
}
