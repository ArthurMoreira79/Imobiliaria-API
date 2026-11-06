package ifma.edu.br.imobiliariasystem.api.controller;

import ifma.edu.br.imobiliariasystem.api.dto.ImovelFiltro;
import ifma.edu.br.imobiliariasystem.api.dto.ImovelInput;
import ifma.edu.br.imobiliariasystem.api.dto.ImovelOutput;
import ifma.edu.br.imobiliariasystem.api.mapper.ImovelMapper;
import ifma.edu.br.imobiliariasystem.domain.model.Imovel;
import ifma.edu.br.imobiliariasystem.domain.service.ImovelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/imoveis")
public class ImovelController {

    private final ImovelService imovelService;
    private final ImovelMapper imovelMapper;

    public ImovelController(ImovelService imovelService, ImovelMapper imovelMapper) {
        this.imovelService = imovelService;
        this.imovelMapper = imovelMapper;
    }

    //GET /imoveis -> 200
    @GetMapping
    public ResponseEntity<List<ImovelOutput>> listarTodos() {
        List<ImovelOutput> imoveis = imovelService.listarTodos()
                .stream()
                .map(imovelMapper::toOutput)
                .toList();
        return ResponseEntity.ok(imoveis);
    }

    //GET /imoveis/paginado?page=0&size=5&sort=metragem,desc -> 200
    @GetMapping("/paginado")
    public ResponseEntity<PagedModel<ImovelOutput>> buscarPaginado(
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        Page<ImovelOutput> pagina = imovelService.buscarPaginado(pageable)
                .map(imovelMapper::toOutput);
        return ResponseEntity.ok(new PagedModel<>(pagina));
    }

    //GET /imoveis/busca?tipo=casa&cidade=luis&dormitorios=3 -> 200
    //Todos os filtros são opcionais; também aceita page, size e sort
    @GetMapping("/busca")
    public ResponseEntity<PagedModel<ImovelOutput>> pesquisar(
            @ModelAttribute ImovelFiltro filtro,
            @PageableDefault(size = 5, sort = "id") Pageable pageable) {
        Imovel exemplo = imovelMapper.toExemplo(filtro);
        Page<ImovelOutput> pagina = imovelService.pesquisarPorExemplo(exemplo, pageable)
                .map(imovelMapper::toOutput);
        return ResponseEntity.ok(new PagedModel<>(pagina));
    }

    //GET /imoveis/{id} -> 200 ou 404
    @GetMapping("/{id}")
    public ResponseEntity<ImovelOutput> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(imovelMapper.toOutput(imovelService.buscarPorId(id)));
    }

    //POST /imoveis -> 201 + header Location
    @PostMapping
    public ResponseEntity<ImovelOutput> criar(@Valid @RequestBody ImovelInput input) {
        Imovel salvo = imovelService.salvar(imovelMapper.toEntity(input));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(imovelMapper.toOutput(salvo));
    }

    //PUT /imoveis/{id} -> 200, 400, 404 ou 409
    @PutMapping("/{id}")
    public ResponseEntity<ImovelOutput> atualizar(@PathVariable Integer id,
                                                  @Valid @RequestBody ImovelInput input) {
        Imovel atualizado = imovelService.atualizar(id, imovelMapper.toEntity(input));
        return ResponseEntity.ok(imovelMapper.toOutput(atualizado));
    }

    //DELETE /imoveis/{id} -> 204, 404 ou 409
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        imovelService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}