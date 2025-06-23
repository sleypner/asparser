FROM gradle:jdk22-jammy AS build

WORKDIR /build_src
COPY . .

RUN gradle dependencies --no-daemon

RUN gradle build --no-daemon --stacktrace

RUN ls -la /build_src/build/libs/

FROM openjdk:22-slim-bookworm AS runtime

COPY --from=build /build_src/build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseZGC -XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app.jar"]