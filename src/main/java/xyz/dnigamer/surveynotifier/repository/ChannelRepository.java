package xyz.dnigamer.surveynotifier.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.dnigamer.surveynotifier.entity.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    boolean existsByChannelId(String channelId);
    void deleteByChannelId(String channelId);

    @Transactional
    default void deleteChannelById(String channelId) {
        deleteByChannelId(channelId);
    }

}
