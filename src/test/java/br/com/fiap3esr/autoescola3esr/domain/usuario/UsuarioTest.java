package br.com.fiap3esr.autoescola3esr.domain.usuario;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {
    @Test
    void deveExporPerfilComoAutoridadeSpringSecurity() {
        Usuario usuario = new Usuario(null, "admin@autoescola.com", "hash", Role.ADMIN);
        assertThat(usuario.getUsername()).isEqualTo("admin@autoescola.com");
        assertThat(usuario.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void devePermitirAlterarSenhaCriptografada() {
        Usuario usuario = new Usuario(null, "aluno@autoescola.com", "hash-antigo", Role.USER);
        usuario.alterarSenha("hash-novo");
        assertThat(usuario.getPassword()).isEqualTo("hash-novo");
    }
}
