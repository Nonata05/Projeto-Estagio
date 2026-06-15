  # Estágio 1: Build e Publicação da aplicação .NET 10 (Preview)
FROM mcr.microsoft.com/dotnet/sdk:10.0-preview AS build
WORKDIR /src

# Copia o arquivo de projeto (.csproj) e restaura as dependências
COPY AuthServer.csproj ./
RUN dotnet restore

# Copia todo o código do projeto e publica em modo Release
COPY . ./
RUN dotnet publish AuthServer.csproj -c Release -o /app/publish

# Estágio 2: Execução da aplicação em imagem .NET 10 (Preview)
FROM mcr.microsoft.com/dotnet/aspnet:10.0-preview AS runtime
WORKDIR /app
COPY --from=build /app/publish .

# Deixe o .NET usar a porta padrão dele (8080), que é mais seguro e compatível
EXPOSE 8080

ENTRYPOINT ["dotnet", "AuthServer.dll"]
