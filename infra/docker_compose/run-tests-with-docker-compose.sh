#!/bin/bash

echo ">>> Остановить Docker Compose"
docker compose down

echo ">>> Docker pull все образы браузеров"
docker pull selenoid/vnc:chrome_120.0
docker pull selenoid/vnc:firefox_89.0
docker pull selenoid/vnc:opera_76.0

TEST_OUTPUT_DIR=target

echo ">>> Поднимаем окружение"
docker compose up -d

#

#echo ">>> Запускаем API тесты..."
#docker run --rm \
#  --network nbank-network \
#  -v "$(pwd -W)/$TEST_OUTPUT_DIR/logs/api":/app/logs \
#  -v "$(pwd -W)/$TEST_OUTPUT_DIR/results/api":/app/target/surefire-reports \
#  -v "$(pwd -W)/$TEST_OUTPUT_DIR/report/api":/app/target/site \
#  -e TEST_PROFILE=api \
#  -e APIBASEURL=http://backend:4111 \
#  -e UIBASEURL=http://nginx:3000 \
#  -e UIREMOTE=http://selenoid:4444 \
#  -e SELENOID_UI_URL=http://localhost:8080 \
#  9708/nbank-tests:20260522_1606
#
#echo ">>> Запускаем UI тесты..."
#docker run --rm \
#  --network nbank-network \
#  -v "$(pwd -W)/$TEST_OUTPUT_DIR/logs/ui":/app/logs \
#  -v "$(pwd -W)/$TEST_OUTPUT_DIR/results/ui":/app/target/surefire-reports \
#  -v "$(pwd -W)/$TEST_OUTPUT_DIR/report/ui":/app/target/site \
#  -e TEST_PROFILE=ui \
#  -e APIBASEURL=http://backend:4111 \
#  -e UIBASEURL=http://nginx \
#  -e UIREMOTE=http://selenoid:4444/wd/hub \
#  -e SELENOID_UI_URL=http://localhost:8080 \
#  9708/nbank-tests:20260522_1606
#
#echo ">>> Останавливаем тестовое окружение"
#docker compose down