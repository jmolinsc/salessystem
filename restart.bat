@echo off
cd /d "C:\Users\User\Downloads\Sales-System"
echo ==========================================
echo    REINICIANDO APLICACION SALES SYSTEM
echo ==========================================
echo.
echo [1/4] Deteniendo procesos Java existentes...
taskkill /f /im java.exe 2>nul
taskkill /f /im javaw.exe 2>nul
netstat -ano | findstr :8080 > nul
if %errorlevel% == 0 (
    echo Puerto 8080 ocupado, intentando liberar...
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr :8080') do taskkill /f /pid %%i 2>nul
)
timeout /t 3 >nul

echo [2/4] Limpiando target y compilando...
call mvnw.cmd clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Fallo en compilacion
    pause
    exit /b 1
)

echo [3/4] Iniciando aplicacion en puerto 8080...
echo CREDENCIALES: admin / admin123
echo URL: http://localhost:8080
echo.
start "Spring Boot App" cmd /k "mvnw.cmd spring-boot:run"

echo [4/4] Esperando inicio de aplicacion...
timeout /t 5 >nul
start http://localhost:8080

echo.
echo ==========================================
echo    APLICACION INICIADA CORRECTAMENTE
echo ==========================================
pause
