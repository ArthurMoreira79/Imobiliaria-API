package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.domain.model.Aluguel;
import ifma.edu.br.imobiliariasystem.domain.service.AluguelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }

    //GET /alugueis -> 200
    @GetMapping
    public ResponseEntity<List<Aluguel>> listarTodos() {
        return ResponseEntity.ok(aluguelService.listarTodos());
    }

    //GET /alugueis/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<Aluguel> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aluguelService.buscarPorId(id));
    }

    //POST /alugueis -> 201 + header Location
    @PostMapping
    public ResponseEntity<Aluguel> criar(@Valid @RequestBody Aluguel aluguel) {
        Aluguel salvo = aluguelService.salvar(aluguel);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    //PUT /alugueis/{id} -> 200, 400, 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<Aluguel> atualizar(@PathVariable Integer id, @Valid @RequestBody Aluguel aluguel) {
        return ResponseEntity.ok(aluguelService.atualizar(id, aluguel));
    }

    //DELETE /alugueis/{id} -> 204 ou 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        aluguelService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}