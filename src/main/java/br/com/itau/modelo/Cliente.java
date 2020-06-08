package br.com.itau.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Cliente implements Serializable {
	@Autowired

	private static final long serialVersionUID = 1L;

	@SequenceGenerator(name = "SECQNAMEINENTITY", sequenceName = "DB_SEC_CLIENTE", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECQNAMEINENTITY")
	@Id
	@Getter
	@Setter
	@ApiModelProperty(hidden = true)
	private Long id;

	@Version
	@Getter
	@JsonIgnore
	private Long version;

	@Getter
	@Setter
	@NotBlank
	private String nome;

	@Getter
	@Setter
	@ApiModelProperty(hidden = true)
	private Long numeroConta;

	@Getter
	@Setter
	@NotNull
	private BigDecimal saldo;

	public void transferePara(BigDecimal valor, Cliente cliente) {

		this.saca(valor);
		cliente.deposita(valor);
	}

	public void deposita(BigDecimal valor) {
		this.saldo = this.saldo.add(valor);
	}

	public void saca(BigDecimal valor) {
		this.saldo = this.saldo.subtract(valor);
	}

}
