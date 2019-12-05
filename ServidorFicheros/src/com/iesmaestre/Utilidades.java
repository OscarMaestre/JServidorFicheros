
package com.iesmaestre;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class Utilidades {
    public static BufferedReader
        getBufferedReader(InputStream is){
            InputStreamReader isr=
                    new InputStreamReader(is);
            BufferedReader bfr=
                    new BufferedReader(isr);
            return bfr;
    }
        
    public static PrintWriter
            getPrintWriter(OutputStream os){
                OutputStreamWriter osw=new OutputStreamWriter(os);
                PrintWriter pw= new PrintWriter(osw);
                return pw;
                        
    }
    /*Este método recibe un fichero enviado a través de un socket, confiando en 
            que el emisor primero envía la longitud de dicho fichero   */
    public static void recibirFichero(Socket socket, String rutaFichero, int tamanoBuffer) throws FileNotFoundException, IOException{
        byte[] buffer=new byte[tamanoBuffer];
        
        DataInputStream flujoReceptor=new DataInputStream(socket.getInputStream());
        FileOutputStream ficheroRecibido=new FileOutputStream(rutaFichero);
        
        long totalBytesParaRecibir=flujoReceptor.readLong();
        System.out.println("Esperando un fichero de longitud:"+totalBytesParaRecibir);
        int numBytesLeidos = flujoReceptor.read(buffer);
        long contadorBytesRecibidos=numBytesLeidos;
        while (contadorBytesRecibidos != totalBytesParaRecibir){
            ficheroRecibido.write(buffer, 
                    0, numBytesLeidos);
            numBytesLeidos = flujoReceptor.read(buffer);
            contadorBytesRecibidos+=numBytesLeidos;
            System.out.print("\rBytes recibidos hasta el momento:"+
                    contadorBytesRecibidos+" de "+totalBytesParaRecibir);
        }
        System.out.println("Fichero recibido!");
        ficheroRecibido.close();
    }
    
    /*Este método envía un fichero a través de un socket indicando primero el número
    total de bytes que se van a enviar a fin de que el receptor sepa exactamente
    cuantos bytes leer   */
    public static void enviarFichero(Socket socket, String rutaFichero, int tamanoBuffer) throws IOException {
        /*Averiguamos el tamaño del fichero*/
        File fichero=new File (rutaFichero);
        long bytesFichero = fichero.length();
        
        DataOutputStream flujoEmision=new DataOutputStream(socket.getOutputStream());
        FileInputStream  ficheroParaEnviar=new FileInputStream(rutaFichero);

        /*Enviamos la longitud del fichero*/
        flujoEmision.writeLong(bytesFichero);

        byte[] buffer=new byte[tamanoBuffer];        

        int numBytesLeidos = ficheroParaEnviar.read(buffer);
        while (numBytesLeidos >0){
            flujoEmision.write(buffer, 0, numBytesLeidos);
            numBytesLeidos = ficheroParaEnviar.read(buffer);
        }
        flujoEmision.flush();
        ficheroParaEnviar.close();
    }
}
