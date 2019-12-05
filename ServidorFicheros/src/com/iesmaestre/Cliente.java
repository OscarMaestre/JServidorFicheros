
package com.iesmaestre;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Cliente {
    Socket socket=null;
    DataInputStream entrada;
    DataOutputStream salida;
    
    String directorioFicherosRecibidos=
            "C:\\Users\\ogomez\\Documents\\ficherosrecibidos";

    private void construirFlujos() throws IOException{
        salida  =new DataOutputStream(socket.getOutputStream());
        entrada =new DataInputStream (socket.getInputStream() );
        
        
    }
    public void establecerConexion() throws IOException{
        InetSocketAddress direccion;
        direccion=new InetSocketAddress("127.0.0.1",
                Constantes.PUERTO_FICHEROS);
        socket=new Socket();
        socket.connect(direccion);
        construirFlujos();
    }
    
    private void enviarLinea(String mensaje) throws IOException{
        this.salida.writeUTF(mensaje);
        this.salida.flush();
        
    }
    public void consultar() throws IOException{
        this.enviarLinea(Constantes.LISTAR);
        String numFicheros=this.entrada.readUTF();
        int cantidadFicheros = Integer.parseInt(numFicheros);
        for (int i=0; i<cantidadFicheros; i++){
            String nombreFichero = this.entrada.readUTF();
            System.out.println(nombreFichero);
        }
        
    }
    
    public void enviar(String rutaFichero){
        
    }
    
    public void recibir(String nombreFichero) throws IOException, InterruptedException{
        
        this.salida.writeUTF(Constantes.GET);
        this.salida.writeUTF(nombreFichero);
        this.salida.flush();
        System.out.println("Fichero pedido");
        String respuesta=this.entrada.readUTF();
        System.out.println("El servidor contestó:"+respuesta);
        if (respuesta.equals(Constantes.ERROR)){
            System.out.println(nombreFichero + "no existe!");
            return ;
        }
        
        /*Si llegamos aquí es que el fichero sí existe en el servidor
        y podemos descargarlo  */       
        String rutaCompleta=this.directorioFicherosRecibidos+
                File.separator + nombreFichero;
        int tamBufferRecepcion=167;
        Utilidades.recibirFichero(socket, nombreFichero, 167);

        /*Una vez recibido todo avisamos al servidor de que cerramos*/       
        this.enviarLinea("FIN");
        
        this.socket.shutdownOutput();
        this.socket.close();
    }
    public void testListadoFicheros() throws IOException{
        this.establecerConexion();
        this.consultar();
    }
    public void testGetFicheroIncorrecto() throws IOException, InterruptedException{
        this.establecerConexion();
        this.recibir("hhhhhh.txt");
    }
    public void testGetFicheroCorrecto() throws IOException, InterruptedException{
        this.establecerConexion();
        this.recibir("Protocolo.txt");
    }
    public void tests() throws IOException, InterruptedException{
        //testListadoFicheros();
        //testGetFicheroIncorrecto();
        testGetFicheroCorrecto();
    }
    public static void main(String[] argumentos) throws IOException, InterruptedException{
        Cliente c=new Cliente();
        c.tests();
    }
}
