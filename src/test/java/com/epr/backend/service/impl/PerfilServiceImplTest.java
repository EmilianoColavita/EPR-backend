package com.epr.backend.service.impl;

import com.epr.backend.entity.FotoPerfil;
import com.epr.backend.entity.Rol;
import com.epr.backend.entity.Usuario;
import com.epr.backend.exception.BadRequestException;
import com.epr.backend.repository.FotoPerfilRepository;
import com.epr.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PerfilServiceImplTest {

    private static final String EMAIL = "ana@epr.com";
    private static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0};
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0};
    private static final byte[] WEBP = {'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'};

    private UsuarioRepository usuarioRepository;
    private FotoPerfilRepository fotoPerfilRepository;
    private PerfilServiceImpl service;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        fotoPerfilRepository = mock(FotoPerfilRepository.class);
        service = new PerfilServiceImpl(usuarioRepository, fotoPerfilRepository);

        Usuario usuario = Usuario.builder().id(1L).nombre("Ana").apellido("Gómez").email(EMAIL).rol(Rol.ALUMNO).build();
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(fotoPerfilRepository.findByUsuarioEmail(EMAIL)).thenReturn(Optional.empty());
    }

    @Test
    void guardaFotosValidasConElContentTypeDetectado() {
        for (Object[] caso : new Object[][]{{"image/png", PNG}, {"image/jpeg", JPEG}, {"image/webp", WEBP}}) {
            String tipo = (String) caso[0];
            byte[] bytes = (byte[]) caso[1];

            service.subirFoto(EMAIL, new MockMultipartFile("archivo", "f", tipo, bytes));

            ArgumentCaptor<FotoPerfil> captor = ArgumentCaptor.forClass(FotoPerfil.class);
            verify(fotoPerfilRepository, org.mockito.Mockito.atLeastOnce()).save(captor.capture());
            FotoPerfil guardada = captor.getValue();
            assertEquals(tipo, guardada.getContentType());
            assertArrayEquals(bytes, guardada.getArchivo());
        }
    }

    @Test
    void rechazaContentTypeNoPermitido() {
        var archivo = new MockMultipartFile("archivo", "f.gif", "image/gif", PNG);
        assertThrows(BadRequestException.class, () -> service.subirFoto(EMAIL, archivo));
        verify(fotoPerfilRepository, never()).save(any());
    }

    @Test
    void rechazaArchivoQueDeclaraImagenPeroNoLoEs() {
        var archivo = new MockMultipartFile("archivo", "f.png", "image/png", "<html>hola</html>".getBytes());
        assertThrows(BadRequestException.class, () -> service.subirFoto(EMAIL, archivo));
        verify(fotoPerfilRepository, never()).save(any());
    }

    @Test
    void rechazaArchivoVacioOAusente() {
        assertThrows(BadRequestException.class,
                () -> service.subirFoto(EMAIL, new MockMultipartFile("archivo", "f.png", "image/png", new byte[0])));
        assertThrows(BadRequestException.class, () -> service.subirFoto(EMAIL, null));
    }

    @Test
    void rechazaFotoMayorA3MB() {
        byte[] grande = new byte[3 * 1024 * 1024 + 1];
        System.arraycopy(PNG, 0, grande, 0, PNG.length);
        var archivo = new MockMultipartFile("archivo", "f.png", "image/png", grande);
        assertThrows(BadRequestException.class, () -> service.subirFoto(EMAIL, archivo));
        verify(fotoPerfilRepository, never()).save(any());
    }

    @Test
    void reemplazaLaFotoExistenteSinCrearOtra() {
        FotoPerfil existente = FotoPerfil.builder().contentType("image/png").archivo(PNG).build();
        when(fotoPerfilRepository.findByUsuarioEmail(EMAIL)).thenReturn(Optional.of(existente));

        service.subirFoto(EMAIL, new MockMultipartFile("archivo", "f.jpg", "image/jpeg", JPEG));

        verify(fotoPerfilRepository).save(existente);
        assertEquals("image/jpeg", existente.getContentType());
        assertArrayEquals(JPEG, existente.getArchivo());
    }
}
