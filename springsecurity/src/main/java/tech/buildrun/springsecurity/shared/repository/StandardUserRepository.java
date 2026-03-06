package tech.buildrun.springsecurity.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.buildrun.springsecurity.shared.models.StandardUser;

import java.util.UUID;

/**
 * Repositório específico para StandardUser.
 *
 * Por que esse repositório é necessário?
 * - Quando usamos o UserRepository (que gerencia User) para salvar um StandardUser,
 *   o Hibernate pode não propagar corretamente o INSERT nas tabelas filhas
 *   (profiled_user e standard_user).
 * - Ao usar o repositório do tipo correto (StandardUser), o Hibernate sabe exatamente
 *   qual hierarquia de tabelas precisa ser preenchida:
 *     → tb_users         (campos de User)
 *     → profiled_user    (campos de ProfiledUser: name, phone, cpf)
 *     → standard_user    (join key)
 */
public interface StandardUserRepository extends JpaRepository<StandardUser, UUID> {
}

