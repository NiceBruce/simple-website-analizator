setup:
	gradle wrapper --gradle-version 7.5.1

clean:
	./gradlew clean

build:
	./gradlew clean build

start:
	APP_ENV=production ./gradlew run

install:
	./gradlew install

start-dist:
	APP_ENV=production ./build/install/java-project-72/bin/java-project-72

generate-migrations:
	./gradlew generateMigrations

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

check-updates:
	./gradlew dependencyUpdates

.PHONY: build