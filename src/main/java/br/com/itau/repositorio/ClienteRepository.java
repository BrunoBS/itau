package br.com.itau.repositorio;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import br.com.itau.modelo.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

	@Lock(LockModeType.OPTIMISTIC)
	Optional<Cliente> findByNumeroContaOrderByIdDesc(Long numeroConta);

	Optional<Cliente> findByNumeroConta(Long numeroConta);

	Optional<Cliente> findTop1ByOrderByIdDesc();

}