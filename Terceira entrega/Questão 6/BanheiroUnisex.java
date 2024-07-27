import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BanheiroUnisex {
    public static void main(String[] args) {
        int capacidade = 3;
        Banheiro banheiro = new Banheiro(capacidade);

        Queue<Pessoa> fila = new LinkedList<>();
        Random rand = new Random();
        for (int i = 1; i <= 100; i++) {
            boolean ehMulher = rand.nextBoolean(); // Gênero aleatório
            String nome = (ehMulher ? "M" : "H") + i;
            fila.add(new Pessoa(banheiro, nome, ehMulher));
        }
        // Cria e inicia as threads para cada pessoa na fila
        for (Pessoa pessoa : fila) {
            new Thread(pessoa).start();
            try {
                Thread.sleep(rand.nextInt(400) + 500); // Tempo aleatório para a próxima pessoa
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Banheiro {
    private final Lock lock = new ReentrantLock();
    private final Condition condicao = lock.newCondition();
    private final int capacidade;
    private int numMulheres = 0;
    private int numHomens = 0;
    private boolean mulherDentro = false;
    private boolean homemDentro = false;
    private int totalDentro = 0;
    private Queue<Pessoa> fila = new LinkedList<>();

    public Banheiro(int capacidade) {
        this.capacidade = capacidade;
    }

    public void entrar(Pessoa pessoa) throws InterruptedException {
        lock.lock();
        try {
            fila.add(pessoa);

            // Aguarda se não é possível entrar
            while (!podeEntrar()) {
                condicao.await();
            }

            fila.poll();

            // Atualiza o estado do banheiro e a fila
            if (pessoa.isEhMulher()) {
                numMulheres++;
                mulherDentro = true;
            } else {
                numHomens++;
                homemDentro = true;
            }
            totalDentro++;
            System.out.println("Entra " + pessoa.getNome() + " - Status: " + totalDentro + " no banheiro. " +
                               numMulheres + " mulheres, " + numHomens + " homens");

            condicao.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void sair(Pessoa pessoa) {
        lock.lock();
        try {
            if (pessoa.isEhMulher()) {
                numMulheres--;
                if (numMulheres == 0) {
                    mulherDentro = false;
                }
            } else {
                numHomens--;
                if (numHomens == 0) {
                    homemDentro = false;
                }
            }
            totalDentro--;
            System.out.println("Sai " + pessoa.getNome() + " - Status: " + totalDentro + " no banheiro. " +
                               numMulheres + " mulheres, " + numHomens + " homens");

            condicao.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean podeEntrar() {
        if (totalDentro >= capacidade) {
            return false;
        }
        // Verifica se a fila está vazia ou se a próxima pessoa pode entrar
        if (fila.isEmpty()) {
            return true;
        }
        Pessoa proximaPessoa = fila.peek();
        if (proximaPessoa.isEhMulher()) {
            return !homemDentro;
        } else {
            return !mulherDentro;
        }
    }
}

class Pessoa implements Runnable {
    private final Banheiro banheiro;
    private final String nome;
    private final boolean ehMulher;

    public Pessoa(Banheiro banheiro, String nome, boolean ehMulher) {
        this.banheiro = banheiro;
        this.nome = nome;
        this.ehMulher = ehMulher;
    }

    public String getNome() {
        return nome;
    }

    public boolean isEhMulher() {
        return ehMulher;
    }

    public void run() {
        try {
            banheiro.entrar(this);

            // Simula o tempo que a pessoa fica dentro do banheiro
            Thread.sleep((long) (Math.random() * 5000));

            banheiro.sair(this);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}