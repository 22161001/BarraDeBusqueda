package barrabusqueda;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class barraBusqueda extends JPanel {

    private JTextField campoBusqueda;
    private JList<String> listaResultados;
    private DefaultListModel<String> modeloLista;
    private JScrollPane scroll;
    private String[] datos = {};

    public barraBusqueda() {
        setLayout(new BorderLayout());

        // Inicialización del campo de búsqueda
        campoBusqueda = new JTextField(20);
        campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(campoBusqueda, BorderLayout.NORTH);

        // Inicialización de la lista y su modelo
        modeloLista = new DefaultListModel<>();
        listaResultados = new JList<>(modeloLista);
        listaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Scroll para la lista
        scroll = new JScrollPane(listaResultados);
        scroll.setPreferredSize(new Dimension(200, 100));
        scroll.setVisible(false);
        add(scroll, BorderLayout.CENTER);

        // Actualizar cada vez que se escribe o borra algo
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscar(); }
            public void removeUpdate(DocumentEvent e) { buscar(); }
            public void changedUpdate(DocumentEvent e) { buscar(); }
        });

        // Navegación por flechitas y selección con Enter
        campoBusqueda.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!scroll.isVisible()) return;
                int index = listaResultados.getSelectedIndex();

                if (e.getKeyCode() == KeyEvent.VK_DOWN && index < modeloLista.size() - 1) {
                    listaResultados.setSelectedIndex(index + 1);
                    listaResultados.ensureIndexIsVisible(index + 1); 
                } else if (e.getKeyCode() == KeyEvent.VK_UP && index > 0) {
                    listaResultados.setSelectedIndex(index - 1);
                    listaResultados.ensureIndexIsVisible(index - 1);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    seleccionarElemento();
                }
            }
        });

        // Doble clic para seleccionarlo
        listaResultados.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarElemento();
                }
            }
        });

        // Selección con Enter desde la lista
        listaResultados.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    seleccionarElemento();
                }
            }
        });
    }

    // Carga el archivo 
    public void setArchivo(File archivo) {
        try (Scanner scanner = new Scanner(archivo)) {
            List<String> palabras = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (!linea.isEmpty()) {
                    palabras.add(capitalizar(linea));
                }
            }
            datos = palabras.toArray(new String[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        }
    }

    // Búsqueda por palabras que INICIAN con el texto (nombre o apellido)
    private void buscar() {
        String texto = normalizar(campoBusqueda.getText().trim());
        modeloLista.clear();

        if (texto.isEmpty() || datos.length == 0) {
            scroll.setVisible(false);
            return;
        }

        boolean hayResultados = false;
        for (String nombre : datos) {
            String[] palabras = normalizar(nombre).split("\\s+");
            for (String palabra : palabras) {
                if (palabra.startsWith(texto)) { // <-- ahora SOLO al inicio de la palabra
                    modeloLista.addElement(nombre);
                    hayResultados = true;
                    break;
                }
            }
        }

        if (hayResultados) {
            scroll.setVisible(true);
        } else {
            modeloLista.addElement("No se encontraron resultados.");
            scroll.setVisible(true);
        }

        revalidate();
        repaint();
    }

    // Selecciona el elemento actual de la lista
    private void seleccionarElemento() {
        String seleccion = listaResultados.getSelectedValue();
        if (seleccion != null && !seleccion.equals("No se encontraron resultados.")) {
            campoBusqueda.setText(seleccion);
        }

        modeloLista.clear();
        scroll.setVisible(false);
        campoBusqueda.requestFocus();
    }

    // vuelve la primera en mayuscula y las demas en minusculas
    private String capitalizar(String texto) {
        String[] partes = texto.toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                resultado.append(Character.toUpperCase(parte.charAt(0)))
                        .append(parte.substring(1)).append(" ");
            }
        }
        return resultado.toString().trim();
    }

    // Convierte el texto a minúsculas
    private String normalizar(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return texto.replaceAll("\\p{M}", "").toLowerCase();
    }
}
