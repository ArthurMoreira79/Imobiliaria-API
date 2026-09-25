package ifma.edu.br.imobiliariasystem.domain.exception;

//Lançada quando uma regra de négocio é violada (ex: cpf duplicado) (409)
public class RegraNegocioException extends RuntimeException{
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
