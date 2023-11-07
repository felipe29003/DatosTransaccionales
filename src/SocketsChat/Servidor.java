package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import java.util.Map;

public class Servidor {
    static Map<String, PrintWriter> clientesConectados = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Esperando que un cliente se conecte");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Te conectaste Utilizando la siguiente IP del local host " + clienteSocket.getInetAddress().getHostAddress());
                PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);

                Thread hiloCliente = new Thread(new ManejadorCliente(clienteSocket, salida));
                hiloCliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void enviarListaUsuarios() {
        synchronized (clientesConectados) {
            for (PrintWriter escritor : clientesConectados.values()) {
                escritor.print("Usuarios Conectados: ");
                for (String usuario : clientesConectados.keySet()) {
                    escritor.print(usuario + " ");
                }
                escritor.println();
            }
        }
    }
}
