package com.epr.backend.controller;

import com.epr.backend.dto.request.PagoRequest;
import com.epr.backend.dto.request.RechazarComprobanteRequest;
import com.epr.backend.dto.response.ComprobantePagoArchivoResponse;
import com.epr.backend.dto.response.ComprobantePagoResponse;
import com.epr.backend.entity.EstadoComprobante;
import com.epr.backend.service.ComprobantePagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comprobantes-pago")
@RequiredArgsConstructor
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComprobantePagoResponse> subir(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "planCuotaId", required = false) Long planCuotaId,
            @RequestParam(value = "monto", required = false) BigDecimal monto,
            @RequestParam(value = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        ComprobantePagoResponse response = comprobantePagoService.subir(
                authentication.getName(), archivo, planCuotaId, monto, fecha);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mios")
    public ResponseEntity<List<ComprobantePagoResponse>> mios(Authentication authentication) {
        return ResponseEntity.ok(comprobantePagoService.listarMios(authentication.getName()));
    }

    @GetMapping("/mios/{id}/archivo")
    public ResponseEntity<byte[]> descargarMio(@PathVariable Long id, Authentication authentication) {
        ComprobantePagoArchivoResponse archivo = comprobantePagoService.descargarMio(authentication.getName(), id);
        return construirDescarga(archivo);
    }

    @GetMapping
    public ResponseEntity<List<ComprobantePagoResponse>> listar(
            @RequestParam(required = false) EstadoComprobante estado) {
        return ResponseEntity.ok(comprobantePagoService.listarPorEstado(estado));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<ComprobantePagoResponse> confirmar(@PathVariable Long id,
                                                               @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(comprobantePagoService.confirmar(id, request));
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<ComprobantePagoResponse> rechazar(@PathVariable Long id,
                                                              @RequestBody(required = false) RechazarComprobanteRequest request) {
        return ResponseEntity.ok(comprobantePagoService.rechazar(id, request));
    }

    private ResponseEntity<byte[]> construirDescarga(ComprobantePagoArchivoResponse archivo) {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(archivo.nombreArchivo(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(archivo.contenido());
    }
}
