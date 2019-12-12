
package com.iesmaestre;

import io.github.oscarmaestre.jutilidades.Utilidades;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Peticion implements Runnable{

    private final Socket socket;
    private DataOutputStream salida;
    private DataInputStream  entrada;
    private final String directorioFicheros="C:\\Users\\ogomez\\"
                + "Documents\\servidorficheros";
    
    Peticion(Socket conexionEntrante) throws IOException {
        this.socket=conexionEntrante;
        this.construirFlujos();
    }

    private void construirFlujos() throws IOException{
        salida  =new DataOutputStream(socket.getOutputStream());
        entrada =new DataInputStream (socket.getInputStream() );
    }
    
    @Override
    public void run() {
        try {
            String linea=this.entrada.readUTF();
            while (!linea.equals(Constantes.FIN)){
                if (linea.equals(Constantes.LISTAR)){
                    enviarListadoFicheros();
                }
                
                if (linea.equals(Constantes.PUT)){
                    String nombreFicheroParaRecibir;
                    nombreFicheroParaRecibir=
                            this.entrada.readUTF();
                    System.out.println("Cliente enviando fichero:"+
                            nombreFicheroParaRecibir);
                    this.recibirFichero(
                        nombreFicheroParaRecibir);
                }
                
                if (linea.equals(Constantes.GET)){
                    String nombreFicheroParaDescargar;
                    nombreFicheroParaDescargar=
                            this.entrada.readUTF();
                    descargarFichero(
                            nombreFicheroParaDescargar);
                }
                linea=this.entrada.readUTF();
            }
            System.out.println("Cliente cerró la conexion");
        } catch (IOException ex) {
            Logger.getLogger(Peticion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarListadoFicheros() throws IOException {
      
        /* Primero averiguarmos cuantos ficheros
        hay en dicha carpeta y enviamos ese número al cliente        */
        File directorio=new File(directorioFicheros);
        /* Despues, si hay ficheros, averiguamos los nombres
        y vamos enviando los nombres al cliente*/
        File[] listaFicheros = directorio.listFiles();
        /* Enviamos la cantidad de ficheros que hay*/
        this.salida.writeInt(listaFicheros.length);
        for (File f:listaFicheros){
            String nombreFichero=f.getName();
            this.salida.writeUTF(nombreFichero);
        }
        this.salida.flush();
        
    }

    private void descargarFichero(String nombreFicheroParaDescargar) throws FileNotFoundException, IOException {
        String nombreCompletoFichero;
        nombreCompletoFichero=
                this.directorioFicheros+File.separator+
                nombreFicheroParaDescargar;
        System.out.println(nombreCompletoFichero);
        File f=new File(nombreCompletoFichero);
        if (!f.exists()){
            this.salida.writeUTF(Constantes.ERROR);
            this.salida.flush();
            return ;
        }
        this.salida.writeUTF(Constantes.EXITO);
        this.salida.flush();
                
        /* Si llegamos aquí el fichero existe y hay que
        enviarlo a través de la red por bytes */
        System.out.println("Enviando fichero:"+
                nombreCompletoFichero);
        /* Enviamos el fichero a través de la red*/
        Utilidades.enviarFichero(socket, nombreCompletoFichero, Constantes.TAM_BUFFER);
    }
    
    private void recibirFichero(String nombreFichero) throws IOException{
        String rutaCompletaFicheroRecibido;
        rutaCompletaFicheroRecibido=
            this.directorioFicheros + File.separator +
                nombreFichero;
        System.out.println("Almacenandolo en:"+
                rutaCompletaFicheroRecibido);
        Utilidades.recibirFichero(socket, 
                rutaCompletaFicheroRecibido, 
                Constantes.TAM_BUFFER);
        
    }

}
