# базовые докер образ
# каждый раз с нуля строить базовый (java, mvn, git)
# голый докер образ и устаовить java, maven
# ЕСЛИ МОЖНО СОЗДАТЬ ОБРАЗ ПОВЕРХ ДРУГОГО ОБРАЗА, ГДЕ ВСЕ УЖЕ УСТАНОВЛЕНО
# -> МАРКЕТПЛЕС ВСЕХ ДОКЕР ОБРАЗОВ - docker hub

FROM maven:3.9.16-eclipse-temurin-21

#Дефолтные значения агрументов
ARG TEST_PROFILE=api
ARG APIBASEURL=http://host.docker.internal:4111
ARG BASEUIURL=http://localhost:3000

#Переменные окружения для контейнера
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV BASEUIURL=${BASEUIURL}

#Работаетм из папки /app
WORKDIR /app

#Копируем помник
COPY pom.xml .

#Загружаем зависимость и кэшируем
RUN mvn dependency:go-offline

#Копируем весь проект
COPY . .

# теперь внутри есть зависимости, есть весь проект и мы готовы запускать тесты

USER root

# mvn test -P api
# mvn -DskipTests=true surfire-report:report
# лог выводился не в консоль, а в файл
# bash file

CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile: ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
   } > /app/logs/run.log 2>&1"



