Write-Host "Iniciando Sales System..." -ForegroundColor Green
Set-Location "C:\Users\User\Downloads\Sales-System"

Write-Host "Verificando MySQL..." -ForegroundColor Yellow
$mysqlProcess = Get-Process -Name "mysqld" -ErrorAction SilentlyContinue
if ($mysqlProcess) {
    Write-Host "MySQL está ejecutándose ✓" -ForegroundColor Green
} else {
    Write-Host "MySQL no está ejecutándose ✗" -ForegroundColor Red
    exit 1
}

Write-Host "Iniciando Spring Boot..." -ForegroundColor Yellow
Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -NoNewWindow -Wait
