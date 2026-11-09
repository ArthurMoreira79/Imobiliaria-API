package ifma.edu.br.imobiliariasystem.api.dto;

import java.util.List;

//O que a API devolve sobre um usuário. NÃO TEM SENHA: nem o hash sai da API (requisito 2.1.7 e 2.3.4).
public record UsuarioOutput(
        Integer id,
        String login,
        List<String> perfis,
        ClienteResumo cliente
) {

    public record ClienteResumo(Integer id, String nome_cliente) {}
}