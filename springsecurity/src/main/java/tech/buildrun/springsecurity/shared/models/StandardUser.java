package tech.buildrun.springsecurity.shared.models;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Implementação concreta de ProfiledUser para usuários cadastrados via fluxo LOCAL.
 *
 * Por que essa classe existe?
 * - ProfiledUser é abstract, então o JPA não consegue instanciá-la diretamente.
 * - StandardUser herda todos os campos de ProfiledUser (name, phone, cpf)
 *   e de User (userId, email, password, provider, roles).
 * - A estratégia de herança é JOINED, ou seja:
 *     → Dados de User          ficam na tabela "tb_users"
 *     → Dados de ProfiledUser  ficam na tabela "profiled_user"
 *     → Dados de StandardUser  ficam na tabela "standard_user" (apenas o join key, sem colunas extras por ora)
 *   As tabelas são unidas pelo userId como chave estrangeira.
 *
 * Futuramente, novos tipos de usuários com perfil (ex: AdminUser, SellerUser)
 * podem estender ProfiledUser da mesma forma.
 */
@Entity
@Table(name = "standard_user")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
public class StandardUser extends ProfiledUser {
    // Nenhum campo adicional por enquanto.
    // Toda a estrutura de dados já está em ProfiledUser e User.
}
