package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.Scanner;
/*
Clase la cual permite gestionar el socket referente al cliente
 */
public class Cliente {
    public static void main(String[] args) {
        Socket clienteSocket = null;
        PrintWriter salida = null;
        BufferedReader entrada = null;

        try {
            //en este caso se crea un objeto socket referente al cliente se le asigna la ip(local host) y el puerto el cual debe coincidir con el socket del servidor
            clienteSocket = new Socket("127.0.0.1", 5000);
            salida = new PrintWriter(clienteSocket.getOutputStream(), true);
            final BufferedReader finalEntrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

            Scanner scanner = new Scanner(System.in);
            System.out.print("Bienvenido al chat multidireccional Ingresa tu nombre ");
            String nombre = scanner.nextLine();
            salida.println(nombre);

            System.out.println("Este es tu comienzo del chat escribe el comando 'chao' para descontectarte");

            Thread entradaMensajes = new Thread(new Runnable() {
                public void run() {
                    try {
                        String mensaje;
                        while (true) {
                            mensaje = finalEntrada.readLine();
                            if (mensaje == null) {
                                // El servidor cerró el socket
                                break;
                            }
                            System.out.println(mensaje);
                        }
                    } catch (SocketException se) {
                        // Ignorar la excepción cuando el socket se cierra
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            entradaMensajes.start();

            String mensaje;
            do {
                mensaje = scanner.nextLine();
                salida.println(mensaje);
            } while (!mensaje.equalsIgnoreCase("chao"));

//se utiliza el .close() para cerrar el socket al momento en el que el usuario digita en consola chao
            clienteSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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