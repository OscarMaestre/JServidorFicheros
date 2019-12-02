
package com.iesmaestre;

import java.io.BufferedReader;
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
    private OutputStream os;
    private InputStream is;
    private PrintWriter pw;
    private BufferedReader bfr;
    private final String directorioFicheros="C:\\Users\\ogomez\\"
                + "Documents\\servidorficheros";

    Peticion(Socket conexionEntrante) throws IOException {
        this.socket=conexionEntrante;
        this.construirFlujos();
    }

    private void construirFlujos() throws IOException{
        os=socket.getOutputStream();
        is=socket.getInputStream();
        
        pw =Utilidades.getPrintWriter(os);
        bfr=Utilidades.getBufferedReader(is);
    }
    @Override
    public void run() {
        try {
            String linea=this.bfr.readLine();
            while (!linea.equals(Constantes.FIN)){
                if (linea.equals(Constantes.LISTAR)){
                    enviarListadoFicheros();
                }
                if (linea.equals(Constantes.GET)){
                    String nombreFicheroParaDescargar;
                    nombreFicheroParaDescargar=
                            this.bfr.readLine();
                    descargarFichero(
                            nombreFicheroParaDescargar);
                }
                linea=this.bfr.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Peticion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarListadoFicheros() {
      
        /* Primero averiguarmos cuantos ficheros
        hay en dicha carpeta y enviamos ese número al cliente        */
        File directorio=new File(directorioFicheros);
        /* Despues, si hay ficheros, averiguamos los nombres
        y vamos enviando los nombres al cliente*/
        File[] listaFicheros = directorio.listFiles();
        /* Enviamos la cantidad de ficheros que hay*/
        pw.println(listaFicheros.length);
        for (File f:listaFicheros){
            String nombreFichero=f.getName();
            pw.println(nombreFichero);
        }
        pw.flush();
        
    }

    private void descargarFichero(String nombreFicheroParaDescargar) throws FileNotFoundException, IOException {
        String nombreCompletoFichero;
        nombreCompletoFichero=
                this.directorioFicheros+File.separator+
                nombreFicheroParaDescargar;
        System.out.println(nombreCompletoFichero);
        File f=new File(nombreCompletoFichero);
        if (!f.exists()){
            this.pw.println(Constantes.ERROR);
            this.pw.flush();
            return ;
        }
        this.pw.println(Constantes.EXITO);
        this.pw.flush();
                
        /* Si llegamos aquí el fichero existe y hay que
        enviarlo a través de la red por bytes */
        System.out.println("Enviando fichero:"+
                nombreCompletoFichero);
        /* Enviamos el fichero a través de la red*/
        FileInputStream ficheroBytes;
        ficheroBytes=new FileInputStream(
                nombreCompletoFichero);
        byte[] buffer=new byte[Constantes.TAM_BUFFER];
        int numBytesLeidos = ficheroBytes.read(buffer);
        while (numBytesLeidos != -1){
            System.out.println("El servidor leyó"
                    + "estos bytes:"+numBytesLeidos);
            this.os.write(buffer, 0, numBytesLeidos);
            numBytesLeidos = ficheroBytes.read(buffer);
        }
        this.os.flush();
        ficheroBytes.close();
        
    }

}
