package xyz.dnigamer.surveynotifier.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey", referencedColumnName = "id", nullable = false)
    private Survey survey;

    private String channelId;

    private String messageId;

    private Date sentAt;

    private boolean isActive = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Notification that)) return false;
        return isActive() == that.isActive() && Objects.equals(getId(), that.getId()) && Objects.equals(getSurvey(), that.getSurvey()) && Objects.equals(getChannelId(), that.getChannelId()) && Objects.equals(getMessageId(), that.getMessageId()) && Objects.equals(getSentAt(), that.getSentAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSurvey(), getChannelId(), getMessageId(), getSentAt(), isActive());
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", survey=" + survey +
                ", channelId='" + channelId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", sentAt=" + sentAt +
                ", isActive=" + isActive +
                '}';
    }
}