package ifma.edu.br.imobiliariasystem.api.controlleradvice;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

//Formato padrão de todas as respostas de erro da API.
//"campos" só aparece no JSON quando houver erros de validação.

@JsonInclude (JsonInclude.Include.NON_NULL)
public record ErroResposta (
    int status,
    String titulo,
    String mensagem,
    LocalDateTime dataHora,
    List<Campo> campos
) {
    public record Campo(String nome, String mensagem) {}
}
