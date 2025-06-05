package org.api_sync.adapter.inbound.red;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.api_sync.adapter.inbound.request.preventa.PreventaManualRequestDTO;
import org.api_sync.adapter.inbound.request.preventa.PreventaUpdateDTO;
import org.api_sync.adapter.inbound.request.preventa.PreventaRequestDTO;
import org.api_sync.adapter.inbound.request.preventa.PreventaEstadoDTO;
import org.api_sync.adapter.inbound.responses.PreventaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@Tag(name = "Preventas", description = "API para gestionar preventas")
public interface PreventaApi {

    @Operation(summary = "Listar preventas", description = "Obtiene una lista paginada de preventas con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de preventas obtenida exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros de filtrado inválidos")
    })
    ResponseEntity<Page<PreventaResponseDTO>> findAll(
        @Parameter(description = "Fecha desde (formato: yyyyMMdd)") 
        @DateTimeFormat(pattern = "yyyyMMdd") LocalDate fechaDesde,
        
        @Parameter(description = "Fecha hasta (formato: yyyyMMdd)") 
        @DateTimeFormat(pattern = "yyyyMMdd") LocalDate fechaHasta,
        
        @Parameter(description = "ID del proveedor") 
        Long proveedorId,
        
        @Parameter(description = "Nombre de la preventa") 
        String nombre,
        
        @Parameter(description = "Configuración de paginación") 
        Pageable pageable
    );

    @Operation(summary = "Obtener preventa por ID", description = "Obtiene los detalles de una preventa específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preventa encontrada exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PreventaResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Preventa no encontrada")
    })
    ResponseEntity<?> findById(
        @Parameter(description = "ID de la preventa") 
        Long id,
        
        @Parameter(description = "Atributos específicos a retornar (separados por coma)") 
        String attributes
    );

    @Operation(summary = "Crear preventa", description = "Crea una nueva preventa con artículos seleccionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Preventa creada exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PreventaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de la preventa inválidos")
    })
    ResponseEntity<PreventaResponseDTO> crearPropuesta(
        @Parameter(description = "Datos de la preventa a crear") 
        @Valid PreventaRequestDTO requestDTO
    );

    @Operation(summary = "Crear preventa manual", description = "Crea una nueva preventa con artículos ingresados manualmente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Preventa creada exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PreventaResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de la preventa inválidos")
    })
    ResponseEntity<PreventaResponseDTO> crearPropuestaManual(
        @Parameter(description = "Datos de la preventa manual a crear") 
        @Valid PreventaManualRequestDTO requestDTO
    );

    @Operation(summary = "Actualizar preventa", description = "Actualiza los datos de una preventa existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Preventa actualizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de la preventa inválidos"),
        @ApiResponse(responseCode = "404", description = "Preventa no encontrada")
    })
    ResponseEntity<?> actualizarPreVenta(
        @Parameter(description = "ID de la preventa a actualizar") 
        Long id,
        
        @Parameter(description = "Datos actualizados de la preventa") 
        @Valid PreventaUpdateDTO dto
    );

    @Operation(summary = "Actualizar estado de preventa", description = "Actualiza el estado de una preventa (ABIERTA/CERRADA)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de preventa actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Estado inválido o preventa ya está en ese estado"),
        @ApiResponse(responseCode = "404", description = "Preventa no encontrada")
    })
    ResponseEntity<?> actualizarEstado(
        @Parameter(description = "ID de la preventa") 
        Long id,
        
        @Parameter(description = "Nuevo estado de la preventa") 
        @Valid PreventaEstadoDTO dto
    );
} 