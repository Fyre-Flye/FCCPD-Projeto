package pacote;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Scanner;
import org.json.*;

public class ServerMulticast {
	
	public static void main(String[] args) throws IOException {
		
		String mensagem = " ";
		byte[] envio = new byte[1024];
		LocalDateTime date1 = LocalDateTime.now();
		Scanner sc = new Scanner(System.in);
		
		// Objeto carro : DO LATER
		String modelo;
		String nome;
		String disponivel;
		String tempo;
		
		// Region : Var
		
		JSONArray listModelos = new JSONArray();

		MulticastSocket socket = new MulticastSocket();
		InetAddress ia = InetAddress.getByName("230.0.0.0");

		while (!mensagem.equals("Servidor Encerrado!")) {
			
			int acao = 0;
			
			Scanner action = new Scanner(System.in);
			System.out.print("[Servidor] Escolha a ação a ser tomada: \n "
					+ "1 - Enviar Anuncios | 2 - Emprestimo | 3 - Devolução | 4 - Encerrar Serviço\n");
			
			acao = action.nextInt();
			
			if (acao == 1) {
				System.out.print("[Servidor] Digite a mensagem para anuncios:\n");
				mensagem = sc.nextLine();
				
				JSONObject msgJSON = new JSONObject();
				msgJSON.put("mensagem", mensagem);
				listModelos.put(msgJSON);
				
				mensagem = "[" + date1 + "]" + "\n\n ---- Anuncios! ---- \n\n" + mensagem + "\n\n";
	            for (Object obj : listModelos) {
	            	JSONObject jsonObject = (JSONObject) obj;		
	            	if (jsonObject.has("modelo")) {		
	            		System.out.println(" - " + jsonObject.getString("modelo"));
	            	}
	            }
	            
	            byte[] jsonEnvio = listModelos.toString().getBytes();
				envio = jsonEnvio;
				DatagramPacket pacote = new DatagramPacket(envio, envio.length, ia, 4321);
				socket.send(pacote);

			} else if (acao == 2) {
				System.out.print("[Servidor] Digite a lista de carros disponiveis, FIM para terminar :\n");
				
				// Region : Registrando novos modelos
				
				while (true) {
					JSONObject novoModelo = new JSONObject();
					System.out.print("[Servidor] Digite o modelo do carro :\n");
					mensagem = sc.nextLine();
					
					if (mensagem.equals("FIM")) {
						break;
					}
					
					novoModelo.put("modelo", mensagem);
					listModelos.put(novoModelo);
					
				}
				
				mensagem = "[" + date1 + "]" + "\nLista de carros disponiveis para aluguel!\n" + mensagem;
				
				// Region : Listando Todos os Modelos
				
	            for (Object obj : listModelos) {
	                JSONObject jsonObject = (JSONObject) obj;
	                System.out.println(" - " + jsonObject.getString("modelo"));
	            }
	            
	            // Region : Tratando e enviando novos modelos
	            
	            byte[] jsonEnvio = listModelos.toString().getBytes();
				envio = jsonEnvio;
				
				DatagramPacket pacote = new DatagramPacket(envio, envio.length, ia, 4322);
				socket.send(pacote);

			} else if (acao == 3) {

			} else if (acao == 4) {
				System.out.print("[Servidor] Encerrando serviço de atendimento.\n");
				mensagem = "Servidor Encerrado!";
				socket.close();
			}

		}
	}
}