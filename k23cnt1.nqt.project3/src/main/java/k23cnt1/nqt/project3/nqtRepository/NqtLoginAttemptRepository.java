package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtLoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NqtLoginAttemptRepository extends JpaRepository<NqtLoginAttempt, Integer> {
    
    /**
     * Count failed attempts by identifier in the last N minutes
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIdentifier = :identifier " +
           "AND a.nqtSuccess = false AND a.nqtAttemptTime >= :since")
    long countFailedAttemptsByIdentifier(@Param("identifier") String identifier, 
                                        @Param("since") LocalDateTime since);
    
    /**
     * Count failed attempts by identifier and action type in the last N minutes
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIdentifier = :identifier " +
           "AND a.nqtActionType = :actionType AND a.nqtSuccess = false AND a.nqtAttemptTime >= :since")
    long countFailedAttemptsByIdentifierAndAction(@Param("identifier") String identifier,
                                                  @Param("actionType") String actionType,
                                                  @Param("since") LocalDateTime since);
    
    /**
     * Count failed attempts by IP address in the last N minutes
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIpAddress = :ipAddress " +
           "AND a.nqtSuccess = false AND a.nqtAttemptTime >= :since")
    long countFailedAttemptsByIpAddress(@Param("ipAddress") String ipAddress, 
                                       @Param("since") LocalDateTime since);
    
    /**
     * Get all failed attempts by identifier in the last N minutes
     */
    @Query("SELECT a FROM NqtLoginAttempt a WHERE a.nqtIdentifier = :identifier " +
           "AND a.nqtSuccess = false AND a.nqtAttemptTime >= :since ORDER BY a.nqtAttemptTime DESC")
    List<NqtLoginAttempt> findFailedAttemptsByIdentifier(@Param("identifier") String identifier, 
                                                         @Param("since") LocalDateTime since);
    
    /**
     * Get all failed attempts by identifier and action type in the last N minutes
     */
    @Query("SELECT a FROM NqtLoginAttempt a WHERE a.nqtIdentifier = :identifier " +
           "AND a.nqtActionType = :actionType AND a.nqtSuccess = false AND a.nqtAttemptTime >= :since ORDER BY a.nqtAttemptTime DESC")
    List<NqtLoginAttempt> findFailedAttemptsByIdentifierAndAction(@Param("identifier") String identifier,
                                                                   @Param("actionType") String actionType,
                                                                   @Param("since") LocalDateTime since);
    
    /**
     * Get all failed attempts by IP address in the last N minutes
     */
    @Query("SELECT a FROM NqtLoginAttempt a WHERE a.nqtIpAddress = :ipAddress " +
           "AND a.nqtSuccess = false AND a.nqtAttemptTime >= :since ORDER BY a.nqtAttemptTime DESC")
    List<NqtLoginAttempt> findFailedAttemptsByIpAddress(@Param("ipAddress") String ipAddress, 
                                                       @Param("since") LocalDateTime since);
    
    /**
     * Delete old attempts (older than specified days)
     */
    @Modifying
    @Query("DELETE FROM NqtLoginAttempt a WHERE a.nqtAttemptTime < :before")
    void deleteOldAttempts(@Param("before") LocalDateTime before);
    
    /**
     * Count total attempts by identifier in the last N minutes (for rate limiting)
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIdentifier = :identifier " +
           "AND a.nqtAttemptTime >= :since")
    long countTotalAttemptsByIdentifier(@Param("identifier") String identifier, 
                                       @Param("since") LocalDateTime since);
    
    /**
     * Count total attempts by IP address in the last N minutes (for rate limiting)
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIpAddress = :ipAddress " +
           "AND a.nqtAttemptTime >= :since")
    long countTotalAttemptsByIpAddress(@Param("ipAddress") String ipAddress, 
                                      @Param("since") LocalDateTime since);
    
    /**
     * Count total attempts by IP address and action type in the last N minutes
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIpAddress = :ipAddress " +
           "AND a.nqtActionType = :actionType AND a.nqtAttemptTime >= :since")
    long countTotalAttemptsByIpAddressAndAction(@Param("ipAddress") String ipAddress,
                                                @Param("actionType") String actionType,
                                                @Param("since") LocalDateTime since);
    
    /**
     * Count total attempts by identifier and action type in the last N minutes
     */
    @Query("SELECT COUNT(a) FROM NqtLoginAttempt a WHERE a.nqtIdentifier = :identifier " +
           "AND a.nqtActionType = :actionType AND a.nqtAttemptTime >= :since")
    long countTotalAttemptsByIdentifierAndAction(@Param("identifier") String identifier,
                                                @Param("actionType") String actionType,
                                                @Param("since") LocalDateTime since);
}

