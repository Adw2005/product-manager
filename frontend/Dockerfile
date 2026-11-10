# Servidor estático leve com Nginx para servir o frontend puro (HTML5, CSS3 e JavaScript Vanilla)
FROM nginx:1.27-alpine

COPY index.html /usr/share/nginx/html/index.html
COPY docs.html /usr/share/nginx/html/docs.html
COPY css /usr/share/nginx/html/css
COPY js /usr/share/nginx/html/js
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
