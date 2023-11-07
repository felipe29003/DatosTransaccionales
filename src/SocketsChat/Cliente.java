package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.Scanner;
/**
 * La clase Cliente se encarga de crear el Socket para comunicarse con el servidor.
 * @author Subgrupo 27 (Persistencia y Datos Transaccionales)
 */
public class Cliente {
    /**
     * Es el método principal de la clase.
     * */

    public static void main(String[] args) {
        /*
         *clienteSocket es una variable de tipo socket la cual permite almacenar los datos del socket del cliente
         * la variable salida asociada a un objeto PrintWriter permite enviar datos del usuario del socket cliente hacia el servidor usando flujo de salida
         * la variable entrada asociada a un objeto BufferedReader lee los datos del servidor y se los envia al cliente a travez de un flujo de entrada
         */
        Socket clienteSocket = null;
        PrintWriter salida = null;
        BufferedReader entrada = null;

        try {
            clienteSocket = new Socket("127.0.0.1", 5000);
            salida = new PrintWriter(clienteSocket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            System.out.println("Bienvenido al chat multidireccional y bidireccional ,para comunicarte con una persona de manera privada utiliza el simbolo @ seguido del nombre de la persona");
            System.out.println("Recuerda utilizar el comando 'chao' para desconectarte del chat");
            Scanner scanner = new Scanner(System.in);

            System.out.print("Ingresa tu nombre: ");
            String nombre = scanner.nextLine();
            salida.println(nombre);

            String mensaje;
            BufferedReader finalEntrada = entrada;
            final Socket finalClienteSocket = clienteSocket;

            Thread entradaMensajes = new Thread(() -> {
                try {
                    String respuesta;
                    while (true) {
                        if (finalClienteSocket.isClosed()) {
                            System.out.println("El usuario "+nombre+" Se desconecto");
                            break;
                        }
                        respuesta = finalEntrada.readLine();
                        if (respuesta == null) {
                            /*
                            si el mensaje del usuario es nulo se rompe el cliclo de mensajes
                             */
                            break;
                        }
                        System.out.println(respuesta);
                    }
                } catch (SocketException e) {
                    if (!finalClienteSocket.isClosed()) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            entradaMensajes.start();

            while (true) {
                /*
                Crea un objeto de tipo scanner este se encarga de determinar si el usuario escribio chao de tal forma que se cierra el socket
                 */
                mensaje = scanner.nextLine();
                salida.println(mensaje);
                if (mensaje.equalsIgnoreCase("chao")) {
                    System.out.println("El usuario "+nombre+" se desconecto");
                    clienteSocket.close();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (clienteSocket != null) {
                    clienteSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}