# Configuración de Spring Boot DevTools y Actuator

## ¿Qué se ha configurado?

Se han agregado las siguientes dependencias y configuraciones para habilitar **Hot Reload** (recarga automática de cambios sin detener el proyecto):

### 1. **spring-boot-devtools**
   - Monitorea cambios en archivos de la carpeta `src/main`
   - Reinicia automáticamente la aplicación cuando detecta cambios
   - Ya estaba incluido en el proyecto

### 2. **spring-boot-starter-actuator**
   - Proporciona endpoints para monitoreo y control de la aplicación
   - Permite reiniciar la aplicación de forma remota

## Configuración en application.properties

```properties
# Habilitar reinicio automático
spring.devtools.restart.enabled=true

# Intervalo de sondeo para detectar cambios (2 segundos)
spring.devtools.restart.poll-interval=2000ms

# Período de espera silencioso antes de reiniciar (1 segundo)
spring.devtools.restart.quiet-period=1000ms

# Habilitar LiveReload para navegadores
spring.devtools.livereload.enabled=true

# Endpoints de Actuator expuestos
management.endpoints.web.exposure.include=health,info,restart,env

# Mostrar detalles en el endpoint de salud
management.endpoint.health.show-details=always

# Habilitar el endpoint de reinicio remoto
management.endpoint.restart.enabled=true
```

## Cómo usar DevTools

### Opción 1: IDE con soporte automático (Recomendado)
En **IntelliJ IDEA / JetBrains** (que es lo que usas):
1. Abre el proyecto
2. Haz clic en **File → Settings → Build, Execution, Deployment → Compiler**
3. Marca la opción: **"Build project automatically"**
4. Presiona `Ctrl + Shift + Alt + /` y abre **Registry**
5. Busca y marca: **"compiler.automake.allow.when.app.running"**
6. Haz clic en **Run → Run** para iniciar la aplicación
7. Los cambios en los archivos se aplicarán automáticamente

### Opción 2: Reinicio manual en IntelliJ
1. Inicia el proyecto
2. Realiza cambios en los archivos (Java, HTML, propiedades)
3. Presiona `Ctrl + F9` (Build Project) o `Ctrl + Shift + F9` (Rebuild Project)
4. La aplicación se reiniciará automáticamente

### Opción 3: Endpoint REST (Reinicio remoto)
Puedes reiniciar la aplicación haciendo una solicitud POST:
```bash
curl -X POST http://localhost:8080/actuator/restart
```

## Archivos que se monitorean para cambios automáticos

✅ Archivos Java compilados (`src/main/java`)
✅ Archivos Thymeleaf (`src/main/resources/templates`)
✅ Archivos de propiedades (`application.properties`)
✅ Archivos estáticos (`src/main/resources/static`)

❌ **NO** se reinicia al cambiar `pom.xml` (requiere reconstruir el proyecto)

## Solución de problemas

### Los cambios no se aplican:
1. Asegúrate de que `spring.devtools.restart.enabled=true`
2. En IntelliJ, verifica que "Build project automatically" está habilitado
3. Guarda el archivo (Ctrl + S)
4. Presiona Ctrl + F9 para compilar

### El proyecto sigue reiniciando indefinidamente:
- Aumenta `spring.devtools.restart.quiet-period` a `2000ms`

### Los cambios en templates no se reflejan:
- Asegúrate de que `spring.thymeleaf.cache=false` está configurado
- Borra la carpeta `target/` y reconstruye el proyecto

## Endpoints de Actuator disponibles

- `http://localhost:8080/actuator/health` - Estado de salud de la aplicación
- `http://localhost:8080/actuator/info` - Información de la aplicación
- `http://localhost:8080/actuator/restart` - Reiniciar la aplicación (POST)
- `http://localhost:8080/actuator/env` - Variables de entorno

## Rendimiento

⚠️ **Nota**: DevTools puede ralentizar el desarrollo en máquinas con recursos limitados. Si experimentas problemas de rendimiento:
- Disminuye `poll-interval` a `1000ms`
- Excluye carpetas innecesarias del monitoreo


