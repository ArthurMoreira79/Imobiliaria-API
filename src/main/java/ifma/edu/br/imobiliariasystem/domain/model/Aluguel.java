package ifma.edu.br.imobiliariasystem.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

//Cada registro é uma cobrança (parcela) de uma locação
@Entity 
@Table (name = "alugueis")
public class Aluguel {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    //Validações de entrada ficam no AluguelInput (api/dto). A locação chega ao service
    //só com o id e é carregada por ele.
    @ManyToOne (optional = false)
    @JoinColumn (name = "id_locacao", nullable = false)
    private Locacao locacao;

    @Column (name = "dt_vencimento", nullable = false)
    private LocalDate dt_vencimento;

    //Nulo significa que a parcela não foi paga
    @Column (name = "valor_pago", precision = 12, scale = 2)
    private BigDecimal valor_pago;

    @Column (length = 500)
    private String obs;

    public Aluguel() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Locacao getLocacao() {
        return locacao;
    }

    public void setLocacao(Locacao locacao) {
        this.locacao = locacao;
    }

    public LocalDate getDt_vencimento() {
        return dt_vencimento;
    }

    public void setDt_vencimento(LocalDate dt_vencimento) {
        this.dt_vencimento = dt_vencimento;
    }

    public BigDecimal getValor_pago() {
        return valor_pago;
    }

    public void setValor_pago(BigDecimal valor_pago) {
        this.valor_pago = valor_pago;
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
        if (!(o instanceof Aluguel outro)) return false;
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Aluguel (id = " + id +
        ", locacao = " + (locacao != null ? locacao.getId() : null) +
        ", vencimento = " + dt_vencimento +
        ", valor pago = " + valor_pago + ")";
    }
}
