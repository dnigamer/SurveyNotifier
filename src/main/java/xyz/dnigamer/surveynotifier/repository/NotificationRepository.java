package xyz.dnigamer.surveynotifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.dnigamer.surveynotifier.entity.Notification;
import xyz.dnigamer.surveynotifier.entity.Survey;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllBySurveyAndIsActiveTrue(Survey survey);
    Integer countAllByIsActiveTrue();
    Integer countAllByIsActiveFalse();

}
