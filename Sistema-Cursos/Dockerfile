# Estágio 1: Build da aplicação Angular usando Node 22
FROM node:22-alpine AS build
WORKDIR /app

# Copia os arquivos de configuração do pacote
COPY package*.json ./
RUN npm ci

# Copia o restante do código e gera o build de produção
COPY . .
RUN npm run build -- --configuration=production

# Estágio 2: Servir a aplicação com Nginx
FROM nginx:alpine

# Remove a página padrão do Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copia os arquivos gerados no build para o Nginx
# NOTA: O Angular moderno gera os arquivos em "dist/<nome-do-projeto>/browser"
# Se der erro de "folder not found", verifique o nome da pasta gerada em "dist/"
COPY --from=build /app/dist/Sistema-Cursos/browser /usr/share/nginx/html

# Copia uma configuração customizada do Nginx para suportar rotas do Angular (SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
