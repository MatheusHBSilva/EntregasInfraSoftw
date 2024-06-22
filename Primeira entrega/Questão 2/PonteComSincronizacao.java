import java.util.Random;

public class PonteComSincronizacao {

    private static final Object lock = new Object();
    private static boolean liberado = true;
    private static int vez = 0;

    public static void main(String[] args) {
        Thread carroEsquerda = new Thread(new CarroEsquerda());
        Thread carroDireita = new Thread(new CarroDireita());
        Thread tempo = new Thread(new Tempo());

        tempo.start();
        carroEsquerda.start();
        carroDireita.start();
    }
    // Tempo para alternar entre os carros
    static class Tempo implements Runnable {
        public void run() {
            Random gerador = new Random();
            while (true) {
                synchronized (lock) {
                    vez = gerador.nextInt(2); // Gera um número entre 0 e 1 para escolher randomicamente quem entra na ponte (direita ou esquerda)
                    lock.notifyAll();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class CarroEsquerda implements Runnable {
        public void run() {
            while (true) {
                synchronized (lock) {
                    while (vez != 1 || !liberado) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    liberado = false;
                    System.out.println("Carro da esquerda está atravessando a ponte.");
                }

                // Tempo do carro passar na ponte
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lock) {
                    System.out.println("Carro da esquerda atravessou a ponte.");
                    liberado = true;
                    lock.notifyAll();
                }
            }
        }
    }

    static class CarroDireita implements Runnable {
        public void run() {
            while (true) {
                synchronized (lock) {
                    while (vez != 0 || !liberado) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    liberado = false;
                    System.out.println("Carro da direita está atravessando a ponte.");
                }

                // Tempo do carro passar na ponte
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (lock) {
                    System.out.println("Carro da direita atravessou a ponte.");
                    liberado = true;
                    lock.notifyAll();
                }
            }
        }
    }
}
