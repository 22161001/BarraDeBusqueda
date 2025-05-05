# Componente barra de Busqueda
----

# Barra de Busqueda
barraBusqueda es un componente personalizado en Java Swing que implementa una barra de búsqueda con sugerencias automáticas tipo autocompletado. Muestra los resultados en una lista desplegable a medida que el usuario escribe, permitiendo seleccionar una opción con teclado o ratón. Es útil para facilitar búsquedas rápidas dentro de un conjunto de datos como nombres, palabras clave, etc.

-----
# Estructura del Código
----
# Paquete e Importaciones

package barrabusqueda;

Define el paquete al que pertenece la clase.

```java
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
```

Importa las clases necesarias para construir interfaces gráficas, leer archivos, manejar eventos de teclado y texto, y normalizar texto (quitar acentos).

---
# Clase Principal

```java
public class barraBusqueda extends JPanel {
```

Define una clase pública que hereda de JPanel, lo que permite integrar el componente en otras interfaces gráficas.

---
# Atributos Principales
```java
private JTextField campoBusqueda;
private JList<String> listaResultados;
private DefaultListModel<String> modeloLista;
private JScrollPane scroll;
private String[] datos = {};
```
campoBusqueda: campo de texto donde se escribe la búsqueda.

listaResultados: lista donde se muestran los resultados.

modeloLista: modelo que gestiona los datos de listaResultados.

scroll: scroll vertical que contiene la lista.

datos: arreglo que almacena los datos a buscar.

---
# Constructor: barraBusqueda()
```java
public barraBusqueda() {
    setLayout(new BorderLayout());
```
Inicializa el panel con un BorderLayout, lo que facilita colocar elementos en el norte y centro.

## Campo de Búsqueda
```
campoBusqueda = new JTextField(20);
campoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
add(campoBusqueda, BorderLayout.NORTH);
```
Crea un campo de texto con una fuente legible y lo coloca en la parte superior.

## Lista de Resultados
```java
modeloLista = new DefaultListModel<>();
listaResultados = new JList<>(modeloLista);
listaResultados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
listaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
```
Inicializa una lista con un modelo de datos editable. Se permite seleccionar solo un elemento a la vez.

## Scroll para Resultados 
```java
scroll = new JScrollPane(listaResultados);
scroll.setPreferredSize(new Dimension(200, 100));
scroll.setVisible(false);
add(scroll, BorderLayout.CENTER);
```
Agrega la lista a un JScrollPane para manejar el desbordamiento. Se oculta inicialmente.

## Listener para Cambios de Texto
```java
campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { buscar(); }
    public void removeUpdate(DocumentEvent e) { buscar(); }
    public void changedUpdate(DocumentEvent e) { buscar(); }
});
```
Escucha los cambios en el texto del campo. Cada vez que se edita, se llama al método buscar().

## Navegación con Teclado
```java
campoBusqueda.addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
        if (!scroll.isVisible()) return;
        int index = listaResultados.getSelectedIndex();
```
Permite navegar la lista con las flechas:

Abajo: baja la selección

Arriba: sube la selección

Enter: selecciona el elemento
```java
        if (e.getKeyCode() == KeyEvent.VK_DOWN && index < modeloLista.size() - 1) {
            listaResultados.setSelectedIndex(index + 1);
        } else if (e.getKeyCode() == KeyEvent.VK_UP && index > 0) {
            listaResultados.setSelectedIndex(index - 1);
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            seleccionarElemento();
        }
    }
});
```
## Selección con Mouse o Tecla Enter
```java
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
```
Permiten seleccionar una opción ya sea con doble clic o pulsando Enter desde la lista.

---
# Método: setArchivo(File archivo)
```java
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
```
Lee un archivo línea por línea, capitaliza cada palabra y guarda las líneas en el arreglo datos.

---
# Método: buscar()
```java
private void buscar() {
    String texto = normalizar(campoBusqueda.getText().trim());
    modeloLista.clear();

Normaliza el texto ingresado y limpia la lista de resultados anteriores.

    if (texto.isEmpty() || datos.length == 0) {
        scroll.setVisible(false);
        return;
    }
```
Si no hay texto o datos, oculta el scroll y termina.
```java
    boolean hayResultados = false;
    for (String nombre : datos) {
        if (normalizar(nombre).startsWith(texto)) {
            modeloLista.addElement(nombre);
            hayResultados = true;
        }
    }
```
Busca coincidencias que comiencen con el texto ingresado.
```java
    if (hayResultados) {
        scroll.setVisible(true);
    } else {
        modeloLista.addElement("No se encontraron resultados.");
        scroll.setVisible(true);
    }

    revalidate();
    repaint();
}
```
Muestra los resultados (o un mensaje si no hay) y actualiza el panel.

