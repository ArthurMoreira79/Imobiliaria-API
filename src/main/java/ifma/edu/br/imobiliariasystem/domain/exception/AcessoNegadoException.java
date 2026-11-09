package ifma.edu.br.imobiliariasystem.domain.exception;

//Lançada quando o usuário está autenticado e tem o perfil certo para o endpoint,
//mas tenta acessar um registro que não é dele (ex: locação de outro cliente) (403)
public class AcessoNegadoException extends RuntimeException {
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}