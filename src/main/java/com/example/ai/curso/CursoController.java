package com.example.ai.curso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curso")
public class CursoController {
    @Autowired
    private CursoService cursoService;

    @PostMapping("/cadastrar")
    public Curso cadastrarCurso(@RequestBody Curso curso) {
        return cursoService.cadastrarCurso(curso);
    }

    @GetMapping("/listar")
    public List<Curso> listarCursos(@RequestParam(required = false) String nome) {
        return cursoService.listarCursos(nome);
    }

    @PostMapping("/adicionarAluno")
    public Curso adicionarAluno(@RequestParam String cpfAluno, @RequestParam String cursoId) {
        return cursoService.adicionarAluno(cpfAluno, cursoId);
    }
}
