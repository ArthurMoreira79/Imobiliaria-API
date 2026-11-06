package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.api.dto.ClienteInput;
import ifma.edu.br.imobiliariasystem.api.dto.ClienteOutput;
import ifma.edu.br.imobiliariasystem.api.mapper.ClienteMapper;
import ifma.edu.br.imobiliariasystem.domain.model.Cliente;
import ifma.edu.br.imobiliariasystem.domain.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    //GET /clientes -> 200
    @GetMapping
    public ResponseEntity<List<ClienteOutput>> listarTodos() {
        List<ClienteOutput> clientes = clienteService.listarTodos()
                .stream()
                .map(clienteMapper::toOutput)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    //GET /clientes/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<ClienteOutput> buscarPorId(@PathVariable Integer id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(clienteMapper.toOutput(cliente));
    }

    //POST /clientes -> 201 + header Location
    @PostMapping
    public ResponseEntity<ClienteOutput> criar(@Valid @RequestBody ClienteInput input) {
        Cliente salvo = clienteService.salvar(clienteMapper.toEntity(input));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(clienteMapper.toOutput(salvo));
    }

    //PUT /clientes/{id} -> 200, 400, 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<ClienteOutput> atualizar(@PathVariable Integer id,
                                                   @Valid @RequestBody ClienteInput input) {
        Cliente atualizado = clienteService.atualizar(id, clienteMapper.toEntity(input));
        return ResponseEntity.ok(clienteMapper.toOutput(atualizado));
    }

    //DELETE /clientes/{id} -> 204, 404 ou 409
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}