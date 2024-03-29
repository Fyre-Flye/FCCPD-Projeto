package pacote;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;

public class ClientMulticast implements Runnable{
   private int var;
   public ClientMulticast(int entrada) {
	   var = entrada;
   }
   
   public void run() {
	   try {
		   String msg = " ";	      
		      MulticastSocket socket=new MulticastSocket(var);
		      
		      InetAddress ia =InetAddress.getByName("230.0.0.0");
		      InetSocketAddress grupo = new InetSocketAddress(ia , var);
		      NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
		      
		      socket.joinGroup(grupo,ni);
		      
		      System.out.println("Digite o seu nome: ");
		      String nome;
		      Scanner name = new Scanner(System.in);
		      nome = name.nextLine();
		      
		      while(!msg.contains("Servidor Encerrado!")){
		         System.out.println("[ " + nome + " ] Conectando ao servidor");
		         byte[] buffer = new byte[1024];
		         
		         DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
		         socket.receive(packet);
		         
		         msg=new String(packet.getData());
		         System.out.println("[ " + nome + " ] " + msg);
		       
		      }
		      System.out.println("[ " + nome + " ] Conexao com o servidor encerrada!");
		      socket.leaveGroup(grupo, ni);
		      socket.close();
	   }catch(Exception e) {
		   System.out.println("Erro");
		   return;
	   }
   }
	
   public static void main(String[] args) throws IOException {	   
	       
	      System.out.println("Escolha o topico desejado:");
	      System.out.println("1 -Anuncios | 2 - Alugar carro | 3 - Devolver carro");
	      int entrada;
	      Scanner entry = new Scanner(System.in);
	      entrada = entry.nextInt();
	      if(entrada == 1) {
	    	  ClientMulticast client1 = new ClientMulticast(4321);
	    	  new Thread(client1).start();
		      
	      }else if(entrada == 2) {
	    	  ClientMulticast client2 = new ClientMulticast(4322);
	    	  new Thread(client2).start();
		      
	      }else if(entrada == 3) {

	      }
	      
	   
   }

  
}