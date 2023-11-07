package SocketsChat;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        Socket clienteSocket = null;
        PrintWriter salida = null;
        BufferedReader entrada = null;

        try {
            clienteSocket = new Socket("127.0.0.1", 5000);
            salida = new PrintWriter(clienteSocket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            System.out.println("Bienvenido al chat multidireccional y bidireccional ,para comunicarte con una persona en un chat privado utiliza el simbolo @ seguido del nombre de la persona");
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
                            break; // Salir del bucle si el socket se cierra
                        }
                        respuesta = finalEntrada.readLine();
                        if (respuesta == null) {
                            break; // Salir si la lectura es nula
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