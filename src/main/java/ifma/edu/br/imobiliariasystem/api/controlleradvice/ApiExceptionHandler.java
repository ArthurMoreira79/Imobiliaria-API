package ifma.edu.br.imobiliariasystem.api.controlleradvice;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // 400 - falhas do Bean Validation (@Valid no controller)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroResposta.Campo> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroResposta.Campo(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return montarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "Um ou mais campos estão inválidos. Corrija e tente novamente.", campos);
    }

    // 400 - JSON malformado ou tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido",
                "Verifique a sintaxe do JSON e o formato dos campos (datas no padrão AAAA-MM-DD).", null);
    }

    // 400 - parâmetro da URL com tipo errado (ex: GET /clientes/abc, onde o id deveria ser número)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> tratarTipoParametro(MethodArgumentTypeMismatchException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido",
                "O parâmetro '" + ex.getName() + "' recebeu o valor '" + ex.getValue() + "', que não é do tipo esperado.", null);
    }

    // 404 - registro não encontrado
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(EntidadeNaoEncontradaException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), null);
    }

    // 404 - URL que não corresponde a nenhum endpoint (ex: GET /clientess)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> tratarRotaInexistente(NoResourceFoundException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, "Endpoint não encontrado",
                "Não existe endpoint para '/" + ex.getResourcePath() + "'.", null);
    }

    // 405 - verbo HTTP não suportado na rota (ex: DELETE /clientes, sem id)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException ex) {
        return montarResposta(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "O método " + ex.getMethod() + " não é suportado neste endpoint.", null);
    }

    // 415 - corpo enviado em formato diferente de JSON
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResposta> tratarFormatoNaoSuportado(HttpMediaTypeNotSupportedException ex) {
        return montarResposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Formato não suportado",
                "Envie o corpo da requisição em JSON (Content-Type: application/json).", null);
    }

    // 409 - regra de negócio violada
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegraNegocio(RegraNegocioException ex) {
        return montarResposta(HttpStatus.CONFLICT, "Regra de negócio violada", ex.getMessage(), null);
    }

    // 409 - o banco recusou a operação
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarIntegridade(DataIntegrityViolationException ex) {
        return montarResposta(HttpStatus.CONFLICT, "Conflito de integridade",
                "A operação não pode ser concluída porque o registro está em uso ou viola uma restrição do banco.", null);
    }

    // 500 - última rede de proteção: qualquer erro não previsto acima.
    // O detalhe vai para o log do servidor, não para o cliente da API.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado no servidor. Tente novamente mais tarde.", null);
    }

    private ResponseEntity<ErroResposta> montarResposta(HttpStatus status, String titulo, String mensagem, List<ErroResposta.Campo> campos) {
        ErroResposta corpo = new ErroResposta(status.value(), titulo, mensagem, LocalDateTime.now(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
