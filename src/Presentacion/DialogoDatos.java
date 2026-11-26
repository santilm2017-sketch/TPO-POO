package Presentacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Ventana común hecha con JDialog, JPanel, JButton y eventos de la cursada.
class DialogoDatos extends JDialog implements ActionListener {
    private int respuesta = -1;
    private List<JButton> botones = new ArrayList<>();
    DialogoDatos(JFrame padre, String titulo, JPanel contenido, String[] opciones) {
        super(padre, titulo, true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(contenido);
        JPanel botones = new JPanel(new FlowLayout());
        for (int i = 0; i < opciones.length; i++) {
            JButton boton = new JButton(opciones[i]);
            boton.addActionListener(this);
            this.botones.add(boton);
            botones.add(boton);
        }
        panel.add(botones);
        add(panel);
        pack();
    }
    int mostrar() {
        setVisible(true);
        return respuesta;
    }
    public void actionPerformed(ActionEvent evento) {
        for (int i = 0; i < botones.size(); i++) {
            if (evento.getSource() == botones.get(i)) respuesta = i;
        }
        dispose();
    }
    static void mostrarTexto(JFrame padre, String titulo, String texto) {
        List<String> paginas = new ArrayList<>();
        String parte = ""; int lineas = 0;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i); parte += c;
            if (c == '\n') lineas++;
            if (lineas == 18) { paginas.add(parte); parte = ""; lineas = 0; }
        }
        if (!parte.isEmpty() || paginas.isEmpty()) paginas.add(parte);
        int pagina = 0;
        while (true) {
            JTextArea area = new JTextArea(paginas.get(pagina), 20, 95);
            area.setEditable(false);
            JPanel panel = new JPanel(new FlowLayout());panel.add(area);
            String[] opciones = {"Cerrar"};
            String encabezado = titulo;
            if (paginas.size() > 1) {
                encabezado += " - " + (pagina + 1) + "/" + paginas.size();
                if (pagina == 0) opciones = new String[]{"Cerrar", "Siguiente"};
                else if (pagina == paginas.size() - 1) opciones = new String[]{"Cerrar", "Anterior"};
                else opciones = new String[]{"Cerrar", "Anterior", "Siguiente"};
            }
            DialogoDatos dialogo = new DialogoDatos(padre, encabezado, panel, opciones);
            int respuesta = dialogo.mostrar();
            if (respuesta <= 0) return;
            if (opciones[respuesta].equals("Anterior")) pagina--;
            else pagina++;
        }
    }
}
