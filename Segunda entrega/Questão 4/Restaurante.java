import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurante {
    public static void main(String[] args) {
        int capacidade = 5;
        RestauranteSala restauranteSala = new RestauranteSala(capacidade);

        Random rand = new Random();

        for (int i = 1; i <= 100; i++) {
            Thread clienteThread = new Thread(new Cliente(restauranteSala, "Cliente " + i, rand.nextInt(5000) + 1000));
            clienteThread.start();
            try {
                Thread.sleep(rand.nextInt(500) + 500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

class RestauranteSala {
    private final Lock lock = new ReentrantLock();
    private final Condition grupoSaiu = lock.newCondition();
    private final int capacidade;
    private int clientesSentados = 0;
    private final Queue<String> filaDeEspera = new LinkedList<>();

    public RestauranteSala(int capacidade) {
        this.capacidade = capacidade;
    }

    public void entrarRestaurante(String nomeCliente, int tempoJantando) throws InterruptedException {
        lock.lock();
        try {
            while (clientesSentados == capacidade || (clientesSentados > 0 && !filaDeEspera.isEmpty())) {
                System.out.println(nomeCliente + " chegou, mas todas os lugares estão ocupados. Entrando na fila de espera.");
                filaDeEspera.add(nomeCliente);
                grupoSaiu.await();
            }
            clientesSentados++;
            System.out.println(nomeCliente + " sentou-se. Clientes sentados: " + clientesSentados);
        } finally {
            lock.unlock();
        }

        Thread.sleep(tempoJantando);

        lock.lock();
        try {
            clientesSentados--;
            System.out.println(nomeCliente + " terminou de jantar e saiu. Clientes sentados: " + clientesSentados);
            if (clientesSentados == 0) {
                System.out.println("Todos os clientes saíram. O restaurante está vazio agora.");
                while (!filaDeEspera.isEmpty()) {
                    filaDeEspera.poll();
                }
                grupoSaiu.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}

class Cliente implements Runnable {
    private final RestauranteSala restaurante;
    private final String nome;
    private final int tempoJantando;

    public Cliente(RestauranteSala restaurante, String nome, int tempoJantando) {
        this.restaurante = restaurante;
        this.nome = nome;
        this.tempoJantando = tempoJantando;
    }

    public void run() {
        try {
            restaurante.entrarRestaurante(nome, tempoJantando);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
