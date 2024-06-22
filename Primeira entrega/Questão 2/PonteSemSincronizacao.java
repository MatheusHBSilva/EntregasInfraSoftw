public class PonteSemSincronizacao {

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
            while (true) {
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
                System.out.println("Carro da esquerda está atravessando a ponte.");
                atravessar();
                System.out.println("Carro da esquerda atravessou a ponte.");
            }
        }

        // Tempo do carro passar na ponte
        private void atravessar() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class CarroDireita implements Runnable {
        public void run() {
            while (true) {
                System.out.println("Carro da direita está atravessando a ponte.");
                atravessar();
                System.out.println("Carro da direita atravessou a ponte.");
            }
        }

        // Tempo do carro passar na ponte
        private void atravessar() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
