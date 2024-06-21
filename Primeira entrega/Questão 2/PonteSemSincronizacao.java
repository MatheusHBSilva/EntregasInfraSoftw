public class PonteSemSincronizacao {

    public static void main(String[] args) {
        Thread carroEsquerda = new Thread(new CarroEsquerda());
        Thread carroDireita = new Thread(new CarroDireita());
        Thread tempo = new Thread(new Tempo());

        tempo.start();
        carroEsquerda.start();
        carroDireita.start();
    }

    static class Tempo implements Runnable {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000); // Espera um tempo antes de alterar a vez novamente
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

        private void atravessar() {
            try {
                Thread.sleep(3000); // Simula o tempo de travessia
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

        private void atravessar() {
            try {
                Thread.sleep(3000); // Simula o tempo de travessia
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
