package com.epr.backend.util;

import com.epr.backend.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Validación común de archivos subidos. El Content-Type y la extensión los declara el cliente y se
 * pueden falsificar, así que el tipo se determina por la firma real del contenido (magic bytes) y
 * es ese tipo detectado el que se guarda y se devuelve al descargar.
 */
public final class ArchivoSubido {

    public static final String PDF = "application/pdf";
    public static final String JPEG = "image/jpeg";
    public static final String PNG = "image/png";
    public static final String WEBP = "image/webp";

    private static final int LONGITUD_MAXIMA_NOMBRE = 200;

    private ArchivoSubido() {
    }

    /**
     * Lee el archivo y verifica que su contenido real sea de alguno de los tipos permitidos.
     *
     * @return el contenido y el content-type detectado
     */
    public static Contenido leerYValidar(MultipartFile archivo, Set<String> tiposPermitidos, long tamanioMaximoBytes,
                                         String mensajeTipoInvalido) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("El archivo es requerido");
        }
        // Si el cliente declara un tipo concreto que no está permitido se rechaza directamente; si no
        // declara nada útil (vacío u octet-stream) decide solo la firma del contenido.
        String declarado = archivo.getContentType();
        if (declarado != null && !declarado.isBlank()
                && !declarado.equalsIgnoreCase("application/octet-stream")
                && !tiposPermitidos.contains(declarado.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException(mensajeTipoInvalido);
        }
        if (archivo.getSize() > tamanioMaximoBytes) {
            throw new BadRequestException("El archivo no puede superar los %d MB"
                    .formatted(tamanioMaximoBytes / (1024 * 1024)));
        }

        byte[] bytes;
        try {
            bytes = archivo.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo");
        }

        String tipo = detectarContentType(bytes);
        if (tipo == null || !tiposPermitidos.contains(tipo)) {
            throw new BadRequestException(mensajeTipoInvalido);
        }
        return new Contenido(bytes, tipo);
    }

    public static String detectarContentType(byte[] b) {
        if (b.length >= 5 && b[0] == '%' && b[1] == 'P' && b[2] == 'D' && b[3] == 'F' && b[4] == '-') {
            return PDF;
        }
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return JPEG;
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G'
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A) {
            return PNG;
        }
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return WEBP;
        }
        return null;
    }

    /**
     * Nombre de archivo apto para guardar y mandar en Content-Disposition: sin rutas, sin caracteres
     * de control y dentro del largo de la columna.
     */
    public static String nombreSeguro(String original, String porDefecto) {
        if (original == null || original.isBlank()) {
            return porDefecto;
        }
        String nombre = original.replace('\\', '/');
        nombre = nombre.substring(nombre.lastIndexOf('/') + 1)
                .replaceAll("\\p{Cntrl}", "")
                .trim();
        if (nombre.isEmpty()) {
            return porDefecto;
        }
        if (nombre.length() > LONGITUD_MAXIMA_NOMBRE) {
            int punto = nombre.lastIndexOf('.');
            String extension = punto > 0 && nombre.length() - punto <= 10 ? nombre.substring(punto) : "";
            nombre = nombre.substring(0, LONGITUD_MAXIMA_NOMBRE - extension.length()) + extension;
        }
        return nombre;
    }

    public record Contenido(byte[] bytes, String contentType) {
    }
}
