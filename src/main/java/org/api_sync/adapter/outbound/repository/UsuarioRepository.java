package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByNombre(String nombre);
	Optional<Usuario> findByNombreAndEmpresa(String nombre, Empresa empresa);
}
