# Lista De Compras
Aplicación sencilla para reemplazar el papelito en la lista de las compras del supermercado.

# Uso de la App
Esta app nativa para Android, desarrollada en Java, pretende ser lo más simple posible, para que la usen las personas sin
habilidades para los smartphones.  Inicialmente la desarrollé para mi madre, que con más de 70 años y su primer smart, la usa 
diariamente para sus compras en el supermercado.

La app consta de 2 tabs, uno para la lista de compras, y otro para TODAS las cosas que ALGUNA VEZ compramos... porque ir al 
supermercado es una actividad repetitiva,
constantemente estamos comprando lo mismo, escribiéndolo una y otra vez (leche, agua, ajo, aceite, bananas,...).

La idea es sólo escribir el artículo a comprar cuando NUNCA ANTES lo compramos.  Entonces, lo agregamos haciendo click en signo (+)
del tab "Total".  Allí nos aparecerá un dialog donde podremos escribir el artículo, y un checkbox para agregarlo además a la lista
de "Compras", si deseamos.

En el tab de "Compras" están los elementos seleccionados, que son los que deseamos comprar.  Para sacarlos de esta lista,
debemos hacer click (o tap) en el mismo, y se borrará, pero quedará en la lista "Total" para agregarlo nuevamente cuando deseemos
volver a comprar lo mismo.

En el tab de "Compras" también podemos hacer click en el botón "+" que está al lado de cada artículo, para agregar 1 a la
cantidad que deseamos comprar.  Y abajo aparecé el botón de "Compartir" para enviar la lista por WhatsApp, SMS, Mail, etc.

En el tab "Total" están todos los ítems.  Podemos agregarlo a la lista de "Compras" haciendo click (o tap).  También podemos
editarlo haciendo click en el lápiz, pero sólo si no están en la lista de "Compras".  Como este tab está pensado para tener
TODOS los artículos POSIBLES de ser comprados, nunca se eliminan, y están ordenados de manera alfabética.  Aquí también hay
un cuadro de texto donde podemos empezar a escribir el nombre de un artículo para encontrarlo más fácilmente.

# Aspectos técnicos
La app está desarrollada en Java.  Actualmente usa una BBDD sqlite local para almacenar los artículos, y accedemos a ella mediante
ROOM.  Está a mitad de desarrollo la posibilidad de agregar una foto a cada artículo, así como el backup y restore de los datos.

Usa 2 fragments (1 para el tab de "Compras" y otro para el tab "Total"), OptionsMenu, FloatingAccessButton y RecyclerView para
los artículos.

# Instalación
Se puede descargar de https://play.google.com/store/apps/details?id=com.adriangalvarez.listadecompras
