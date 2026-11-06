package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.api.dto.LocacaoInput;
import ifma.edu.br.imobiliariasystem.api.dto.LocacaoOutput;
import ifma.edu.br.imobiliariasystem.api.mapper.LocacaoMapper;
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
    private final LocacaoMapper locacaoMapper;

    public LocacaoController(LocacaoService locacaoService, LocacaoMapper locacaoMapper) {
        this.locacaoService = locacaoService;
        this.locacaoMapper = locacaoMapper;
    }

    //GET /locacoes -> 200
    @GetMapping
    public ResponseEntity<List<LocacaoOutput>> listarTodos() {
        List<LocacaoOutput> locacoes = locacaoService.listarTodos()
                .stream()
                .map(locacaoMapper::toOutput)
                .toList();
        return ResponseEntity.ok(locacoes);
    }

    //GET /locacoes/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<LocacaoOutput> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(locacaoMapper.toOutput(locacaoService.buscarPorId(id)));
    }

    //POST /locacoes -> 201 + header Location
    @PostMapping
    public ResponseEntity<LocacaoOutput> criar(@Valid @RequestBody LocacaoInput input) {
        Locacao salva = locacaoService.salvar(locacaoMapper.toEntity(input));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salva.getId())
                .toUri();

        return ResponseEntity.created(location).body(locacaoMapper.toOutput(salva));
    }

    //PUT /locacoes/{id} -> 200, 400, 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<LocacaoOutput> atualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody LocacaoInput input) {
        Locacao atualizada = locacaoService.atualizar(id, locacaoMapper.toEntity(input));
        return ResponseEntity.ok(locacaoMapper.toOutput(atualizada));
    }

    //DELETE /locacoes/{id} -> 204, 404 ou 409
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        locacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    //PUT /locacoes/{id}/confirmar -> 200, 404 ou 409 (período ocupado ou já confirmada)
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<LocacaoOutput> confirmar(@PathVariable Integer id) {
        return ResponseEntity.ok(locacaoMapper.toOutput(locacaoService.confirmar(id)));
    }

    //PUT /locacoes/{id}/cancelar -> 200, 404 ou 409 (já cancelada)
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<LocacaoOutput> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(locacaoMapper.toOutput(locacaoService.cancelar(id)));
    }
}
