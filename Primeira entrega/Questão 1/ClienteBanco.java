import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClienteBanco {
    public static void main(String[] args) {
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
            
            int serverPort = 2409;
            String serverName = "localhost";
            InetAddress serverAddress = InetAddress.getByName(serverName);
            
            Scanner scanner = new Scanner(System.in);

            String mensagem = """
                    Bem vindo(a) ao banco de New City
                    Existem três comandos disponíveis
                    Sacar
                    Depositar
                    Ver saldo""";
            
            System.out.println(mensagem);
           
            String stop = "a";

            while(!stop.equals("Parar")){
                System.out.print(">> ");
                String message = scanner.nextLine();
                byte[] sendData = message.getBytes();
                
                // Envia a mensagem pelo socket criado.
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);
                
                // Recebe as respostas do servidor.
                byte[] receiveData = new byte[2048];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String reply = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Resposta recebida: " + reply);

                stop = message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }
}
