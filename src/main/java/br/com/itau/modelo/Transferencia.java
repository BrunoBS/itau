package br.com.itau.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
public class Transferencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@SequenceGenerator(name = "SECQNAMEINENTITY", sequenceName = "DB_SEC_TRANSFERENCIA", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECQNAMEINENTITY")
	@Id
	@Getter
	@JsonIgnore
	private Long id;

	@Version
	@Getter
	@JsonIgnore
	private Long version;

	@Getter
	@Setter
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime data;

	@Getter
	@Setter
	private Long contaOrigem;

	@Getter
	@Setter
	private Long contaDestino;
	@Getter
	@Setter
	private BigDecimal valor;
	
	@Getter
	@Setter
	private String observacao;
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private OperacaoType operacaoType;

	@Getter
	@Setter
	private Boolean sucesso;
}
