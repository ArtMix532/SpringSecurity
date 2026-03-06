package tech.buildrun.springsecurity.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.shared.models.ProfiledUser;

import java.util.UUID;

public interface ProfiledUserRepository extends JpaRepository<ProfiledUser, UUID> {
}
