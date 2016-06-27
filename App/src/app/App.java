/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import vistas.VistaCliente;

/**
 *
 * @author robin
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VistaCliente vistaCliente; // Crea instancia de la vista de inicio del cliente
        vistaCliente = new VistaCliente();
        vistaCliente.setVisible(true); // Hace visible la ventana creada
    }
    
}
