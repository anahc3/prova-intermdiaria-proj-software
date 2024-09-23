package com.example.ai.CursoService;

import com.example.ai.aluno.AlunoService;
import com.example.ai.curso.Curso;
import com.example.ai.curso.CursoRepository;
import com.example.ai.curso.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CursoServiceTests {

    @InjectMocks
    private CursoService cursoService;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private AlunoService alunoService;

    private Curso curso;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Criar um curso de exemplo
        curso = new Curso();
        curso.setNome("Curso de Java");
        curso.setDescricao("Curso para iniciantes em Java");
        curso.setNumeroMaximoAlunos(30);
        curso.setCpfProfessor("12345678900");
        curso.setAlunos(new ArrayList<>());
    }

    // Teste para cadastrar curso com sucesso
    @Test
    public void testCadastrarCursoComSucesso() {
        // Mock para simular que o professor existe
        when(alunoService.verificarUsuario(curso.getCpfProfessor())).thenReturn(true);
        when(cursoRepository.save(curso)).thenReturn(curso);

        Curso cursoCadastrado = cursoService.cadastrarCurso(curso);

        assertNotNull(cursoCadastrado);
        assertEquals("Curso de Java", cursoCadastrado.getNome());
        verify(cursoRepository, times(1)).save(curso);
    }

    // Teste para cadastrar curso com professor inexistente
    @Test
    public void testCadastrarCursoProfessorNaoEncontrado() {
        // Mock para simular que o professor não existe
        when(alunoService.verificarUsuario(curso.getCpfProfessor())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cursoService.cadastrarCurso(curso);
        });

        assertEquals("Professor com CPF 12345678900 não encontrado.", exception.getMessage());
        verify(cursoRepository, never()).save(curso);
    }

    // Teste para listar cursos com sucesso (sem filtro de nome)
    @Test
    public void testListarCursosSemFiltro() {
        List<Curso> cursos = new ArrayList<>();
        cursos.add(curso);

        // Mock para simular a listagem de todos os cursos
        when(cursoRepository.findAll()).thenReturn(cursos);

        List<Curso> resultado = cursoService.listarCursos(null);

        assertEquals(1, resultado.size());
        verify(cursoRepository, times(1)).findAll();
    }

    // Teste para listar cursos com filtro de nome
    @Test
    public void testListarCursosComFiltro() {
        List<Curso> cursos = new ArrayList<>();
        cursos.add(curso);

        // Mock para simular a listagem de cursos filtrando pelo nome
        when(cursoRepository.findByNomeContaining("Java")).thenReturn(cursos);

        List<Curso> resultado = cursoService.listarCursos("Java");

        assertEquals(1, resultado.size());
        verify(cursoRepository, times(1)).findByNomeContaining("Java");
    }

    // Teste para adicionar aluno com sucesso
    @Test
    public void testAdicionarAlunoComSucesso() {
        curso.setId("123");
        curso.setNumeroMaximoAlunos(30);

        // Mock para simular a busca do curso e a verificação do aluno
        when(cursoRepository.findById("123")).thenReturn(Optional.of(curso));
        when(alunoService.verificarUsuario("98765432100")).thenReturn(true);
        when(cursoRepository.save(curso)).thenReturn(curso);

        Curso resultado = cursoService.adicionarAluno("98765432100", "123");

        assertNotNull(resultado);
        assertTrue(resultado.getAlunos().contains("98765432100"));
        verify(cursoRepository, times(1)).findById("123");
        verify(cursoRepository, times(1)).save(curso);
    }

    // Teste para adicionar aluno em curso inexistente
    @Test
    public void testAdicionarAlunoCursoNaoEncontrado() {
        when(cursoRepository.findById("123")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cursoService.adicionarAluno("98765432100", "123");
        });

        assertEquals("Curso não encontrado.", exception.getMessage());
        verify(cursoRepository, times(1)).findById("123");
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    // Teste para adicionar aluno quando o curso já está cheio
    @Test
    public void testAdicionarAlunoCursoCheio() {
        curso.setNumeroMaximoAlunos(1); // Curso com limite de 1 aluno
        curso.getAlunos().add("11111111111"); // Já tem 1 aluno no curso

        when(cursoRepository.findById("123")).thenReturn(Optional.of(curso));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cursoService.adicionarAluno("98765432100", "123");
        });

        assertEquals("O curso já atingiu o número máximo de alunos.", exception.getMessage());
        verify(cursoRepository, times(1)).findById("123");
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    // Teste para adicionar aluno que não existe na API externa
    @Test
    public void testAdicionarAlunoNaoEncontrado() {
        when(cursoRepository.findById("123")).thenReturn(Optional.of(curso));
        when(alunoService.verificarUsuario("98765432100")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            cursoService.adicionarAluno("98765432100", "123");
        });

        assertEquals("Aluno com CPF 98765432100 não encontrado.", exception.getMessage());
        verify(cursoRepository, times(1)).findById("123");
        verify(cursoRepository, never()).save(curso);
    }
}
