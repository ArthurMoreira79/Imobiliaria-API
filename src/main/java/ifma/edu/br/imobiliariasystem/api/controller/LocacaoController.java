package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.domain.model.Locacao;
import ifma.edu.br.imobiliariasystem.domain.service.LocacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/locacoes")
public class LocacaoController {

    private final LocacaoService locacaoService;

    public LocacaoController(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    //GET /locacoes -> 200
    @GetMapping
    public ResponseEntity<List<Locacao>> listarTodos() {
        return ResponseEntity.ok(locacaoService.listarTodos());
    }

    //GET /locacoes/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<Locacao> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(locacaoService.buscarPorId(id));
    }

    //POST /locacoes -> 201 + header Location apontando para o novo recurso
    @PostMapping
    public ResponseEntity<Locacao> criar(@Valid @RequestBody Locacao locacao) {
        Locacao salva = locacaoService.salvar(locacao);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salva.getId())
                .toUri();

        return ResponseEntity.created(location).body(salva);
    }

    //PUT /locacoes/{id} -> 200, 400, 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<Locacao> atualizar(@PathVariable Integer id, @Valid @RequestBody Locacao locacao) {
        return ResponseEntity.ok(locacaoService.atualizar(id, locacao));
    }

    //DELETE /locacoes/{id} -> 204, 404 ou 409
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        locacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    //PUT /locacoes/{id}/confirmar -> 200, 404 ou 409 (período ocupado ou já confirmada)
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Locacao> confirmar(@PathVariable Integer id) {
        return ResponseEntity.ok(locacaoService.confirmar(id));
    }

    //PUT /locacoes/{id}/cancelar -> 200, 404 ou 409 (já cancelada)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Locacao> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(locacaoService.cancelar(id));
    }
}
