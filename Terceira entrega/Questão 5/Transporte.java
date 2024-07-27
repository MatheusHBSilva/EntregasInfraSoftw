import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

class PontoDeOnibus {
    private static final int CAPACIDADE_ONIBUS = 50;
    private static final int TOTAL_PASSAGEIROS = 100;
    private Lock lock = new ReentrantLock();
    private Condition onibusChegou = lock.newCondition();
    private Condition onibusPartiu = lock.newCondition();
    private boolean onibusPresente = false;
    private int passageirosEmbarcados = 0;
    private int passageirosEsperando = 0;
    private int passageirosRestantes = TOTAL_PASSAGEIROS;
    private boolean operacaoConcluida = false;

    public class SistemaDeOnibus {
        public static void main(String[] args) {
            PontoDeOnibus pontoDeOnibus = new PontoDeOnibus();
            Random random = new Random();

            Onibus onibus = new Onibus(pontoDeOnibus);
            onibus.start();

            // Criando e iniciando threads de 100 passageiros e simulando o tempo de chegada deles (20 a 70 milésimos)
            for (int i = 1; i <= TOTAL_PASSAGEIROS; i++) {
                new Passageiro(pontoDeOnibus).start();
                try {
                    Thread.sleep(random.nextInt(50) + 35);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void chegarNoPonto(Passageiro passageiro) throws InterruptedException {
        lock.lock();
        try {
            if (passageirosRestantes == 0) {
                return;
            }
            passageirosEsperando++;
            System.out.println("Passageiro " + passageiro.getIdPassageiro() + " chegou na parada e está esperando o ônibus.");
            while (!onibusPresente || passageirosEmbarcados >= CAPACIDADE_ONIBUS) {
                onibusChegou.await();
            }
            passageirosEmbarcados++;
            passageirosEsperando--;
            System.out.println("Passageiro " + passageiro.getIdPassageiro() + " entrou no ônibus.");
            if (passageirosEmbarcados == CAPACIDADE_ONIBUS || passageirosEsperando == 0) {
                onibusPartiu.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void onibusChega() throws InterruptedException {
        lock.lock();
        try {
            if (passageirosRestantes == 0) {
                operacaoConcluida = true;
                return;
            }
            onibusPresente = true;
            passageirosEmbarcados = 0;
            System.out.println("Ônibus chegou.");
            onibusChegou.signalAll();
            while (passageirosEmbarcados < CAPACIDADE_ONIBUS && passageirosEsperando > 0) {
                onibusPartiu.await();
            }
            System.out.println("Ônibus partiu com " + passageirosEmbarcados + " passageiros.");
            passageirosRestantes -= passageirosEmbarcados;
            if (passageirosRestantes == 0) {
                operacaoConcluida = true;
            }
            onibusPresente = false;
        } finally {
            lock.unlock();
        }
    }

    public boolean todosOsPassageirosEntregues() {
        return operacaoConcluida;
    }
}

class Passageiro extends Thread {
    private static int proximoId = 1;
    private int id;
    private PontoDeOnibus pontoDeOnibus;

    public Passageiro(PontoDeOnibus pontoDeOnibus) {
        this.pontoDeOnibus = pontoDeOnibus;
        this.id = proximoId++;
    }

    public int getIdPassageiro() {
        return id;
    }

    public void run() {
        try {
            pontoDeOnibus.chegarNoPonto(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Onibus extends Thread {
    private PontoDeOnibus pontoDeOnibus;
    private Random random = new Random();

    public Onibus(PontoDeOnibus pontoDeOnibus) {
        this.pontoDeOnibus = pontoDeOnibus;
    }

    // Simula a chegada dos ônibus entre 1 e 3 segundos
    public void run() {
        try {
            // Espera inicial aleatória antes do primeiro ônibus chegar
            Thread.sleep(random.nextInt(2000) + 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (!pontoDeOnibus.todosOsPassageirosEntregues()) {
            try {
                Thread.sleep(random.nextInt(2000) + 1000);
                pontoDeOnibus.onibusChega();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Os ônibus encerraram suas operações.");
    }
}
