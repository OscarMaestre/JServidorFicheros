
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
    PrintWriter pw;
    BufferedReader bfr;
    InputStream is;
    OutputStream os;
    String directorioFicherosRecibidos=
            "C:\\Users\\ogomez\\Documents\\ficherosrecibidos";
    private void construirFlujos() throws IOException{
        os=socket.getOutputStream();
        is=socket.getInputStream();
        
        pw =Utilidades.getPrintWriter(os);
        bfr=Utilidades.getBufferedReader(is);
    }
    public void establecerConexion() throws IOException{
        InetSocketAddress direccion;
        direccion=new InetSocketAddress("127.0.0.1",
                Constantes.PUERTO_FICHEROS);
        socket=new Socket();
        socket.connect(direccion);
        construirFlujos();
    }
    
    private void enviarLinea(String mensaje){
        this.pw.println(mensaje);
        this.pw.flush();
    }
    public void consultar() throws IOException{
        this.enviarLinea(Constantes.LISTAR);
        String numFicheros=this.bfr.readLine();
        int cantidadFicheros = Integer.parseInt(numFicheros);
        for (int i=0; i<cantidadFicheros; i++){
            String nombreFichero = this.bfr.readLine();
            System.out.println(nombreFichero);
        }
        
    }
    
    public void enviar(String rutaFichero){
        
    }
    
    public void recibir(String nombreFichero) throws IOException{
        this.pw.println(Constantes.GET);
        this.pw.println(nombreFichero);
        this.pw.flush();
        String respuesta=this.bfr.readLine();
        System.out.println("El servidor contestó:"+respuesta);
        if (respuesta.equals(Constantes.ERROR)){
            System.out.println(nombreFichero + "no existe!");
            return ;
        }
        byte[] buffer=new byte[167];
        FileOutputStream ficheroRecibido;
        
        String rutaCompleta=this.directorioFicherosRecibidos+
                File.separator + nombreFichero;
        ficheroRecibido=new FileOutputStream(rutaCompleta);
        DataInputStream dis=new DataInputStream(this.is);
        int numBytesLeidos = this.is.read(buffer);
        while (numBytesLeidos >0){
            ficheroRecibido.write(buffer, 
                    0, numBytesLeidos);
            System.out.println("Recibido un bloque de fichero de tam "+numBytesLeidos);
            numBytesLeidos = dis.read(buffer);
            System.out.println("Se leyo:"+numBytesLeidos);
        }
        System.out.println("Saliendo");
        this.enviarLinea("FIN");
        ficheroRecibido.flush();
        ficheroRecibido.close();
    }
    public void testListadoFicheros() throws IOException{
        this.establecerConexion();
        this.consultar();
    }
    public void testGetFicheroIncorrecto() throws IOException{
        this.establecerConexion();
        this.recibir("hhhhhh.txt");
    }
    public void testGetFicheroCorrecto() throws IOException{
        this.establecerConexion();
        this.recibir("Protocolo.txt");
    }
    public void tests() throws IOException{
        //testListadoFicheros();
        //testGetFicheroIncorrecto();
        testGetFicheroCorrecto();
    }
    public static void main(String[] argumentos) throws IOException{
        Cliente c=new Cliente();
        c.tests();
    }
}
