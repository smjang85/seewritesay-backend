#!/bin/bash

set -e

# === 1. 변수 설정 ===
KEY_PATH="/Users/enfl171/Documents/aws-keypair/see-write-say-key.pem"
EC2_USER="ec2-user"
EC2_HOST="43.200.138.179"
REMOTE_APP_DIR="~/seewritesay-backend"
ENV_FILE=".env.production"
JAR_PATTERN="build/libs/*.jar"
DOCKER_APP_NAME="seewritesay-app"

# === 2. 백엔드 빌드 ===
echo "🔨 (1) Gradle 빌드 시작..."
./gradlew clean build -x test

# === 3. 환경파일 업로드 ===
echo "🛂 (2) 환경 설정 파일 업로드..."
scp -i "$KEY_PATH" "$ENV_FILE" "$EC2_USER@$EC2_HOST:$REMOTE_APP_DIR/.env"


# === 4. JAR 파일 업로드 ===
echo "🚚 (3) 빌드된 JAR 업로드 중..."
scp -i "$KEY_PATH" $JAR_PATTERN "$EC2_USER@$EC2_HOST:$REMOTE_APP_DIR/build/libs/"

# === 5. EC2 접속 후 도커 컴포즈 재기동 ===
echo "🔁 (4) 서버에서 docker-compose 재시작..."
ssh -i "$KEY_PATH" "$EC2_USER@$EC2_HOST" << EOF
  set -e
  cd $REMOTE_APP_DIR

  echo "🧹 docker-compose down"
  docker-compose down

#(선택) 디스크 공간 확보가 필요할 경우 주석 해제
#echo "🧹 docker system prune -a -f"
#docker system prune -a -f


  echo "🔧 docker-compose build"
  docker-compose build

  echo "🚀 docker-compose up -d"
  docker-compose up -d

  echo "📄 로그 출력 중 (Ctrl+C로 중단 가능)"
  docker logs -f $DOCKER_APP_NAME
EOF

