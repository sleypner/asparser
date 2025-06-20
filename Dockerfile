FROM gradle:jdk21-jammy AS build

COPY ./ /build_src

WORKDIR /build_src

RUN gradle -no-daemon --stacktrace --info --debug --refresh-dependencies --no-daemon && \ls -la /app/build/libs/ build

FROM openjdk:21-slim-bookworm AS runtime

COPY --from=build /build_src/build/libs/parserarticles-1.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
