package ifma.edu.br.imobiliariasystem.domain.exception;

//Lançada quando umm registro buscado por ID não existe (404 not found)
public class EntidadeNaoEncontradaException extends RuntimeException{
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
