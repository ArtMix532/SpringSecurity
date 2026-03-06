package tech.buildrun.springsecurity.email.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.email.models.VerificationToken;
import tech.buildrun.springsecurity.shared.models.User;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUser(User user);

}