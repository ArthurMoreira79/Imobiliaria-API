package ifma.edu.br.imobiliariasystem.security;

import ifma.edu.br.imobiliariasystem.api.controlleradvice.ErroResposta;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDateTime;

//Respostas de erro do Spring Security no mesmo formato JSON do ApiExceptionHandler.
//Por que não dá para usar o @RestControllerAdvice? Porque a segurança barra a requisição
//nos FILTROS, antes de ela chegar ao controller; o advice só enxerga exceções lançadas
//dentro dos controllers. Por isso a resposta é escrita aqui, direto no HttpServletResponse.
@Component
public class SegurancaErroHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    //O mesmo JsonMapper (Jackson 3) que o Spring usa nos controllers: datas saem no mesmo formato
    public SegurancaErroHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    //401: não autenticado (sem credenciais, usuário inexistente ou senha errada)
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        //Diz ao cliente como se autenticar (padrão do HTTP para o 401)
        response.setHeader("WWW-Authenticate", "Basic realm=\"imobiliaria\"");

        escrever(response, HttpStatus.UNAUTHORIZED, "Não autenticado",
                "Informe login e senha válidos (HTTP Basic) para acessar este recurso.");
    }

    //403: autenticado, mas o perfil não tem permissão
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escrever(response, HttpStatus.FORBIDDEN, "Acesso negado",
                "Seu perfil não tem permissão para realizar esta operação.");
    }

    private void escrever(HttpServletResponse response, HttpStatus status,
                          String titulo, String mensagem) throws IOException {
        ErroResposta erro = new ErroResposta(status.value(), titulo, mensagem, LocalDateTime.now(), null);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        jsonMapper.writeValue(response.getOutputStream(), erro);
    }
}