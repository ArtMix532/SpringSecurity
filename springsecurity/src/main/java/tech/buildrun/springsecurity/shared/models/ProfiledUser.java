package tech.buildrun.springsecurity.shared.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

/**
 * Classe abstrata que representa um usuário com dados de perfil completo.
 *
 * Estratégia JOINED: os campos name, phone e cpf ficam na tabela "profiled_user",
 * ligada à "tb_users" via user_id como chave estrangeira.
 *
 * Lombok (@Getter @Setter) gera automaticamente todos os getters e setters —
 * NÃO declarar métodos manuais para evitar conflito de assinaturas duplicadas,
 * o que causava o problema onde os dados de perfil não eram persistidos.
 */
@Entity
@Table(name = "profiled_user")
@PrimaryKeyJoinColumn(name = "user_id")
@Getter
@Setter
@NoArgsConstructor
public abstract class ProfiledUser extends User {

    // @Column(nullable = false) garante a constraint no banco.
    // Sem @Column explícito, alguns dialetos JPA podem ignorar o campo na tabela filha.
    @Column(name = "name", nullable = true)
    protected String name;

    // Telefone é opcional — nullable = true (padrão, mas explícito para clareza).
    @Column(name = "phone", nullable = true)
    protected String phone;

    // CPF único e obrigatório, validado pelo Hibernate Validator.
    @CPF(message = "Favor digitar um cpf válido")
    @Column(name = "cpf", unique = true, nullable = true)
    protected String cpf;


}
