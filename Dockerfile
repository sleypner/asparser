FROM gradle:jdk21-jammy AS build

WORKDIR /build_src
COPY . .

RUN gradle build && \ls -la /build_src/build/libs/

FROM openjdk:21-slim-bookworm AS runtime

COPY --from=build /build_src/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
