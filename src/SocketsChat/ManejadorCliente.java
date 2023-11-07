package SocketsChat;

import java.io.*;
import java.net.*;
/**
 * La clase ManejadorCliente sirve como puente para transmitir la información del Cliente al Servidor.
 * @author Subgrupo 27 (Persistencia y Datos Transaccionales)
 */

public class ManejadorCliente implements Runnable {
    /**
     * Se utiliza para manejar la comunicación entre el Cliente y el Servidor.
     * @param clienteSocket Representa la conexión establecida con un cliente, a través de este constructor interactua.
     * @param salida Es un medio a través del cual el servidor puede enviar mensajes o datos al cliente.
     * */

    private Socket clienteSocket;
    private PrintWriter salida;
    private String nombreCliente;


    /*
     *metodo constructor de la clase manejador cliente

     */
    public ManejadorCliente(Socket clienteSocket, PrintWriter salida) {

        this.clienteSocket = clienteSocket;
        this.salida = salida;
    }
    /**
     * Gestiona Clientes individualmente en sus respectivos hilos abriendo flujos separados de entrada y salida.
     * */
    @Override
    public void run() {
        // Muestra en pantalla la conexión del nuevo Cliente.
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            nombreCliente = entrada.readLine();
            System.out.println("Cliente conectado: " + nombreCliente);
            // Almacena el nuevo Cliente en la lista clientesConectados
            synchronized (Servidor.clientesConectados) {
                Servidor.clientesConectados.put(nombreCliente, salida);
                Servidor.enviarListaUsuarios();
            }

            String mensaje;
            while (true) {
                try {
                    mensaje = entrada.readLine();
                    if (mensaje == null) {
                        break;
                    }
                    /*
                     * Este condicional permite enviar un mensaje privado de un Cliente a otro al escribir @ seguido
                     * del nombre del cliente.
                     */

                    if (mensaje.startsWith("@")) {
                        int espacio = mensaje.indexOf(" ");
                        if (espacio != -1) {
                            String destinatario = mensaje.substring(1, espacio);
                            String contenido = mensaje.substring(espacio + 1);

                            PrintWriter escritor = Servidor.clientesConectados.get(destinatario);
                            // Mensaje para el Cliente seleccionado.
                            if (escritor != null) {
                                escritor.println(nombreCliente + ": " + contenido);
                            }
                        }
                        /*
                         * En caso de que no sea un mensaje privado, el comportamiento por defecto es enviar el mensaje
                         * para todos.
                         */
                    } else {
                        //envia un mensaje a todos
                        synchronized (Servidor.clientesConectados) {
                            for (PrintWriter escritor : Servidor.clientesConectados.values()) {
                                if (escritor != salida) {
                                    escritor.println(nombreCliente + ": " + mensaje);
                                }
                            }
                        }
                    }
                } catch (SocketException e) {

                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Elimina de la lista de usuarios al Cliente que se desconectó.
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