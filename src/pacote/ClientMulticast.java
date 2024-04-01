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
   private int action;
   public ClientMulticast(int entrada, int acao) {
	   var = entrada;
	   action = acao;
   }
   
   public void run() {
	   try {
		   String msg = " ";	      
		      MulticastSocket socket=new MulticastSocket(var);
		      byte[] buffer = new byte[1024];
		      InetAddress ia =InetAddress.getByName("230.0.0.0");
		      InetSocketAddress grupo = new InetSocketAddress(ia , var);
		      NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
		      
		      socket.joinGroup(grupo,ni);
		      
		      System.out.println("Digite o seu nome: ");
		      String nome;
		      Scanner name = new Scanner(System.in);
		      nome = name.nextLine();
		      
		      if(action == 1) {
		    	  System.out.println("[ " + nome + " ] Conectando ao servidor");
			         
			      DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
			      socket.receive(packet);
			         
			      msg=new String(packet.getData());
			      System.out.println("[ " + nome + " ] " + msg);
			      
		      }else if(action == 2) {
		    	  String escolha;
		    	  String tempo;
		    	  System.out.println("[ " + nome + " ] Conectando ao servidor");				         				         
				         
		    	  DatagramPacket packet=new DatagramPacket(buffer,buffer.length);			      
		    	  socket.receive(packet);
		    		  
		    	  msg=new String(packet.getData());				      
		    	  System.out.println("[ " + nome + " ] " + msg);				         				      
		    	  System.out.println("[ " + nome + " ] Escolha o carro desejado para alugar:\n");				      				      
		    	  Scanner choice = new Scanner(System.in);				      
		    	  escolha = choice.nextLine();				      
		    	  System.out.println("[ " + nome + " ] Carro escolhido: " + escolha + ":\n");
		    	  
		      }else if(action == 3) {				         
		    	  String escolha;
		    	  System.out.println("[ " + nome + " ] Escolha o carro desejado para alugar:\n");				      				      
		    	  Scanner choice = new Scanner(System.in);				      
		    	  escolha = choice.nextLine();				      
		    	  System.out.println("[ " + nome + " ] Carro escolhido: " + escolha + ":\n");
				  buffer = escolha.getBytes();
				  
		    	  DatagramPacket packet2 = new DatagramPacket(buffer, buffer.length,ia, 4323);				      			    		 
		    	  socket.send(packet2);				         
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
	      System.out.println("1 -Anuncios | 2 - Alugar carro | 3 - Devolver carro | 4 - Encerrar");
	      int entrada;
	      Scanner entry = new Scanner(System.in);
	      entrada = entry.nextInt();
		  if(entrada == 1) {
			  ClientMulticast client1 = new ClientMulticast(4321, entrada);
		      new Thread(client1).start();
			      
		  }else if(entrada == 2) {
		      ClientMulticast client2 = new ClientMulticast(4322, entrada);
		      new Thread(client2).start();
			      
		  }else if(entrada == 3) {
		      ClientMulticast client3 = new ClientMulticast(4323, entrada);
		      new Thread(client3).start();
		  }
   	}
}