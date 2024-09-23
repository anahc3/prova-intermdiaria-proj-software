package com.example.ai.curso;

import com.example.ai.aluno.AlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AlunoService alunoService;

    public Curso cadastrarCurso(Curso curso) {
        if (alunoService.verificarUsuario(curso.getCpfProfessor())) {
            return cursoRepository.save(curso);
        } else {
            throw new RuntimeException("Professor com CPF " + curso.getCpfProfessor() + " não encontrado.");
        }
    }

    public List<Curso> listarCursos(String nome) {
        if (nome != null && !nome.isEmpty()) {
            return cursoRepository.findByNomeContaining(nome);
        } else {
            return cursoRepository.findAll();
        }
    }

    public Curso adicionarAluno(String cpfAluno, String cursoId) {
        Optional<Curso> cursoOptional = cursoRepository.findById(cursoId);
        if (!cursoOptional.isPresent()) {
            throw new RuntimeException("Curso não encontrado.");
        }

        Curso curso = cursoOptional.get();

        if (curso.getAlunos().size() >= curso.getNumeroMaximoAlunos()) {
            throw new RuntimeException("O curso já atingiu o número máximo de alunos.");
        }

        if (alunoService.verificarUsuario(cpfAluno)) {
            curso.getAlunos().add(cpfAluno);
            return cursoRepository.save(curso);
        } else {
            throw new RuntimeException("Aluno com CPF " + cpfAluno + " não encontrado.");
        }
    }
}
