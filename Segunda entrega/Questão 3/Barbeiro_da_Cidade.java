import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Barbeiro_da_Cidade {
    public static void main(String[] args) {
        int capacidade = 5;
        Barbearia barbearia = new Barbearia(capacidade);
        Thread barbeiroThread = new Thread(new Barbeiro(barbearia));
        Random rand = new Random();
        barbeiroThread.start();

        for (int i = 1; i <= 100; i++) {
            Thread clienteThread = new Thread(new Pessoa(barbearia, i));
            clienteThread.start();
            try {
                Thread.sleep(rand.nextInt(400) + 500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class Barbearia {
    private final Lock lock = new ReentrantLock();
    private final Condition barbeiroDormindo = lock.newCondition();
    private final Condition cadeirasDisponiveis = lock.newCondition();
    private int cadeirasOcupadas = 0;
    private final int capacidade;

    public Barbearia(int capacidade) {
        this.capacidade = capacidade;
    }

    public void cortarCabelo() throws InterruptedException {
        lock.lock();
        try {
            while (cadeirasOcupadas == 0) {
                System.out.println("Barbeiro dormindo!");
                barbeiroDormindo.await();
            }
            cadeirasOcupadas--;
            System.out.println("Barbeiro está cortando cabelo. Cadeiras ocupadas: " + cadeirasOcupadas);
            cadeirasDisponiveis.signal();
        } finally {
            lock.unlock();
        }
    }

    public void entrarBarbearia(int pessoa_num) throws InterruptedException {
        lock.lock();
        try {
            if (cadeirasOcupadas == capacidade) {
                System.out.println("Pessoa " + pessoa_num + " chegou, mas todas as cadeiras estão ocupadas.");
                return;
            }
            cadeirasOcupadas++;
            System.out.println("Pessoa " + pessoa_num + " sentou. Cadeiras ocupadas: " + cadeirasOcupadas);
            barbeiroDormindo.signal();
            cadeirasDisponiveis.await();
            System.out.println("Pessoa " + pessoa_num + " está cortando o cabelo");
        } finally {
            lock.unlock();
        }
    }
}

class Barbeiro implements Runnable {
    private final Barbearia barbearia;

    public Barbeiro(Barbearia barbearia) {
        this.barbearia = barbearia;
    }

    public void run() {
        try {
            while (true) {
                barbearia.cortarCabelo();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Pessoa implements Runnable {
    private final Barbearia barbearia;
    private final int pessoa_num;

    public Pessoa(Barbearia barbearia, int Pessoa_num) {
        this.barbearia = barbearia;
        this.pessoa_num = Pessoa_num;
    }

    public void run() {
        try {
            barbearia.entrarBarbearia(pessoa_num);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
