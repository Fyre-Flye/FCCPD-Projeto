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

		String msg = " ";
		byte[] envio = new byte[1024];
		byte[] buffer = new byte[1024];
		LocalDateTime date1 = LocalDateTime.now();
		Scanner sc = new Scanner(System.in);
		JSONArray jsonServidor;

		// Region : Socket Config and JSONArray

		JSONArray listModelos = new JSONArray();

		MulticastSocket socket = new MulticastSocket();
		InetAddress ia = InetAddress.getByName("230.0.0.0");

		while (!msg.equals("Servidor Encerrado!")) {

			int acao = 0;

			Scanner action = new Scanner(System.in);
			System.out.println("[Servidor] Escolha a ação a ser tomada: \n "
					+ "1 - Enviar Anuncios | 2 - Registrar Veículos | 3 - Realizar Empréstimo | 4 - Proceder Devolução | 5 - Encerrar Serviço\n");

			acao = action.nextInt();

			if (acao == 1) { // Enviar Anuncios
				System.out.print("[Servidor] Digite a mensagem para anuncios:\n");
				msg = sc.nextLine();

				JSONObject msgJSON = new JSONObject();
				msgJSON.put("msg", msg);
				listModelos.put(msgJSON);

				msg = "[" + date1 + "]" + "\n\n --- Anuncios! --- \n\n" + msg + "\n\n";
				for (Object obj : listModelos) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("modelo")) {
						System.out.println("Modelo : " + jsonObject.getString("modelo"));
					}
					if (jsonObject.has("disponibilidade")) {
						System.out.println(" Disponibilidade : " + jsonObject.getString("disponibilidade"));
					}
				}

				byte[] jsonEnvio = listModelos.toString().getBytes();
				envio = jsonEnvio;
				DatagramPacket pacote = new DatagramPacket(envio, envio.length, ia, 4321);
				socket.send(pacote);

			} else if (acao == 2) { // Registrar Veiculos
				System.out.println("[Servidor] Digite a lista de carros que queira registrar, FIM para terminar :");

				// Region : Registrando novos modelos

				while (true) {
					JSONObject novoModelo = new JSONObject();
					System.out.println("[Servidor] Digite o modelo do carro :\n");
					msg = sc.nextLine();

					if (msg.equals("FIM")) {
						break;
					}

					novoModelo.put("modelo", msg);
					novoModelo.put("nome", "N/A");
					novoModelo.put("disponibilidade", "disponivel");
					novoModelo.put("tempo", "N/A");
					listModelos.put(novoModelo);

				}

				// Region : Listando Todos os Modelos

				for (Object obj : listModelos) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("modelo")) {
						System.out.println("Modelo : " + jsonObject.getString("modelo"));
					}
					if (jsonObject.has("disponibilidade")) {
						System.out.println(" Disponibilidade : " + jsonObject.getString("disponibilidade"));
					}
				}

				// Region : Tratando e enviando novos modelos

				byte[] jsonEnvio = listModelos.toString().getBytes();
				envio = jsonEnvio;

				DatagramPacket pacoteRegistro = new DatagramPacket(envio, envio.length, ia, 4322);
				socket.send(pacoteRegistro);

			} else if (acao == 3) {// Realizar Emprestimo

				// Worflow -> Enviar modelos pro cliente
				// Cliente vai escolher
				// Tratar
				// Retornar novo JSON

				// Region : Enviando Modelos Para o Cliente Alugar

				byte[] jsonEnvio = listModelos.toString().getBytes();
				envio = jsonEnvio;

				DatagramPacket pacoteModelos = new DatagramPacket(envio, envio.length, ia, 4322);
				socket.send(pacoteModelos);

				System.out.println("[Servidor] Modelos Enviados ao Cliente, aguardando resposta...\n");

				// Receber Resposta do Aluguel do Cliente

				String msgFromCliente = " ";

				DatagramPacket pacoteEmprestimo = new DatagramPacket(buffer, buffer.length);
				socket.receive(pacoteEmprestimo);

				System.out.println("Reposta do Cliente Recebida...\n");

				msgFromCliente = new String(pacoteEmprestimo.getData());

				JSONArray jsonCliente = new JSONArray(msgFromCliente);

				jsonServidor = jsonCliente;

				msg = "Aluguel de Carro Efetuado com Sucesso! ";

				JSONObject msgJSONOBJ = new JSONObject();
				msgJSONOBJ.put("msg", msg);

				for (Object obj : jsonServidor) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("msg")) {
						System.out.println("[GERENTE] msg DE ALUGUEL : " + jsonObject.getString("msg"));
					} else if (jsonObject.has("modelo")) {
						System.out.println(" Modelo : " + jsonObject.getString("modelo") + "Foi alugado para: "
								+ jsonObject.getString("nome"));
					}
				}

			} else if (acao == 4) { // Realizar Devolução 
				
				// Enviar Modelos Alugados pelo Cliente
				
				byte[] jsonEnvio = listModelos.toString().getBytes();
				envio = jsonEnvio;

				DatagramPacket pacote = new DatagramPacket(envio, envio.length, ia, 4323);
				socket.send(pacote);

				System.out.println("[DEVOLUÇÂO] Modelos Enviados ao Cliente, aguardando resposta...\n");

				// Receber Resposta do Aluguel do Cliente

				String msgFromCliente = " ";

				DatagramPacket pacoteDevolucao = new DatagramPacket(buffer, buffer.length);
				socket.receive(pacoteDevolucao);

				System.out.println("Reposta do Cliente Recebida...\n");

				msgFromCliente = new String(pacoteDevolucao.getData());

				JSONArray jsonCliente = new JSONArray(msgFromCliente);

				jsonServidor = jsonCliente;

				msg = "Aluguel de Carro Efetuado com Sucesso! ";

				JSONObject msgJSON = new JSONObject();
				msgJSON.put("msg", msg);

				for (Object obj : jsonServidor) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("msg")) {
						System.out.println("[GERENTE] msg DE ALUGUEL : " + jsonObject.getString("msg"));
					} else if (jsonObject.has("modelo")) {
						System.out.println(" Modelo : " + jsonObject.getString("modelo") + "Foi alugado para: "
								+ jsonObject.getString("nome"));
					}
				}			

			} else if (acao == 5) { // Encerrar Conexão
				System.out.print("[Servidor] Encerrando serviço de atendimento.\n");
				msg = "Servidor Encerrado!";
				socket.close();
			}

		}
	}
}