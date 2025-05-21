FROM gradle:jdk22-jammy AS build

COPY ./ /build_src

WORKDIR /build_src

RUN gradle build

FROM openjdk:22-slim-bookworm AS runtime

COPY --from=build /build_src/build/libs/parserarticles-1.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
