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

    private JTextField campoBusqueda; // Campo de texto donde el usuario escribe la búsqueda
    private JList<String> listaResultados; // Lista que muestra las coincidencias encontradas
    private DefaultListModel<String> modeloLista; // Modelo de datos para la lista
    private JScrollPane scroll; // Scroll para la lista
    private String[] datos = {}; // Arreglo de datos cargados desde archivo

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
        scroll.setVisible(false); // Ocultar la lista inicialmente
        add(scroll, BorderLayout.CENTER);

        // Escuchar cambios en el campo de texto (cada vez que se escribe o borra algo)
        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscar(); }
            public void removeUpdate(DocumentEvent e) { buscar(); }
            public void changedUpdate(DocumentEvent e) { buscar(); }
        });

        // Navegación por teclado dentro de la lista y selección con Enter
        campoBusqueda.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!scroll.isVisible()) return;
                int index = listaResultados.getSelectedIndex();

                if (e.getKeyCode() == KeyEvent.VK_DOWN && index < modeloLista.size() - 1) {
                    listaResultados.setSelectedIndex(index + 1); // Flecha abajo
                } else if (e.getKeyCode() == KeyEvent.VK_UP && index > 0) {
                    listaResultados.setSelectedIndex(index - 1); // Flecha arriba
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    seleccionarElemento(); // Enter selecciona el elemento
                }
            }
        });

        // Doble clic en un elemento de la lista para seleccionarlo
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

    // Carga el archivo con las palabras a mostrar en la búsqueda
    public void setArchivo(File archivo) {
        try (Scanner scanner = new Scanner(archivo)) {
            List<String> palabras = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine().trim();
                if (!linea.isEmpty()) {
                    palabras.add(capitalizar(linea)); // Capitaliza cada línea
                }
            }
            datos = palabras.toArray(new String[0]); // Convierte la lista a arreglo
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + e.getMessage());
        }
    }

    // Realiza la búsqueda ignorando acentos y mayúsculas/minúsculas
    private void buscar() {
        String texto = normalizar(campoBusqueda.getText().trim());
        modeloLista.clear();

        if (texto.isEmpty() || datos.length == 0) {
            scroll.setVisible(false); // Oculta la lista si no hay texto
            return;
        }

        boolean hayResultados = false;
        for (String nombre : datos) {
            if (normalizar(nombre).startsWith(texto)) { // Compara ignorando acentos
                modeloLista.addElement(nombre);
                hayResultados = true;
            }
        }

        if (hayResultados) {
            scroll.setVisible(true); // Muestra si hay resultados
        } else {
            modeloLista.addElement("No se encontraron resultados.");
            scroll.setVisible(true); // Muestra igual para indicar que no hay coincidencias
        }

        revalidate(); // Revalida el layout del panel
        repaint(); // Redibuja el panel
    }

    // Selecciona el elemento actual de la lista
    private void seleccionarElemento() {
        String seleccion = listaResultados.getSelectedValue();
        if (seleccion != null && !seleccion.equals("No se encontraron resultados.")) {
            campoBusqueda.setText(seleccion); // Coloca el texto seleccionado en el campo
        }

        modeloLista.clear(); // Limpia la lista
        scroll.setVisible(false); // Oculta la lista después de seleccionar
        campoBusqueda.requestFocus(); // Vuelve a enfocar el campo de texto
    }

    // Este método convierte un texto en formato "Capitalizado", es decir, que la primera letra de cada palabra esté en mayúscula y el resto en minúscula.
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

    // Elimina acentos y convierte el texto a minúsculas
    private String normalizar(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return texto.replaceAll("\\p{M}", "").toLowerCase(); // Elimina signos diacríticos
    }
}
