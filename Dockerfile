FROM openjdk:15-alpine

COPY ./LicenseServer-0.0.1-SNAPSHOT.jar /opt/diplom.jar
COPY ./diplomKeyStore /opt/diplomKeyStore

ENV PORT 5000

EXPOSE $PORT

CMD java -Dserver.port=${PORT} -Xmx2G -jar /opt/diplom.jar