package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import java.util.Map;


/**
 * La clase Servidor se encarga de crear el Socket que recibirá las conexiones de los clientes.
 * @author Subgrupo 27 (Persistencia y Datos Transaccionales)
 * */
public class Servidor {

    //Se crea un HashMap para almacenar los clientes que se van conectando
    static Map<String, PrintWriter> clientesConectados = new HashMap<>();

    /**
     * Es el método principal de la clase.
     * */
    public static void main(String[] args) {

        // El puerto al que los Clientes se conectan.
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Esperando que un cliente se conecte");


            //Acepta el Socket del Cliente que se conecte, muestra la IP a la que se conectó e inicia el hilo para comunicarse.
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

    /**
     * Se encarga de mostrar en pantalla todos los usuarios conectados en ese momento.
     * */
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
