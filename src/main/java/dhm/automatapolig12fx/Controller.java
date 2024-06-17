package dhm.automatapolig12fx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML Button btnArchivo,btnSalir,btnProcesar;
    @FXML TextArea txtSalida;
    @FXML TextField txtFrase;
    @FXML CheckBox chkArchivo;
    @FXML Label lblNombreArchivo;

    Scanner leerArchivo = null;

    Alert alert = new Alert(Alert.AlertType.ERROR);

    FileChooser fileChooser = new FileChooser();
    File archivo = null;

    int NodoInicial = 0;
    int NodoFinal = 9;
    int NodoActual;
    Objeto[][] matriz = new Objeto[11][11];
    boolean Valida = true;

    private void llenarMatriz(){

        // Llena la matriz con null
        for (int i = 0 ; i < 11 ; i++){
            for(int k = 0 ; k < 11 ; k++){
                matriz[i][k] = null;
            }
        }

        // Validaciones
        matriz[0][1] = new Objeto(0,1,"(\\s|[1-9])*");  // Vacio o numeros del 2 al 9, cero o mas veces
        matriz[1][1] = new Objeto(1,1,"(\\s|[0-9])*");  // Vacio o numeros del 0 al 9, cero o mas veces
        matriz[1][2] = new Objeto(1,2,"x");             // Solo el caracter x
        matriz[2][3] = new Objeto(2,3,"\\^");           // Solo el caracter ^
        matriz[2][10] = new Objeto(2,10,"[+-]");        // Solo signos de + o -
        matriz[3][4] = new Objeto(3,4,"2");             // Solo el caracter 2
        matriz[4][5] = new Objeto(4,5,"[+-]");          // Solo signos de + o -
        matriz[4][8] = new Objeto(4,8,"[+-]");          // Solo signos de + o -
        matriz[5][6] = new Objeto(5,6,"(\\s|[1-9])*");  // Vacio o numeros del 2 al 9, cero o mas veces
        matriz[6][6] = new Objeto(6,6,"(\\s|[0-9])*");  // Vacio o numeros del 0 al 9, cero o mas veces
        matriz[6][7] = new Objeto(6,7,"x");             // Solo el caracter x
        matriz[7][8] = new Objeto(7,8,"[+-]");          // Solo signos de + o -
        matriz[8][9] = new Objeto(8,9,"[1-9]");         // Solo numeros del 1 al 9
        matriz[9][9] = new Objeto(9,9,"(\\s|[0-9])*");  // Vacio o numeros del 0 al 9, cero o mas veces
        matriz[10][9] = new Objeto(10,9,"[1-9]");       // Solo numeros del 1 al 9

    }

    private void revisarCadena(String auxCadena, TextArea txtSalida){

        Valida = true;
        char[] cadena = auxCadena.toCharArray();
        NodoActual = NodoInicial;
        int auxNoValido = 0 ;

        for(int ContadorCharCadena = 0 ; ContadorCharCadena < cadena.length ; ContadorCharCadena++){

            for(int Columna = 0 ; Columna < matriz.length ; Columna++){

                if(matriz[NodoActual][Columna] == null){
                    continue;
                }

                txtSalida.insertText(txtSalida.getLength(),"\n\nNodo actual: q" + NodoActual);
                txtSalida.insertText(txtSalida.getLength(),"\nCaracter a evaluar: " + cadena[ContadorCharCadena]);

                if(Pattern.matches(matriz[NodoActual][Columna].validacion,String.valueOf(cadena[ContadorCharCadena]))){

                    txtSalida.insertText(txtSalida.getLength(),"\nValidacion correcta, cambia al nodo: q" + matriz[NodoActual][Columna].NodoDestino);
                    NodoActual = matriz[NodoActual][Columna].NodoDestino;
                    Valida = true;
                    break;

                }else {
                    txtSalida.insertText(txtSalida.getLength(),"\nNo hay validacion, buscando otro camino");
                    Valida = false;
                }
            }

            if(!Valida){
                break;
            }

        }

        if(NodoActual == NodoFinal && Valida){
            txtSalida.insertText(txtSalida.getLength(),"\n\nCADENA VALIDA");
        }else {
            txtSalida.insertText(txtSalida.getLength(),"\n\nCADENA NO VALIDA");
        }

    }

    private void revisarArchivo(Scanner leerArchivo, TextArea txtSalida){

        int auxLinea = 1;
        String auxCadena = null;

        while (leerArchivo.hasNextLine()) {
            auxCadena = leerArchivo.nextLine();
            txtSalida.insertText(txtSalida.getLength(),"\n\nLINEA #" + auxLinea + "\nContenido: " + auxCadena);
            auxLinea++;
            revisarCadena(auxCadena,txtSalida);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        llenarMatriz();

        // Desactiva para el usuario el checkbox para el archivo y la caja de texto
        chkArchivo.setDisable(true);
        txtSalida.setEditable(false);

        // Acciones de los botones de la interfaz
        btnSalir.setOnAction(event -> System.exit(0));

        btnArchivo.setOnAction(event -> {

            fileChooser.setTitle("Buscar archivo");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivo de texto", "*.txt")
            );

            archivo = fileChooser.showOpenDialog(null);

            if ((archivo == null) || (archivo.getName().equals(""))) {
                alert.setHeaderText(null);
                alert.setTitle("Error");
                alert.setContentText("Archivo invalido o no seleccionado.");
                alert.showAndWait();
            }else{
                chkArchivo.setSelected(true);
                try {
                    lblNombreArchivo.setText(archivo.getName());
                    leerArchivo = new Scanner(archivo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });

        btnProcesar.setOnAction(event -> {


            txtSalida.clear();

            // Evalua la cadena ingresada, si es que  hay
            String auxCadena = txtFrase.getText();
            auxCadena = auxCadena.replaceAll(" ","");

            if(!auxCadena.isEmpty()){
                txtSalida.clear();
                txtSalida.insertText(txtSalida.getLength(),"ORIGEN: CADENA INGRESADA\nContenido: " + auxCadena);

                revisarCadena(auxCadena,txtSalida);
                txtFrase.clear();
            }

            // Evalua el archivo, si es que hay
            if(archivo != null){
                txtSalida.insertText(txtSalida.getLength(),"\nORIGEN: ARCHIVO");

                revisarArchivo(leerArchivo,txtSalida);

                chkArchivo.setSelected(false);
                lblNombreArchivo.setText(null);
                leerArchivo.close();
                archivo = null;
            }


        });

    }



}