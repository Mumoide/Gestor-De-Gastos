Gestor de gastos (CLI)

App de línea de comandos para registrar y consultar gastos.

¿Qué hace?
- Permite Agregar, listar, editar y eliminar gastos con persistencia de datos en un archivo JSON.
- Generar reportes mensuales.
- Exportar gastos a CSV con las siguientes opciones: Exportar todo, exportar por rango de fechas o exportar por estado.

Stack
- Java 17+ + GSON para este repositorio.
- Spring Boot + Jackson CSV para el microservicio.

Roadmap
- ~~Agregar filtro por mes en listado de gastos.~~
- ~~Editar y eliminar por ID.~~
- ~~Reporte mensual por categoría y total.~~
- ~~Exportar a CSV.~~ (Implementado con microservicio en Spring Boot)
- Manejo de errores y validaciones más amigables. Falta listar gastos, editar gasto, eliminar gasto, reporte mensual, y exportar reporte 
- Tests con JUnit.