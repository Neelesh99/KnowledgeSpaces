FROM node:18-bullseye
WORKDIR /app
COPY frontend/frontend-app .
RUN ls
RUN npm install
EXPOSE 80:80
ARG VITE_BACKEND_PREFIX
ENV VITE_BACKEND_PREFIX=$VITE_BACKEND_PREFIX
RUN echo $VITE_BACKEND_PREFIX
RUN npm run build
RUN npm install -g serve
ENTRYPOINT ["serve", "-s", "dist", "-l", "80"]