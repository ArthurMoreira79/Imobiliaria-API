package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.api.dto.UsuarioAtualizacaoInput;
import ifma.edu.br.imobiliariasystem.api.dto.UsuarioInput;
import ifma.edu.br.imobiliariasystem.api.dto.UsuarioOutput;
import ifma.edu.br.imobiliariasystem.api.mapper.UsuarioMapper;
import ifma.edu.br.imobiliariasystem.domain.model.Usuario;
import ifma.edu.br.imobiliariasystem.domain.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

//Gerenciamento de usuários e perfis: só ADMIN (regra "/usuarios/**" no SecurityConfig)
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    //GET /usuarios -> 200
    @GetMapping
    public ResponseEntity<List<UsuarioOutput>> listarTodos() {
        List<UsuarioOutput> usuarios = usuarioService.listarTodos()
                .stream()
                .map(usuarioMapper::toOutput)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    //GET /usuarios/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioOutput> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioMapper.toOutput(usuarioService.buscarPorId(id)));
    }

    //POST /usuarios -> 201, 400, 404 (perfil ou cliente inexistente) ou 409
    @PostMapping
    public ResponseEntity<UsuarioOutput> criar(@Valid @RequestBody UsuarioInput input) {
        Usuario salvo = usuarioService.salvar(usuarioMapper.toEntity(input));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(usuarioMapper.toOutput(salvo));
    }

    //PUT /usuarios/{id} -> 200, 400, 403 (próprios perfis), 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioOutput> atualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody UsuarioAtualizacaoInput input) {
        Usuario atualizado = usuarioService.atualizar(id, usuarioMapper.toEntity(input));
        return ResponseEntity.ok(usuarioMapper.toOutput(atualizado));
    }

    //DELETE /usuarios/{id} -> 204, 403 (o próprio usuário) ou 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}