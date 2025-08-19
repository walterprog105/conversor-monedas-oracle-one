<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Gson-000000?style=for-the-badge&logo=google&logoColor=white"/>
  <img src="https://img.shields.io/badge/Oracle%20ONE-F80000?style=for-the-badge&logo=oracle&logoColor=white"/>
  <br>
  <img src="https://cdn-icons-png.flaticon.com/512/2103/2103633.png" width="200">
  <h1 align="center">💱 Conversor de Monedas</h1>
  <h3 align="center">Proyecto Java con API de tasas de cambio</h3>
</p>

## 🌍 Descripción
Aplicación de consola Java que permite:
- Consultar tasas de cambio en tiempo real mediante APIs
- Realizar conversiones entre +160 monedas
- Interfaz intuitiva con validación de entradas
- Procesamiento eficiente de datos JSON con Gson

**Desarrollado como parte del programa Oracle ONE Next Education**

## 🚀 Características Principales
✅ Consumo de API REST con HttpClient  
✅ Precisión decimal con BigDecimal  
✅ Interfaz de usuario por consola interactiva  
✅ Parseo de JSON con Gson  
✅ Validación robusta de entradas  
✅ Pruebas unitarias integradas

## 🛠️ Tecnologías
| Componente | Versión | Uso |
|------------|---------|-----|
| Java JDK | 21+ | Lógica principal |
| Gson | 2.10.1 | Parseo de JSON |
| HttpClient | Java 11+ | Conexiones API |
| Maven | 3.9+ | Gestión de dependencias |

## 📦 Instalación
```bash
# Clonar repositorio
git clone https://github.com/walterprog105/conversor-monedas-oracle-one

# Compilar proyecto
mvn clean package

# Ejecutar
java -jar target/conversor-monedas-1.0.jar