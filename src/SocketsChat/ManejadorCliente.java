package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.List;
/*
Esta clase permite crear los hilos que gestionan los chats del cliente
 */

public class ManejadorCliente implements Runnable {
    private Socket clienteSocket;
    // creamos una variable privada de tipo socket
    private PrintWriter salida;
    private BufferedReader entrada;
    private List<PrintWriter> salidas;

    public ManejadorCliente(Socket clienteSocket, PrintWriter salida, List<PrintWriter> salidas) {
        this.clienteSocket = clienteSocket;
        this.salida = salida;
        this.salidas = salidas;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            String nombreCliente = entrada.readLine();
            System.out.println("Cliente conectado: " + nombreCliente);

            while (true) {
                String mensaje = entrada.readLine();
                if (mensaje == null) {
                    // Cliente desconectado
                    break;
                }

                System.out.println(nombreCliente + ": " + mensaje);


                synchronized (salidas) {
                    for (PrintWriter otraSalida : salidas) {
                        if (otraSalida != salida) {
                            otraSalida.println(nombreCliente + ": " + mensaje);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
                synchronized (salidas) {
                    salidas.remove(salida);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
