package tech.buildrun.springsecurity.shared.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.buildrun.springsecurity.email.models.VerificationToken;
import tech.buildrun.springsecurity.email.repository.VerificationTokenRepository;
import tech.buildrun.springsecurity.email.service.EmailService;
import tech.buildrun.springsecurity.shared.DTO.*;
import tech.buildrun.springsecurity.shared.enums.AuthProvider;
import tech.buildrun.springsecurity.shared.enums.Role;
import tech.buildrun.springsecurity.shared.models.ProfiledUser;
import tech.buildrun.springsecurity.shared.models.StandardUser;
import tech.buildrun.springsecurity.shared.models.User;
import tech.buildrun.springsecurity.shared.repository.StandardUserRepository;
import tech.buildrun.springsecurity.shared.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

// @Service: registra essa classe como um componente de serviço no Spring.
// Ela contém toda a lógica de negócio relacionada a usuários.
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenService jwtTokenService;
    private final StandardUserRepository standardUserRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       StandardUserRepository standardUserRepository,
                       JwtTokenService jwtTokenService,
                       VerificationTokenRepository tokenRepository,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.standardUserRepository = standardUserRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    /**
     * Cria um usuário no banco de dados com base no provider informado no DTO.
     *
     * Fluxo LOCAL:
     *   - Instancia um StandardUser (concreto, com perfil completo).
     *   - Valida que os campos obrigatórios de perfil (name, cpf) foram enviados.
     *   - Preenche name, phone e cpf vindos do DTO.
     *   - Criptografa a senha com BCrypt antes de salvar.
     *
     * Fluxo GOOGLE:
     *   - Instancia um User simples (sem perfil).
     *   - Não define senha (autenticação é delegada ao Google).
     *   - Os dados de perfil (name, cpf, phone) serão preenchidos posteriormente
     *     via endpoint de completar cadastro (a ser implementado).
     *
     * Em ambos os fluxos:
     *   - Verifica duplicidade de e-mail antes de criar.
     *   - Atribui a role USER por padrão.
     */
    public User createUser(CreateUserDTO createUserDTO) {
        // Garante que o e-mail ainda não está cadastrado.
        userRepository.findByEmail(createUserDTO.email())
                .ifPresent(user -> {
                    throw new EntityNotFoundException("Já existe um usuário cadastrado com este e-mail.");
                });

        if (createUserDTO.provider() == AuthProvider.LOCAL) {
            // --- Fluxo LOCAL ---
            if (createUserDTO.name() == null || createUserDTO.name().isBlank()) {
                throw new IllegalArgumentException("O campo 'name' é obrigatório para cadastro LOCAL.");
            }
            if (createUserDTO.cpf() == null || createUserDTO.cpf().isBlank()) {
                throw new IllegalArgumentException("O campo 'cpf' é obrigatório para cadastro LOCAL.");
            }
            if (createUserDTO.password() == null || createUserDTO.password().isBlank()) {
                throw new IllegalArgumentException("O campo 'password' é obrigatório para cadastro LOCAL.");
            }

            var standardUser = new StandardUser();

            // Campos de ProfiledUser
            standardUser.setName(createUserDTO.name());
            standardUser.setPhone(createUserDTO.phone());
            standardUser.setCpf(createUserDTO.cpf());

            // Campos de User
            standardUser.setPassword(bCryptPasswordEncoder.encode(createUserDTO.password()));
            standardUser.setEmail(createUserDTO.email());
            standardUser.setProvider(createUserDTO.provider());
            standardUser.setEmailVerified(false); // Usuário LOCAL precisa verificar o e-mail
            standardUser.setRoles(Set.of(Role.USER));

            // 1. Salva o usuário no banco PRIMEIRO, para que ele receba um ID.
            StandardUser savedUser = standardUserRepository.save(standardUser);

            // 2. Gera o token e salva na nova tabela, linkando com o usuário salvo.
            String tokenString = java.util.UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(tokenString, savedUser);
            tokenRepository.save(verificationToken);

            // 3. Monta o link mágico que o React vai ler.
            String confirmationLink = "http://localhost:5413/confirm-email?token=" + tokenString;

            // 4. Dispara o e-mail usando o Thymeleaf.
            emailService.enviarEmailConfirmacao(savedUser.getEmail(), savedUser.getName(), confirmationLink);

            // 5. Retorna o usuário criado.
            return savedUser;

        } else {
            // --- Fluxo GOOGLE ---
            // Instanciamos como StandardUser para que a tabela de perfil já seja criada,
            // mas deixamos name, cpf e phone nulos por enquanto.
            var googleUser = new StandardUser();
            googleUser.setEmail(createUserDTO.email());
            googleUser.setProvider(createUserDTO.provider());
            googleUser.setEmailVerified(true); // Google já valida o e-mail
            googleUser.setRoles(Set.of(Role.USER));

            // Salva usando o repositório do StandardUser!
            return standardUserRepository.save(googleUser);
        }
    }

    public UserProfileResponse getUserProfile(UUID userId) {
        // Busca o usuário genérico no banco
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        String name = null;
        String cpf = null;
        String phone = null;

        // MÁGICA AQUI: Se o usuário retornado for uma instância de ProfiledUser (ou StandardUser),
        // o Java já faz o cast automático para a variável 'profiledUser'.
        if (user instanceof ProfiledUser profiledUser) {
            name = profiledUser.getName();
            cpf = profiledUser.getCpf();
            phone = profiledUser.getPhone();
        }

        // Retorna o DTO limpo para o front-end
        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                name,
                cpf,
                phone,
                user.getProvider().name(),
                user.getRoles()
        );
    }

    /**
     * Completa o perfil de um usuário que veio do Google.
     */
    public void completeUserProfile(UUID userId, CompleteProfileDTO dto) {
        // Busca o usuário. Como salvamos o Google como StandardUser, podemos buscar direto nele.
        StandardUser user = standardUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        // Se o CPF já estiver preenchido, ele já completou o perfil!
        if (user.getCpf() != null && !user.getCpf().isBlank()) {
            throw new IllegalArgumentException("Este perfil já está completo.");
        }

        user.setName(dto.name());
        user.setCpf(dto.cpf());
        user.setPhone(dto.phone());

        standardUserRepository.save(user);
    }


    /**
     * Registra um novo usuário E já retorna um token JWT.
     * Combina criação + autenticação automática em uma única operação.
     */
    public LoginResponse registerAndAuthenticatePublicUser(CreateUserDTO createUserDTO) {
        // 1) Cria e persiste o usuário.
        User newUser = createUserPublic(createUserDTO);

        // 2) Gera o JWT para o novo usuário e retorna direto para o controller.
        return jwtTokenService.generateToken(newUser);
    }

    /**
     * Wrapper de createUser para o contexto de registro público.
     * Separado para permitir reuso sem autenticação automática, se necessário.
     */
    public User createUserPublic(CreateUserDTO createUserDTO) {
        return createUser(createUserDTO);
    }

    /**
     * Garante que o usuário admin exista no banco ao iniciar a aplicação.
     * - Se já existir: não faz nada.
     * - Se não existir: cria com e-mail "admin@admin.com", senha "123456" e role ADMIN.
     *
     * @Transactional garante que a operação seja atômica
     * (tudo salvo ou nada, em caso de erro).
     */
    @Transactional
    public void ensureAdminUser() {
        var userAdmin = userRepository.findByEmail("admin@admin.com");

        // ifPresentOrElse: executa o primeiro lambda se o admin JÁ existir,
        // ou o segundo lambda se NÃO existir.
        userAdmin.ifPresentOrElse(
                user -> {
                    // Admin já existe — nenhuma ação necessária.
                },
                () -> {
                    // Admin não existe — cria agora.
                    var user = new User();
                    user.setEmail("admin@admin.com");
                    user.setPassword(bCryptPasswordEncoder.encode("123456"));
                    user.setEmailVerified(false); // Admin criado localmente
                    user.setRoles(Set.of(Role.ADMIN));
                    userRepository.save(user);
                }
        );
    }


    @Transactional
    public void verifyUserEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou não encontrado."));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("O link expirou. Por favor, solicite um novo link.");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }

    /**
     * Autentica um usuário com e-mail e senha (fluxo LOCAL).
     * - Busca o usuário pelo e-mail.
     * - Verifica se a senha informada bate com o hash no banco.
     * - Retorna um JWT em caso de sucesso.
     */
    public LoginResponse autenticarUsuario(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new EntityNotFoundException("E-mail e/ou senha inválido(s)"));

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("Por favor, verifique seu e-mail antes de fazer login.");
        }

        boolean senhaCorreta = bCryptPasswordEncoder.matches(
                loginRequest.password(),
                user.getPassword()
        );

        if (!senhaCorreta) {
            throw new EntityNotFoundException("E-mail e/ou senha inválido(s)");
        }

        return jwtTokenService.generateToken(user);
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Este e-mail já está verificado. Pode fazer login.");
        }

        // Busca se o usuário já tem um token ativo
        var existingTokenOpt = tokenRepository.findByUser(user);

        if (existingTokenOpt.isPresent()) {
            VerificationToken existingToken = existingTokenOpt.get();

            // TRAVA ANTI-FLOOD: Se o token expira daqui a mais de 10 minutos,
            // significa que ele foi criado há menos de 5 minutos!
            if (existingToken.getExpiryDate().isAfter(LocalDateTime.now().plusMinutes(10))) {
                throw new IllegalArgumentException("Aguarde 5 minutos antes de solicitar um novo reenvio.");
            }

            // Se já passou dos 5 minutos, apagamos o token velho para gerar um novo
            tokenRepository.delete(existingToken);
            tokenRepository.flush(); // Força o banco a apagar antes de salvar o próximo
        }

        // Gera o novo token e envia o e-mail
        String tokenString = java.util.UUID.randomUUID().toString();
        VerificationToken newToken = new VerificationToken(tokenString, user);
        tokenRepository.save(newToken);

        String confirmationLink = "http://localhost:5413/confirm-email?token=" + tokenString;

        // Pega o nome do usuário de forma segura
        String name = "Cliente";
        if (user instanceof ProfiledUser profiledUser) {
            name = profiledUser.getName();
        }

        emailService.enviarEmailConfirmacao(user.getEmail(), name, confirmationLink);
    }
}
