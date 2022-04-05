# Arep Tarea 5

### Autor: Juan Sebastian Ospina  -Fecha:  04/05/2022

## **Arepa Tarea 5**

En esta tarea Se desea plantear una conexion segura entre aplicaciones aplicaciones montadas en AWS, para esto
lo que se debe hacer es el uso de ssl para la conexion http, para lograr esto voy a usar el framework de SparkJava
y una funcion de esta llamada secure, con esta podemos hacer uso de los keyStore y de los TrustStore, con eso logramos crear certificados, ademas de confiar en certificados creados por nosotros:

![](Img1.png)

El metodo Secure permite configurar estos dos con sus Passwords, pero se me presento un problema con el TrusStore que no me lo configuraba por eso realize la configuracion manual de esta usando de net.ssl para ello, una ves probado lo desplege.


Como funciona mi aplicacion super basica primero esta configurada para que lea variables de entorno:

    TRUSTSTORE: Ruta de el archivo de TrustStore
    TRUSTSTOREPWD: Password de el TrustStore
    KEYSTORE: Ruta de el archivo de KeyStore
    KEYSTOREPWD: Password de el TrustKeyStoreStore
    PORT: puerto por donde correra el servicio

Esto permite que la aplicacion se pueda desplegar con variables de entorno y se adapte al nuevo entorno donde se despligue,
Despues de ver esto, vamos a tener una comunicacion entre dos servicios muy sencillos, uno va a consultar al otro un numero aleatorio
usando una conexion http + ssl, ya que a donde va a consultar el numero esta en su TrustStore va a poder realizar la consulta a este,
Para apreciar eso, el servidor que consulta el numero al igual que el que genera el numero random lo van a imprimir en pantalla.




![](VID_20220405_144814.mp4)
