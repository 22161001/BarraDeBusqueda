## Componenteb barra de Busqueda
----
# Barra de Busqueda
Este componente de Java Swing crea una barra de búsqueda con autocompletado utilizando una lista desplegable tipo JList y una caja de texto JTextField. A continuación, se explica cada parte del código, incluyendo sus métodos y líneas clave.
-----
# Estructura del Código
----
Paquete e Importaciones

# package barrabusqueda;

Define el paquete al que pertenece la clase.

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

Importa las clases necesarias para construir interfaces gráficas, leer archivos, manejar eventos de teclado y texto, y normalizar texto (quitar acentos).

# Clase Principal

public class barraBusqueda extends JPanel {

Define una clase pública que hereda de JPanel, lo que permite integrar el componente en otras interfaces gráficas.

# Atributos Principales

private JTextField campoBusqueda;
private JList<String> listaResultados;
private DefaultListModel<String> modeloLista;
private JScrollPane scroll;
private String[] datos = {};

campoBusqueda: campo de texto donde se escribe la búsqueda.

listaResultados: lista donde se muestran los resultados.

modeloLista: modelo que gestiona los datos de listaResultados.

scroll: scroll vertical que contiene la lista.

datos: arreglo que almacena los datos a buscar.

# Constructor: barraBusqueda()

public barraBusqueda() {
    setLayout(new BorderLayout());

Inicializa el panel con un BorderLayout, lo que facilita colocar elementos en el norte y centro.

# Campo de Búsqueda

campoBusqueda = new JTextField(20);
campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
add(campoBusqueda, BorderLayout.NORTH);

Crea un campo de texto con una fuente legible y lo coloca en la parte superior.

# Lista de Resultados

modeloLista = new DefaultListModel<>();
listaResultados = new JList<>(modeloLista);
listaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
listaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

Inicializa una lista con un modelo de datos editable. Se permite seleccionar solo un elemento a la vez.

# Scroll para Resultados 

scroll = new JScrollPane(listaResultados);
scroll.setPreferredSize(new Dimension(200, 100));
scroll.setVisible(false);
add(scroll, BorderLayout.CENTER);

Agrega la lista a un JScrollPane para manejar el desbordamiento. Se oculta inicialmente.

Listener para Cambios de Texto

campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { buscar(); }
    public void removeUpdate(DocumentEvent e) { buscar(); }
    public void changedUpdate(DocumentEvent e) { buscar(); }
});

Escucha los cambios en el texto del campo. Cada vez que se edita, se llama al método buscar().

Navegación con Teclado

campoBusqueda.addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
        if (!scroll.isVisible()) return;
        int index = listaResultados.getSelectedIndex();

Permite navegar la lista con las flechas:

Abajo: baja la selección

Arriba: sube la selección

Enter: selecciona el elemento

        if (e.getKeyCode() == KeyEvent.VK_DOWN && index < modeloLista.size() - 1) {
            listaResultados.setSelectedIndex(index + 1);
        } else if (e.getKeyCode() == KeyEvent.VK_UP && index > 0) {
            listaResultados.setSelectedIndex(index - 1);
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            seleccionarElemento();
        }
    }
});

Selección con Mouse o Tecla Enter

listaResultados.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            seleccionarElemento();
        }
    }
});

listaResultados.addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            seleccionarElemento();
        }
    }
});

Permiten seleccionar una opción ya sea con doble clic o pulsando Enter desde la lista.

Método: setArchivo(File archivo)

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

Lee un archivo línea por línea, capitaliza cada palabra y guarda las líneas en el arreglo datos.

Método: buscar()

private void buscar() {
    String texto = normalizar(campoBusqueda.getText().trim());
    modeloLista.clear();

Normaliza el texto ingresado y limpia la lista de resultados anteriores.

    if (texto.isEmpty() || datos.length == 0) {
        scroll.setVisible(false);
        return;
    }

Si no hay texto o datos, oculta el scroll y termina.

    boolean hayResultados = false;
    for (String nombre : datos) {
        if (normalizar(nombre).startsWith(texto)) {
            modeloLista.addElement(nombre);
            hayResultados = true;
        }
    }

Busca coincidencias que comiencen con el texto ingresado.

    if (hayResultados) {
        scroll.setVisible(true);
    } else {
        modeloLista.addElement("No se encontraron resultados.");
        scroll.setVisible(true);
    }

    revalidate();
    repaint();
}

Muestra los resultados (o un mensaje si no hay) y actualiza el panel.

Método: seleccionarElemento()

private void seleccionarElemento() {
    String seleccion = listaResultados.getSelectedValue();
    if (seleccion != null && !seleccion.equals("No se encontraron resultados.")) {
        campoBusqueda.setText(seleccion);
    }

    modeloLista.clear();
    scroll.setVisible(false);
    campoBusqueda.requestFocus();
}

Inserta el valor seleccionado en el campo de texto, limpia la lista y devuelve el foco al campo.

Método: capitalizar(String texto)

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

Convierte el texto a "Capitalizado", es decir, primera letra en mayúscula y el resto en minúscula.

Método: normalizar(String texto)

private String normalizar(String texto) {
    texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
    return texto.replaceAll("\\p{M}", "").toLowerCase();
}

Elimina tildes y convierte el texto a minúsculas, útil para hacer búsquedas más robustas.
