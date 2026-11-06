package ifma.edu.br.imobiliariasystem.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "locacao")
public class Locacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Validações de entrada ficam no LocacaoInput (api/dto); aqui só o mapeamento do banco.
    //O inquilino e o imóvel chegam ao service só com o id e são carregados por ele.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_inquilino", nullable = false)
    private Cliente inquilino;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_imovel", nullable = false)
    private Imovel imovel;

    //Opcional no JSON: se não vier, o service define true ao criar
    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate data_inicio;

    //Obrigatória: sem ela não dá para calcular a quantidade de diárias
    @Column(name = "data_fim", nullable = false)
    private LocalDate data_fim;

    @Column(name = "dia_vencimento", nullable = false)
    private Integer dia_vencimento;

    @Column(name = "perc_multa", precision = 5, scale = 2)
    private BigDecimal perc_multa;

    //Calculado pelo service (valor_diaria do imóvel x quantidade de diárias).
    //Não existe no LocacaoInput, então o cliente da API não consegue enviá-lo
    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor_total;

    @Column(length = 500)
    private String obs;

    public Locacao() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cliente getInquilino() {
        return inquilino;
    }

    public void setInquilino(Cliente inquilino) {
        this.inquilino = inquilino;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(LocalDate data_inicio) {
        this.data_inicio = data_inicio;
    }

    public LocalDate getData_fim() {
        return data_fim;
    }

    public void setData_fim(LocalDate data_fim) {
        this.data_fim = data_fim;
    }

    public Integer getDia_vencimento() {
        return dia_vencimento;
    }

    public void setDia_vencimento(Integer dia_vencimento) {
        this.dia_vencimento = dia_vencimento;
    }

    public BigDecimal getPerc_multa() {
        return perc_multa;
    }

    public void setPerc_multa(BigDecimal perc_multa) {
        this.perc_multa = perc_multa;
    }

    public BigDecimal getValor_total() {
        return valor_total;
    }

    public void setValor_total(BigDecimal valor_total) {
        this.valor_total = valor_total;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Locacao outra)) return false;
        return id != null && Objects.equals(id, outra.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    //Mostra só os ids do inquilino e do imóvel, para não imprimir os objetos inteiros
    @Override
    public String toString() {
        return "Locacao (id = " + id +
        ", inquilino = " + (inquilino != null ? inquilino.getId() : null) +
        ", imovel = " + (imovel != null ? imovel.getId() : null) +
        ", ativo = " + ativo +
        ", inicio = " + data_inicio +
        ", fim = " + data_fim +
        ", vencimento = dia " + dia_vencimento +
        ", valor = " + valor_total + ")";
    }
}
