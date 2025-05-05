package xyz.dnigamer.surveynotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "configs")
public class Config {

    @Id
    private String guildId;

    @Column(name = "reload_interval")
    private long interval;
    private String cronExpression;

    public Config() {
    }

    public Config(String guildId, long interval, String cronExpression) {
        this.guildId = guildId;
        this.interval = interval;
        this.cronExpression = cronExpression;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Config config)) return false;
        return getInterval() == config.getInterval() && Objects.equals(getGuildId(), config.getGuildId()) && Objects.equals(getCronExpression(), config.getCronExpression());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGuildId(), getInterval(), getCronExpression());
    }

    @Override
    public String toString() {
        return "Config{" +
                "guildId='" + guildId + '\'' +
                ", interval=" + interval +
                ", cronExpression='" + cronExpression + '\'' +
                '}';
    }
}
