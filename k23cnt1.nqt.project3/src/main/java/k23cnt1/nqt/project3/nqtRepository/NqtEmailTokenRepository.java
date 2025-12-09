package k23cnt1.nqt.project3.nqtRepository;

import k23cnt1.nqt.project3.nqtEntity.NqtEmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NqtEmailTokenRepository extends JpaRepository<NqtEmailToken, Integer> {
    Optional<NqtEmailToken> findByNqtTokenAndNqtType(String token, String type);
    
    Optional<NqtEmailToken> findByNqtUserIdAndNqtTypeAndNqtUsed(Integer userId, String type, Boolean used);
    
    @Modifying
    @Query("DELETE FROM NqtEmailToken n WHERE n.nqtExpiresAt < ?1")
    void deleteExpiredTokens(LocalDateTime now);
}

