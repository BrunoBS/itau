package br.com.itau.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.itau.modelo.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

	List<Transferencia> findByContaOrigemOrderByDataDesc(Long numeroConta);

}