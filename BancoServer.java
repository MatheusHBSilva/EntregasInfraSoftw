import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BancoServer {
    private static final int PORT = 2409;
    private static final String SERVER_NAME = "localhost";
    public static int saldo = 1000;
    
    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT, InetAddress.getByName(SERVER_NAME));
            System.out.println("The server is ready to receive");
            while (true) {
                new Thread(new HandleRequestUdp(serverSocket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class HandleRequestUdp implements Runnable {
    private DatagramSocket serverSocket;
    

    public HandleRequestUdp(DatagramSocket socket) {
        this.serverSocket = socket;
    }

    @Override

    public void run() {
        try {
            /*Os comandos serão: Sacar, Depositar, Ver saldo */
            /*Cada cliente vai poder se comunicar infinitamente caso eles queiram*/
            while(true){
            byte[] receiveData = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String req = new String(receivePacket.getData(), 0, receivePacket.getLength());
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            
            System.out.println("Requisicao recebida de " + clientAddress + ":" + clientPort);
            
            String rep = null;

            if(req.contains("Depositar")){
                String[] inputs = req.split(" ");
                int val = Integer.parseInt(inputs[1]);

                BancoServer.saldo += val;
                rep = "Você adicionou " + val + " ao seu saldo total, totalizando " + BancoServer.saldo;
            }
            else if(req.contains("Ver saldo")){
                rep = "Seu saldo é de: " + BancoServer.saldo;
            } 
            else if(req.contains("Sacar")){
                String[] inputs1 = req.split(" ");
                int val1 = Integer.parseInt(inputs1[1]);

                if(val1 <= BancoServer.saldo){
                    rep = "Retirado: " + val1 + "reais";
                    BancoServer.saldo -= val1;
                }
                else{
                    rep = "Você nao tem saldo para retirar, deposite algum dinheiro para desbloquear a funçao";
                }
            }
            else{
                rep = "Solicitação nao entendida";
            }
            byte[] sendData = rep.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
            serverSocket.send(sendPacket);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
