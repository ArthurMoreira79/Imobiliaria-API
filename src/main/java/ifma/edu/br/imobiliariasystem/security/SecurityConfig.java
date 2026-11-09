package ifma.edu.br.imobiliariasystem.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String ATENDENTE = "ATENDENTE";
    private static final String CLIENTE = "CLIENTE";

    private final SegurancaErroHandler segurancaErroHandler;

    public SecurityConfig(SegurancaErroHandler segurancaErroHandler) {
        this.segurancaErroHandler = segurancaErroHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //CSRF protege formulários de páginas com sessão/cookie. Numa API REST stateless,
                //em que as credenciais vão em todo request, ele só atrapalharia os POST/PUT/DELETE.
                .csrf(AbstractHttpConfigurer::disable)

                //Stateless: o servidor não guarda sessão; toda requisição traz o header Authorization.
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //Matriz de permissões. A ORDEM IMPORTA: o Spring testa de cima para baixo
                //e aplica a PRIMEIRA regra que casar com a requisição.
                .authorizeHttpRequests(auth -> auth
                        //Página de erro interna do Spring Boot: liberada para um erro não virar 401 por engano
                        .requestMatchers("/error").permitAll()

                        //Gerenciar usuários e perfis: só ADMIN
                        .requestMatchers("/usuarios/**").hasRole(ADMIN)

                        //Exclusão de qualquer recurso: só ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole(ADMIN)

                        //Imóveis: todos consultam; ADMIN e ATENDENTE cadastram e atualizam
                        .requestMatchers(HttpMethod.GET, "/imoveis/**").hasAnyRole(ADMIN, ATENDENTE, CLIENTE)
                        .requestMatchers("/imoveis/**").hasAnyRole(ADMIN, ATENDENTE)

                        //Clientes: só ADMIN e ATENDENTE
                        .requestMatchers("/clientes/**").hasAnyRole(ADMIN, ATENDENTE)

                        //Locações: todos consultam e cadastram (o CLIENTE só as próprias: regra do passo 4,
                        //feita no service). Atualizar, confirmar e cancelar: ADMIN e ATENDENTE
                        .requestMatchers(HttpMethod.GET, "/locacoes/**").hasAnyRole(ADMIN, ATENDENTE, CLIENTE)
                        .requestMatchers(HttpMethod.POST, "/locacoes").hasAnyRole(ADMIN, ATENDENTE, CLIENTE)
                        .requestMatchers("/locacoes/**").hasAnyRole(ADMIN, ATENDENTE)

                        //Aluguéis (cobranças): ADMIN e ATENDENTE
                        .requestMatchers("/alugueis/**").hasAnyRole(ADMIN, ATENDENTE)

                        //Qualquer outra URL: basta estar logado (uma rota inexistente continua dando 404)
                        .anyRequest().authenticated()
                )

                //HTTP Basic com a nossa resposta 401 (credenciais erradas)
                .httpBasic(basic -> basic.authenticationEntryPoint(segurancaErroHandler))

                //Respostas de segurança: 401 sem credenciais e 403 sem permissão
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(segurancaErroHandler)
                        .accessDeniedHandler(segurancaErroHandler)
                );

        return http.build();
    }

    //BCrypt: o mesmo algoritmo dos hashes da V12
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}