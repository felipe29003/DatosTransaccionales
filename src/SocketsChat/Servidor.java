package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
/*
Creamos una clase la cual gestiona el socket referente al servidor
 */
public class Servidor {
    private static List<Socket> clientes = new ArrayList<>();
    private static List<PrintWriter> salidas = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Esperando que un cliente se conecte");

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Te conectaste Utilizando la siguiente IP del local host " + clienteSocket.getInetAddress().getHostAddress());
                BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
                PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);

                synchronized (clientes) {
                    clientes.add(clienteSocket);
                    salidas.add(salida);
                }

                Thread hiloCliente = new Thread(new ManejadorCliente(clienteSocket, salida, salidas));
                hiloCliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
