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
        try {
            /*
             Se crea un nuevo objeto de tipo socket el cual referencia al servidor ,se establece el valor 5000 como puerto de conexion.
             */
            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Esperando que un cliente se conecte");

            while (true) {
                /*
                Ciclo mediante el cual el servidor escucha iterativamente las solicitudes de conexion por parte de los clientes
                 */
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Te conectaste Utilizando la siguiente IP del local host " + clienteSocket.getInetAddress().getHostAddress());
                PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true);

                /*
                 *cada vez que un cliente se conecta se obtiene su direcion IP y se muestra en el log del servidor
                 */

                Thread hiloCliente = new Thread(new ManejadorCliente(clienteSocket, salida));
                hiloCliente.start();
                /*
                se crea un nuevo objeto de tipo de hilo y se inicia cada vez que un usuario se conecta al servidor de manera iterativa
                 */
            }
        } catch (IOException e) {
            /*
            proviene del metodo try-catch si ocurre un error en la ejecucion del servidor aparece un mensaje el cual indica que la conexion no fue exitosa
             */
            e.printStackTrace();
        }
    }
    /*
    Esta clase permite imprimir en el log del cliente y del servidor que clientes se encuentran conectados se actualiza cada vez que uno se conecta

     */
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
