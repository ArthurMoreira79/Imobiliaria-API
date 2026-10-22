package ifma.edu.br.imobiliariasystem.domain;

//Grupos do Bean Validation. Uma anotação com groups = X só é verificada
//quando alguém pede para validar o grupo X.
//Usados na Locação: o JSON traz "inquilino": {"id": 1}, e só o id precisa ser validado,
//não os outros campos obrigatórios do Cliente (nome, cpf...).
public interface ValidationGroups {

    interface ClienteId {}

    interface ImovelId {}

    interface LocacaoId {}
}
