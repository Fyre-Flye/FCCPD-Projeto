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
	private boolean ALREADY_HAS_NAME = false;
	private JSONArray JSONCliente;

	public ClientMulticast(int entrada, int acao) {
		var = entrada;
		action = acao;
	}

	public void run() {
		try {
			// Region : Socket Config

			String msgFromServidor = " ";
			MulticastSocket socket = new MulticastSocket(var);
			byte[] buffer = new byte[1024];
			InetAddress ia = InetAddress.getByName("230.0.0.0");
			InetSocketAddress grupo = new InetSocketAddress(ia, var);
			NetworkInterface ni = NetworkInterface.getByInetAddress(ia);
			socket.joinGroup(grupo, ni);

			// Region : Read name

			System.out.println("Digite o seu nome: ");
			Scanner scan = new Scanner(System.in);

			if (!this.ALREADY_HAS_NAME) {
				this.nome = scan.nextLine();
				this.ALREADY_HAS_NAME = true;
			}

			if (this.action == 1) { // Receber Anúncio
				System.out.println("[ " + nome + " ] Conectando ao servidor");

				// Region: Tratamento do Pacote do Servidor

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				msgFromServidor = new String(packet.getData());

				JSONArray jsonServidor = new JSONArray(msgFromServidor);

				JSONCliente = jsonServidor;

				for (Object obj : jsonServidor) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("modelo")) {
						System.out.println("Modelo : " + jsonObject.getString("modelo"));
					}
					if (jsonObject.has("disponibilidade")) {
						System.out.println(" Disponibilidade : " + jsonObject.getString("disponibilidade"));
					}
				}

			} else if (this.action == 2) { // Region: Alugar Carro

				// Worflow -> Receber do Servidor os modelos
				// Escolher Modelo
				// Retornar ao Servidor o modelo escolhido

				// Region : Receive Package from SERVIDOR (GERENTE)

				System.out.println("Aguardando Resposta do Servidor...\n");

				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				msgFromServidor = new String(packet.getData());

				JSONArray jsonServidor = new JSONArray(msgFromServidor);

				JSONCliente = jsonServidor;

				System.out.println("[GERENTE] Modelos Disponíveis:\n");

				// Listando Modelos Recebidos pelo Servidor

				for (Object obj : JSONCliente) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("modelo")) {
						System.out.println("Modelo : " + jsonObject.getString("modelo"));
					}
					if (jsonObject.has("disponibilidade")) {
						System.out.println(" Disponibilidade : " + jsonObject.getString("disponibilidade"));
					}
				}

				// End Region : Receive from SERVIDOR (GERENTE)

				// Region : Alugar

				System.out.println("[ " + nome + " ] Escolha o carro desejado para alugar:\n");
				String escolha;
				Scanner choice = new Scanner(System.in);
				escolha = choice.nextLine();

				for (Object obj : JSONCliente) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("modelo") && obj.equals(choice)) {
						if (jsonObject.has("disponibilidade") && obj.equals("disponivel")) {
							jsonObject.put("nome", this.nome);
							jsonObject.put("disponibilidade", "alugado");
						} else {
							System.out.println("Carro não disponível para alugar.\n");
						}
					}
				}

				// Region: Send Package Again to Server

				System.out.println("[ " + nome + " ] Carro escolhido: " + escolha + ":\n");
				buffer = escolha.getBytes();

				System.out.println("Veiculo Alugado com Sucesso, enviando confirmação!\n");

				DatagramPacket pacoteAluguel = new DatagramPacket(buffer, buffer.length, ia, 4322);
				socket.send(pacoteAluguel);

			} else if (this.action == 3) { // Devolver

				// Receber Resposta de devolução do Servidor

				DatagramPacket pacoteEmprestimo = new DatagramPacket(buffer, buffer.length);
				socket.receive(pacoteEmprestimo);

				System.out.println("Reposta do Cliente Recebida...\n");

				msgFromServidor = new String(pacoteEmprestimo.getData());

				JSONArray jsonServidor = new JSONArray(msgFromServidor);

				JSONCliente = jsonServidor;

				msgFromServidor = "Aluguel de Carro Efetuado com Sucesso! ";

				JSONObject msgJSONOBJ = new JSONObject();
				msgJSONOBJ.put("msgDevo", msgFromServidor);
				
				// Listando Modelos alugados pelo cliente

				for (Object obj : jsonServidor) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("msg")) {
						System.out.println("[GERENTE] INFO DE DEVOLUÇÃO : " + jsonObject.getString("msgDevo"));
					} else if (jsonObject.has("modelo") && obj.equals(this.nome)) {
						System.out.println(" Modelo : " + jsonObject.getString("modelo") + "Foi alugado para: "
								+ jsonObject.getString("nome"));
					}
				}
				
				// Enviar Resposta de Devolução 
				
				System.out.println("[ " + nome + " ] Escolha o carro desejado para devolver:\n");
				String escolha;
				Scanner choice = new Scanner(System.in);
				escolha = choice.nextLine();

				for (Object obj : JSONCliente) {
					JSONObject jsonObject = (JSONObject) obj;
					if (jsonObject.has("nome") && obj.equals(choice)) {
						if (jsonObject.has("disponibilidade") && obj.equals("alugado")) {
							jsonObject.put("disponibilidade", "disponivel.");
						} else {
							System.out.println("Nome do carro inválido.\n");
						}
					}
				}
				
				System.out.println("[ " + nome + " ] Carro devolvido: " + escolha + ":\n");
				buffer = escolha.getBytes();

				System.out.println("Veiculo Devolvido com Sucesso, enviando confirmação!\n");

				DatagramPacket pacoteDevolucao = new DatagramPacket(buffer, buffer.length, ia, 4323);
				socket.send(pacoteDevolucao);

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
		if (entrada == 1) { // Anuncio
			ClientMulticast client1 = new ClientMulticast(4321, entrada);
			client1.action = entrada;
			new Thread(client1).start();

		} else if (entrada == 2) { // Alugar
			ClientMulticast client2 = new ClientMulticast(4322, entrada);
			client2.action = entrada;
			new Thread(client2).start();

		} else if (entrada == 3) { // Devolver
			ClientMulticast client3 = new ClientMulticast(4323, entrada);
			client3.action = entrada;
			new Thread(client3).start();
		}
	}
}