FROM node:18-bullseye
WORKDIR /app
COPY . .
RUN npm install
EXPOSE 5173:5173
RUN ls
RUN npm run build
RUN npm install -g serve
ENTRYPOINT ["serve", "-s", "dist", "-l", "5173"]