---
# Método: seleccionarElemento()
```java
private void seleccionarElemento() {
    String seleccion = listaResultados.getSelectedValue();
    if (seleccion != null && !seleccion.equals("No se encontraron resultados.")) {
        campoBusqueda.setText(seleccion);
    }

    modeloLista.clear();
    scroll.setVisible(false);
    campoBusqueda.requestFocus();
}
```
Inserta el valor seleccionado en el campo de texto, limpia la lista y devuelve el foco al campo.

---
# Método: capitalizar(String texto)
```java
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
```
Convierte el texto a "Capitalizado", es decir, primera letra en mayúscula y el resto en minúscula.

---
# Método: normalizar(String texto)


```java
private String normalizar(String texto) {
    texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
    return texto.replaceAll("\\p{M}", "").toLowerCase();
}
```
Elimina tildes y convierte el texto a minúsculas, útil para hacer búsquedas más robustas.

-------
# Instrucciones de uso 

1.- Crea un nuevo JFrame de prueba.

![image](https://github.com/user-attachments/assets/86668d45-4dae-41ad-87ce-24b0a72db43b)

2.- Agrega tu componente.jar a la paleta de componentes

a) En la barra de herramientas busca Tools

![image](https://github.com/user-attachments/assets/f17fc9f9-dab9-42d1-bd18-00e816a37abf)

b) busca donde dice Palette

![image](https://github.com/user-attachments/assets/556df796-08e5-430c-b8f0-cf2aa513cd0d)

c)Selecciona Swing/AWT Components

![image](https://github.com/user-attachments/assets/13bb805c-9163-4e78-90a5-cecf5c46ee55)

d) Agrega una nueva categoria

![image](https://github.com/user-attachments/assets/76e7b3f7-7b3f-481f-a81c-87c8a38f5c78)

e) La categoria se llamará BarraBusqueda

![image](https://github.com/user-attachments/assets/8f6b9ff1-d80d-44d7-89f1-2c8ee2e3a60f)

f) Agregamos nuestro .jar, damos en donde dice Add from JAR

![image](https://github.com/user-attachments/assets/1dedd75a-738c-4472-a272-b89b8192c622)

g) Buscamos y seleccionamos nuestro .jar

![image](https://github.com/user-attachments/assets/9967e503-8a73-44ec-8281-2bd062e380b6)

h) seleccionamos como se llama nuestro componente

![image](https://github.com/user-attachments/assets/1d6870e9-a2bf-4246-b0fa-5aa9ca615c79)

i) Y seleccionamos en la categoria que nuestro componente debe aparecer

![image](https://github.com/user-attachments/assets/5165ac3e-a1a6-421f-85f4-900ffdff17f4)

y listo, solo verificamos que aparezca nuestro componente en la paleta.

![image](https://github.com/user-attachments/assets/ce640742-70da-4bad-8a0e-8c5f73b26eec)


3.-Arrastramos nuestro componente a nuestro JFrame

![image](https://github.com/user-attachments/assets/9832d811-e70c-41b6-86d6-c432dc93e55e)

4.- Entramos a propiedades del componente

![image](https://github.com/user-attachments/assets/32298903-11a7-4202-a074-fa406370998f)

5.- Buscamos donde dice archivo y le damos en los ...

![image](https://github.com/user-attachments/assets/f5772c9a-525d-45d3-a4d5-3549ab228522)

6.- Seleccionamos nuestro archivo .txt o .csv

![image](https://github.com/user-attachments/assets/f8934eb6-7de4-4a06-9461-bb2a06961584)

7.- Ahora si ejecutamos nuestro Jframe.

8.- Escribimos en el cuadro de texto y seleccionamos cualquier sugerencia que aparezca.

![image](https://github.com/user-attachments/assets/6727b6f9-97d6-4fca-9b89-c7b0b3c952f3)

9.- Podemos seleccionar usando las teclas de direccion y ENTER, o tambien haciendo doble clic sobre la sugerencia.

![image](https://github.com/user-attachments/assets/b7ae5849-fff8-4626-875d-7c12a8c18592) ![image](https://github.com/user-attachments/assets/f1a64e24-6760-44d1-a150-23c158a39528)


----
# CREDITOS DEL EQUIPO

Núñez Reyes Jorge Emilio

Antonio Contreras Alan

Cruz Gallegos Julio Gabriel


-----
# LINK DEL VIDEO











