import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.NoSuchElementException;

import com.example.TqsStack;

class TqsStackTest {

    @Test
    void testStackComecaVazia() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertTrue(stack.isEmpty(), "A stack devia estar vazia ao início");
    }

    @Test
    void testStackTamanhoZero() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertEquals(0, stack.size(), "A stack devia começar com tamanho 0");
    }

    @Test
    void testAdicionarElementos() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(5);
        stack.push(10);
        assertFalse(stack.isEmpty(), "A stack não devia estar vazia depois de adicionar elementos");
        assertEquals(2, stack.size(), "O tamanho devia ser 2 depois de adicionar dois elementos");
    }

    @Test
    void testPopRetornaX() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(7);
        stack.push(14);
        int removido = stack.pop();
        assertEquals(14, removido, "O pop devia devolver o último elemento inserido");
        assertEquals(1, stack.size(), "O tamanho devia ser 1 depois de remover um elemento");
    }

    @Test
    void testPeekMostraXSemRemover() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(3);
        stack.push(6);
        assertEquals(6, stack.peek(), "O peek devia devolver o último elemento inserido");
        assertEquals(2, stack.size(), "O tamanho devia continuar o mesmo depois de um peek");
    }

    @Test
    void testRemoverTodosElementosStackVazia() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.pop();
        stack.pop();
        stack.pop();
        assertTrue(stack.isEmpty(), "A stack devia estar vazia depois de remover todos os elementos");
        assertEquals(0, stack.size(), "O tamanho devia ser 0 no fim");
    }

    @Test
    void testPopStackVaziaErro() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertThrows(NoSuchElementException.class, stack::pop, "Fazer pop numa stack vazia devia dar erro");
    }

    @Test
    void testPeekStackVaziaErro() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertThrows(NoSuchElementException.class, stack::peek, "Fazer peek numa stack vazia devia dar erro");
    }

    @Test
    void testAdicionarAlemDoLimiteErro() {
        TqsStack<Integer> stack = new TqsStack<>(2);
        stack.push(1);
        stack.push(2);
        assertThrows(IllegalStateException.class, () -> stack.push(3), "Adicionar além do limite devia dar erro");
    }

    @Test
    void testPopTopVerificarRemocao() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);
        assertEquals(3, stack.popTopN(3), "O popTopN devia devolver o terceiro elemento");
        assertEquals(2, stack.size(), "O tamanho devia ser 2 depois de remover 3 elementos");
    }
}
