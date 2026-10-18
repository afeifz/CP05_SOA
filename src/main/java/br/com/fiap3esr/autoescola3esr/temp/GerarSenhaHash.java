package br.com.fiap3esr.autoescola3esr.temp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarSenhaHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaHash = encoder.encode("password");
        System.out.println(senhaHash);
    }
}