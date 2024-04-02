package pacote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClientMulticast implements Runnable {
	
	private String nome;
	private int var;
	private int action;

	public ClientMulticast(int entrada, int acao) {
		var = entrada;
		action = acao;
	}

	public void run() {
		try {
			// Region : Socket Config
			String msgServidor = " ";
			MulticastSocket socket = new MulticastSocket(var);
			byte[] buffer = new byte[1024];
			InetAddress ia = InetAddress.getByName("230.0.0.0");
			InetSocketAddress grupo = new InetSocketAddress(ia, var);
			NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
			socket.joinGroup(grupo, ni);
			
			// Region : Start
			
			System.out.println("Digite o seu nome: ");
			Scanner scan = new Scanner(System.in);
			
			this.nome = scan.nextLine();

			if (this.action == 1) {
				System.out.println("[ " + nome + " ] Conectando ao servidor");

				// Region: Tratamento do Pacote
				
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				msgServidor = new String(packet.getData());
				
				JSONArray jsonServidor = new JSONArray(msgServidor);
		
	            for (Object obj : jsonServidor) {
	            	JSONObject jsonObject = (JSONObject) obj;		
	            	if (jsonObject.has("mensagem")) {	            		
	            		System.out.println("MENSAGEM PARA ANÚNCIOS : " + jsonObject.getString("mensagem"));
	            	} else if (jsonObject.has("modelo")) {		
	            		System.out.println(" - " + jsonObject.getString("modelo"));
	            	}
	            }

			} else if (this.action == 2) {
				String escolha;
				String modeloNome;
				String tempo;
				System.out.println("[ " + nome + " ] Conectando ao servidor");

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				msgServidor = new String(packet.getData());
				System.out.println("[ " + nome + " ] " + msgServidor);
				System.out.println("[ " + nome + " ] Escolha o carro desejado para alugar:\n");
				Scanner choice = new Scanner(System.in);
				escolha = choice.nextLine();
				System.out.println("[ " + nome + " ] Carro escolhido: " + escolha + ":\n");

			} else if (this.action == 3) { // TODO
				String escolha;
				System.out.println("[ " + nome + " ] Escolha o carro desejado para alugar:\n");
				Scanner choice = new Scanner(System.in);
				escolha = choice.nextLine();
				System.out.println("[ " + nome + " ] Carro escolhido: " + escolha + ":\n");
				buffer = escolha.getBytes();

				DatagramPacket packet2 = new DatagramPacket(buffer, buffer.length, ia, 4323);
				socket.send(packet2);
			} else if (this.action == 4) {				
				System.out.println("[ " + nome + " ] Conexao com o servidor encerrada!");
				socket.leaveGroup(grupo, ni);
				socket.close();
			} 
			main(null);

		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Escolha o topico desejado:");
		System.out.println("1 - Anuncios | 2 - Alugar carro | 3 - Devolver carro | 4 - Encerrar");
		int entrada;
		Scanner entry = new Scanner(System.in);
		entrada = entry.nextInt();
		if (entrada == 1) {
			ClientMulticast client1 = new ClientMulticast(4321, entrada);
			client1.action = entrada;
			new Thread(client1).start();

		} else if (entrada == 2) {
			ClientMulticast client2 = new ClientMulticast(4322, entrada);
			client2.action = entrada;
			new Thread(client2).start();

		} else if (entrada == 3) {
			ClientMulticast client3 = new ClientMulticast(4323, entrada);
			client3.action = entrada;
			new Thread(client3).start();
		}
	}
}