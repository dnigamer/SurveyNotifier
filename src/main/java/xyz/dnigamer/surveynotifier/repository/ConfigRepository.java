package xyz.dnigamer.surveynotifier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.dnigamer.surveynotifier.entity.Config;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Long> {
    boolean existsByGuildId(String guildId);
    void deleteByGuildId(String guildId);
    Config findByGuildId(String guildId);
}
