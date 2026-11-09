package ifma.edu.br.imobiliariasystem.domain.service;

import ifma.edu.br.imobiliariasystem.domain.exception.*;
import ifma.edu.br.imobiliariasystem.domain.model.Perfil;
import ifma.edu.br.imobiliariasystem.domain.model.Usuario;
import ifma.edu.br.imobiliariasystem.domain.repository.PerfilRepository;
import ifma.edu.br.imobiliariasystem.domain.repository.UsuarioRepository;
import ifma.edu.br.imobiliariasystem.security.UsuarioLogado;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final String PERFIL_CLIENTE = "CLIENTE";

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final ClienteService clienteService;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioLogado usuarioLogado;

    public UsuarioService(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository,
                          ClienteService clienteService, PasswordEncoder passwordEncoder,
                          UsuarioLogado usuarioLogado) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioLogado = usuarioLogado;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário de id " + id + " não encontrado"));
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        usuario.setId(null);

        if (usuarioRepository.existsByLogin(usuario.getLogin())) {
            throw new RegraNegocioException("Já existe um usuário com o login '" + usuario.getLogin() + "'");
        }

        carregarPerfis(usuario);
        validarVinculoCliente(usuario, null);

        //Requisito 2.1.6 / 2.3.5: a senha vira hash ANTES de chegar ao banco
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(Integer id, Usuario dadosAtualizados) {
        Usuario atual = buscarPorId(id);

        if (usuarioRepository.existsByLoginAndIdNot(dadosAtualizados.getLogin(), id)) {
            throw new RegraNegocioException("Já existe outro usuário com o login '" + dadosAtualizados.getLogin() + "'");
        }

        carregarPerfis(dadosAtualizados);
        validarVinculoCliente(dadosAtualizados, id);

        //Requisito 2.3.6: ninguém altera os próprios privilégios (nem o ADMIN)
        if (ehOProprioUsuario(atual) && !nomes(atual.getPerfis()).equals(nomes(dadosAtualizados.getPerfis()))) {
            throw new AcessoNegadoException("Você não pode alterar os seus próprios perfis");
        }

        //Senha opcional no PUT: sem senha nova, mantém o hash atual; com senha nova, gera outro hash
        if (dadosAtualizados.getSenha() == null) {
            dadosAtualizados.setSenha(atual.getSenha());
        } else {
            dadosAtualizados.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
        }

        dadosAtualizados.setId(id);
        return usuarioRepository.save(dadosAtualizados);
    }

    @Transactional
    public void excluir(Integer id) {
        Usuario usuario = buscarPorId(id);

        if (ehOProprioUsuario(usuario)) {
            throw new AcessoNegadoException("Você não pode excluir o seu próprio usuário");
        }

        usuarioRepository.delete(usuario);
        usuarioRepository.flush();
    }

    //Troca os perfis que vieram só com o nome pelos registros do banco (404 se o nome não existir)
    private void carregarPerfis(Usuario usuario) {
        Set<Perfil> perfis = usuario.getPerfis().stream()
                .map(p -> perfilRepository.findByNome(p.getNome())
                        .orElseThrow(() -> new EntidadeNaoEncontradaException(
                                "Perfil '" + p.getNome() + "' não existe. Use ADMIN, ATENDENTE ou CLIENTE")))
                .collect(Collectors.toSet());
        usuario.setPerfis(perfis);
    }

    //Perfil CLIENTE exige cliente vinculado; os outros perfis não podem ter.
    //idIgnorado: na atualização, o próprio usuário não conta na verificação de unicidade.
    private void validarVinculoCliente(Usuario usuario, Integer idIgnorado) {
        boolean temPerfilCliente = nomes(usuario.getPerfis()).contains(PERFIL_CLIENTE);

        if (temPerfilCliente && usuario.getCliente() == null) {
            throw new RegraNegocioException("Usuário com perfil CLIENTE precisa estar vinculado a um cliente");
        }
        if (!temPerfilCliente && usuario.getCliente() != null) {
            throw new RegraNegocioException("Só usuários com perfil CLIENTE podem ser vinculados a um cliente");
        }
        if (usuario.getCliente() == null) {
            return;
        }

        Integer idCliente = usuario.getCliente().getId();
        boolean jaVinculado = idIgnorado == null
                ? usuarioRepository.existsByClienteId(idCliente)
                : usuarioRepository.existsByClienteIdAndIdNot(idCliente, idIgnorado);
        if (jaVinculado) {
            throw new RegraNegocioException("O cliente de id " + idCliente + " já possui um usuário");
        }

        //404 se o cliente não existir
        usuario.setCliente(clienteService.buscarPorId(idCliente));
    }

    private boolean ehOProprioUsuario(Usuario usuario) {
        return usuario.getLogin().equals(usuarioLogado.getLogin());
    }

    private Set<String> nomes(Set<Perfil> perfis) {
        return perfis.stream().map(Perfil::getNome).collect(Collectors.toSet());
    }
}