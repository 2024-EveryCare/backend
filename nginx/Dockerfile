FROM nginx:latest
LABEL authors="dokyounglee"

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d

CMD ["nginx", "-g", "deamon off;"]

EXPOSE 80