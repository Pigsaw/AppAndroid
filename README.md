# 📱 Proyecto Android – Actividad 15 %

Prototipo 2 – Programación Android
Estudiante: Ismael González 
Carrera: Ingeniería en Informática 
Fecha:17 de Octubre 2025

---

## 🧾 Resumen del Proyecto
Aplicación Android desarrollada en Java que implementa 8 Intents en total (5 implícitos + 3 explícitos) 
Incluye manejo de permisos, uso de cámara, hilos secundarios, navegación entre Activities y documentación en GitHub con README, capturas y APK debug.

> 📦 Versión Android Studio: Koala / Giraffe o superior  
> 📱 minSdk / targetSdk: según build.gradle  
> 🧩 Lenguaje: Java

---

## ⚙️ Estructura del Repositorio GitHub
- Rama principal: `main`
- Rama de trabajo: `feature/intents`
- Commits atómicos y descriptivos 
- Proyecto subido con archivo `README.md` y carpeta `app/build/outputs/apk/debug/app-debug.apk`

---

## 🚀 Intents Implícitos (5)
| # | Descripción | Acción / Código Clave |
|:-:|:-------------|:---------------------|
| 1 | Abrir ubicación en Google Maps (IP Santo Tomás – Vergara 165) | `Uri.parse("geo:-33.4514,-70.6627?q=Vergara 165, Santiago, Chile")` → `ACTION_VIEW` |
| 2 | Abrir cámara frontal del dispositivo | `MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA` con extras de frontal |
| 3 | Marcar teléfono desde Home | `Intent.ACTION_DIAL` con `tel:+56912345678` |
| 4 | Elegir imagen de la galería | `ActivityResultContracts.GetContent()` con `image/*` |
| 5 | Abrir configuración de Bluetooth | `Settings.ACTION_BLUETOOTH_SETTINGS` |

Todos funcionan mediante botones en `HomeActivity`.

---

## 🧭 Intents Explícitos (3)
| # | Origen → Destino | Funcionalidad |
|:-:|:------------------|:--------------|
| 1 | `LoginActivity → HomeActivity` | Envía email del usuario con `putExtra()` y muestra mensaje de bienvenida. |
| 2 | `HomeActivity → PerfilActivity` | Envía email, recibe nombre editado con `ActivityResultLauncher`. |
| 3 | `HomeActivity → CamaraActivity` | Abre pantalla propia para tomar fotografía con permisos de cámara. |

---

## 🔦 Permisos y Componentes
- **CAMERA** para linterna y fotos.
- **FileProvider** registrado en `AndroidManifest.xml`.
- **Thread** para simular proceso en segundo plano.
- **Toolbar/Menu** con opciones: Perfil – Docs – Salir.

---

El archivo apk está disponible en: app/build/outputs/apk/debug/app-debug.apk
O se puede compilar en: (Build > Build Bundle(s) / APK(s) > Build APK(s)).

## 📷 Capturas de pantalla

![Menú](imagenes/Menú.png)
![Bluetooth](imagenes/Bluetooth.png)
![Maps](imagenes/Maps.png)
![Perfil](imagenes/Perfil.png)




