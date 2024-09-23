package com.example.ai.aluno;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AlunoService {

    public boolean verificarUsuario(String cpf) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://184.72.80.215:8080/usuario/" + cpf;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

        return responseEntity.getStatusCode().is2xxSuccessful();
    }
}