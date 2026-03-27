Write-Host "Buildando game-api..." -ForegroundColor Cyan

mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0)
{
    Write-Host "ERRO: Maven falhou!" -ForegroundColor Red
    exit 1
}

docker build -t xcorpiiion/game-api:latest .

if ($LASTEXITCODE -eq 0)
{
    Write-Host "game-api buildada com sucesso!" -ForegroundColor Green
}
else
{
    Write-Host "ERRO: Docker build falhou!" -ForegroundColor Red
}