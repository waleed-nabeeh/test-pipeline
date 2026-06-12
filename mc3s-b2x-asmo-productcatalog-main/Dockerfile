#
# Create a docker image
#
FROM maven:3.8.6-jdk-8-slim

EXPOSE 9091 8888 1099 8081

RUN mkdir -p /app/mc3s-b2b-webshop/workspace
WORKDIR /app/mc3s-b2b-webshop/workspace

CMD ["sh","startWebshop.sh"]
