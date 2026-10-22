package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.domain.model.Imovel;
import ifma.edu.br.imobiliariasystem.domain.service.ImovelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;

import java.net.URI;
import java.util.List;

@RestController 
@RequestMapping("/imoveis")
public class ImovelController {
    
    private final ImovelService imovelService;

    public ImovelController(ImovelService imovelService) {
        this.imovelService = imovelService;
    }

    //GET /imoveis -> 200
    @GetMapping 
    public ResponseEntity<List<Imovel>> listarTodos() {
        return ResponseEntity.ok(imovelService.listarTodos());
    }
    
    //GET /imoveis/paginado?page=0&size=5&sort=metragem,desc -> 200
    //O Spring monta o Pageable a partir dos parâmetros page, size e sort da URL.
    //@PageableDefault vale quando eles não são informados
    @GetMapping("/paginado")
    public ResponseEntity<PagedModel<Imovel>> buscarPaginado(@PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(new PagedModel<>(imovelService.buscarPaginado(pageable)));
    }

    //GET /imoveis/busca?tipo=casa&cidade=luis&dormitorios=3 -> 200
    //Todos os filtros são opcionais; também aceita page, size e sort
    @GetMapping("/busca")
    public ResponseEntity<PagedModel<Imovel>> pesquisar(@RequestParam(required = false) String tipo,
                                                        @RequestParam(required = false) String cidade,
                                                        @RequestParam(required = false) Integer dormitorios,
                                                        @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(new PagedModel<>(imovelService.pesquisarPorExemplo(tipo, cidade, dormitorios, pageable)));
    }

    //GET /imoveis/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<Imovel> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(imovelService.buscarPorId(id));
    }

    //POST /imoveis -> 201 + header Location apontando para o novo recurso
    @PostMapping
    public ResponseEntity<Imovel> criar(@Valid @RequestBody Imovel imovel) {
        Imovel salvo = imovelService.salvar(imovel);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(salvo);
    }

    //PUT /imoveis/{id} -> 200, 400, 404, 409
    @PutMapping("/{id}")
    public ResponseEntity<Imovel> atualizar(@PathVariable Integer id, @Valid @RequestBody Imovel imovel) {
        return ResponseEntity.ok(imovelService.atualizar(id, imovel));
    }

    //DELETE /imoveis/{id} -> 204, 404, 409
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        imovelService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
