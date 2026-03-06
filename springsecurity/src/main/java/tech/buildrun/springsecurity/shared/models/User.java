package tech.buildrun.springsecurity.shared.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import tech.buildrun.springsecurity.shared.enums.AuthProvider;
import tech.buildrun.springsecurity.shared.enums.Role;

import java.util.Set;
import java.util.UUID;

// @Entity: indica ao JPA que essa classe representa uma tabela no banco de dados.
@Entity
// @Table: define o nome da tabela no banco como "tb_users".
@Table(name = "tb_users")
// @Inheritance: permite que subclasses de User sejam mapeadas em tabelas separadas (JOINED).
// Útil se futuramente existir, ex: AdminUser ou GoogleUser com colunas extras.
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    // Chave primária gerada automaticamente como UUID (identificador único universal).
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID userId;

    // E-mail único no banco — dois usuários não podem ter o mesmo e-mail.
    // @Email valida o formato antes de persistir.
    @Column(unique = true)
    @Email
    protected String email;

    // Senha armazenada como hash BCrypt.
    // nullable = true pois usuários do Google não possuem senha local.
    @Column(nullable = true)
    protected String password;

    // Indica como o usuário foi criado: LOCAL (e-mail/senha) ou GOOGLE (OAuth2).
    // @Enumerated(STRING) salva o nome do enum como texto no banco (ex: "GOOGLE"),
    // em vez de salvar o índice numérico — mais legível e seguro.
    @Enumerated(EnumType.STRING)
    protected AuthProvider provider;

    @Column(name = "email_verified", nullable = false)
    protected boolean emailVerified;

  // Conjunto de roles (perfis de acesso) do usuário, ex: USER, ADMIN.
    // @ElementCollection: cria uma tabela separada "tb_users_roles" para armazenar os valores.
    // fetch = EAGER: carrega as roles junto com o usuário em toda consulta.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    protected Set<Role> roles;

    // --- Getters e Setters padrão ---

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public AuthProvider getProvider() { return provider; }
    public void setProvider(AuthProvider provider) { this.provider = provider; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
}
