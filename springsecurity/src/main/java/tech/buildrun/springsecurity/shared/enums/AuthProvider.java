package tech.buildrun.springsecurity.shared.enums;

/**
 * Enum que identifica a origem (provedor) da autenticação do usuário.
 * É salvo na tabela tb_users na coluna "provider".
 *
 * Permite que a aplicação saiba como o usuário foi criado e
 * tome decisões diferentes em cada fluxo (ex: não exigir senha para GOOGLE).
 */
public enum AuthProvider {

    // Usuário criado com e-mail e senha diretamente na nossa aplicação.
    LOCAL,

    // Usuário autenticado via conta Google (OAuth2 / OpenID Connect).
    // Não possui senha local — a identidade é verificada pelo Google.
    GOOGLE
}
