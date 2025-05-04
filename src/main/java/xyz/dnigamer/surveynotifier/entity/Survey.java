package xyz.dnigamer.surveynotifier.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;
    private String currency;
    private String duration;
    private String closingDate;
    private String surveyLink;

    public Survey() {
    }

    public Survey(Long id, String value, String currency, String duration, String closingDate, String surveyLink) {
        this.id = id;
        this.value = value;
        this.currency = currency;
        this.duration = duration;
        this.closingDate = closingDate;
        this.surveyLink = surveyLink;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public String getSurveyLink() {
        return surveyLink;
    }

    public void setSurveyLink(String surveyLink) {
        this.surveyLink = surveyLink;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Survey survey)) return false;
        return Objects.equals(getId(), survey.getId()) && Objects.equals(getValue(), survey.getValue()) && Objects.equals(getCurrency(), survey.getCurrency()) && Objects.equals(getDuration(), survey.getDuration()) && Objects.equals(getClosingDate(), survey.getClosingDate()) && Objects.equals(getSurveyLink(), survey.getSurveyLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue(), getCurrency(), getDuration(), getClosingDate(), getSurveyLink());
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", currency='" + currency + '\'' +
                ", duration='" + duration + '\'' +
                ", closingDate='" + closingDate + '\'' +
                ", surveyLink='" + surveyLink + '\'' +
                '}';
    }
}