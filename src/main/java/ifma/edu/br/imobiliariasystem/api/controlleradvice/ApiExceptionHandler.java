package ifma.edu.br.imobiliariasystem.api.controlleradvice;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

import javax.xml.crypto.Data;

@RestControllerAdvice
public class ApiExceptionHandler {
    
    // 400 - falhas do Bean Validation (@Valid no controller)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ErroResposta.Campo> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ErroResposta.Campo(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return montarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "Um ou mais campos estão invélidos. Corrija e tente novamente.", campos);
    }

    // 400 - JSON malformado ou tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> tratarJsonInválido(HttpMessageNotReadableException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido",
                "Verifique a sintaxe do JSON e o formato dos campos (datas no padrão AAAA-MM-DD).", null);
    }

    //404 - registro não encontrado
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(EntidadeNaoEncontradaException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), null);
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

    private ResponseEntity<ErroResposta> montarResposta(HttpStatus status, String titulo, String mensagem, List<ErroResposta.Campo> campos) {
        ErroResposta corpo = new ErroResposta(status.value(), titulo, mensagem, LocalDateTime.now(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
