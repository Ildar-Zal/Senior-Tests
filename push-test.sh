#Настройка
IMAGE_NAME=nbank-tests
DOCKER_HUB_USER=9708
TAG=$(date +"%Y%m%d_%H%M")

# ЗАГРУЗКА ТОКЕНА: Если файл .env существует, читаем его
if [ -f .env ]; then
  export $(echo $(cat .env | sed 's/#.*//g' | xargs) | envsubst)
fi

# Авторизация
echo "$DOCKERHUB_TOKEN" | docker login -u $DOCKER_HUB_USER --password-stdin

# Тегирование и пуш
docker tag $IMAGE_NAME $DOCKER_HUB_USER/$IMAGE_NAME:$TAG
docker push $DOCKER_HUB_USER/$IMAGE_NAME:$TAG

# Финальное сообщение
echo "Чтобы скачать наш образ, введите команду: docker pull $DOCKER_HUB_USER/$IMAGE_NAME:$TAG"