package xyz.dnigamer.surveynotifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.dnigamer.surveynotifier.entity.Survey;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    boolean existsBySurveyLink(String surveyLink);
    List<Survey> findAllBySurveyLinkNotIn(List<String> surveyLinks);

    @Query("SELECT SUM(CAST(s.value AS int)) FROM Survey s")
    Integer calculateTotalValue();

    @Query("SELECT SUM(CAST(s.duration AS int)) FROM Survey s")
    Integer calculateTotalDuration();

}
