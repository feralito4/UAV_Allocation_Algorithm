Esto es un proyecto Maven, de modo que todas las librerias usadas y sus dependencias se encuentran en el archivo pom.xml

Para ejecutar el proyecto se debe realizar una compilación mediante Maven y después ejecutar el método Main en la clase Client.

El método InitData() en el que se inicializan todos los datos del proyecto. Las lineas 46-51 contienen las variables más importantes para ejecutar los distintos algoritmos. 
Aunque hay más parámetros que pueden modificarse como "modoEMU" que se encuentra en la llamada al mátodo del algoritmo de colocación Tercera Versión en la linea 326.
Por último, decir que si se necesita mantener una de las configuraciones generadas mediante el metodo generateInput(), se debe comentar las llamadas al método en las lineas 254 para la 
topología pequeña y 158 para la topología grande.
