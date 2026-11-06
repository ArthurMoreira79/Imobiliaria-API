package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.api.dto.AluguelInput;
import ifma.edu.br.imobiliariasystem.api.dto.AluguelOutput;
import ifma.edu.br.imobiliariasystem.api.mapper.AluguelMapper;
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
    private final AluguelMapper aluguelMapper;

    public AluguelController(AluguelService aluguelService, AluguelMapper aluguelMapper) {
        this.aluguelService = aluguelService;
        this.aluguelMapper = aluguelMapper;
    }

    //GET /alugueis -> 200
    @GetMapping
    public ResponseEntity<List<AluguelOutput>> listarTodos() {
        List<AluguelOutput> alugueis = aluguelService.listarTodos()
                .stream()
                .map(aluguelMapper::toOutput)
                .toList();
        return ResponseEntity.ok(alugueis);
    }

    //GET /alugueis/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<AluguelOutput> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(aluguelMapper.toOutput(aluguelService.buscarPorId(id)));
    }

    //POST /alugueis -> 201 + header Location
    @PostMapping
    public ResponseEntity<AluguelOutput> criar(@Valid @RequestBody AluguelInput input) {
        Aluguel salvo = aluguelService.salvar(aluguelMapper.toEntity(input));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(aluguelMapper.toOutput(salvo));
    }

    //PUT /alugueis/{id} -> 200, 400, 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<AluguelOutput> atualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody AluguelInput input) {
        Aluguel atualizado = aluguelService.atualizar(id, aluguelMapper.toEntity(input));
        return ResponseEntity.ok(aluguelMapper.toOutput(atualizado));
    }

    //DELETE /alugueis/{id} -> 204 ou 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        aluguelService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
