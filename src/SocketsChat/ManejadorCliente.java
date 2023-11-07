package SocketsChat;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    private PrintWriter salida;
    private String nombreCliente;

    public ManejadorCliente(Socket clienteSocket, PrintWriter salida) {
        this.clienteSocket = clienteSocket;
        this.salida = salida;
    }

    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            nombreCliente = entrada.readLine();
            System.out.println("Cliente conectado: " + nombreCliente);

            synchronized (Servidor.clientesConectados) {
                Servidor.clientesConectados.put(nombreCliente, salida);
                Servidor.enviarListaUsuarios();
            }

            String mensaje;
            while (true) {
                mensaje = entrada.readLine();
                if (mensaje == null) {
                    break;
                }

                if (mensaje.startsWith("@")) {
                    int espacio = mensaje.indexOf(" ");
                    if (espacio != -1) {
                        String destinatario = mensaje.substring(1, espacio);
                        String contenido = mensaje.substring(espacio + 1);

                        PrintWriter escritor = Servidor.clientesConectados.get(destinatario);
                        if (escritor != null) {
                            escritor.println(nombreCliente + ": " + contenido);
                        }
                    }
                } else {
                    // Mensaje para todos
                    synchronized (Servidor.clientesConectados) {
                        for (PrintWriter escritor : Servidor.clientesConectados.values()) {
                            if (escritor != salida) {
                                escritor.println(nombreCliente + ": " + mensaje);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
                synchronized (Servidor.clientesConectados) {
                    Servidor.clientesConectados.remove(nombreCliente);
                    Servidor.enviarListaUsuarios();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}