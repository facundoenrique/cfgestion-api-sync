package org.api_sync.adapter.outbound.repository;

import org.api_sync.adapter.outbound.entities.Usuario;
import org.api_sync.adapter.outbound.entities.gestion.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	/**
	 * Se debe usar solo para rehabilitar un usuario
	 * @param nombre
	 * @return
	 */
	Optional<Usuario> findByNombre(String nombre); //Solo para habilitar user
	Optional<Usuario> findByNombreAndEmpresa(String nombre, Empresa empresa); //Solo para habilitar user
	Optional<Usuario> findByNombreAndEliminado(String nombre, int eliminado);
	Optional<Usuario> findByNombreAndEmpresaAndEliminado(String nombre, Empresa empresa, int eliminado);
}
