#!/bin/bash

echo "============================================"
echo "🚀 DESPLIEGUE RÁPIDO - Oh Hell!"
echo "============================================"
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Variables
BACKEND_DIR=~/IdeaProjects/OHHELL/ohhell-backend/ohhell-api
TOMCAT_DIR=~/Servidores/apache-tomee-webprofile-10.1.3

echo -e "${YELLOW}📦 Paso 1: Compilando backend...${NC}"
cd $BACKEND_DIR
mvn clean package -q
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Backend compilado${NC}"
else
    echo -e "${RED}❌ Error compilando${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}🛑 Paso 2: Deteniendo Tomcat...${NC}"
cd $TOMCAT_DIR/bin
./catalina.sh stop 2>&1 | grep -i "stop" || echo "Deteniendo..."
sleep 5
echo -e "${GREEN}✅ Tomcat detenido${NC}"

echo ""
echo -e "${YELLOW}🗑️  Paso 3: Limpiando...${NC}"
rm -f $TOMCAT_DIR/webapps/ohhell-api.war
rm -rf $TOMCAT_DIR/webapps/ohhell-api
echo -e "${GREEN}✅ Limpiado${NC}"

echo ""
echo -e "${YELLOW}📋 Paso 4: Copiando WAR...${NC}"
cp $BACKEND_DIR/target/ohhell-api.war $TOMCAT_DIR/webapps/
if [ -f $TOMCAT_DIR/webapps/ohhell-api.war ]; then
    echo -e "${GREEN}✅ WAR copiado${NC}"
    ls -lh $TOMCAT_DIR/webapps/ohhell-api.war
else
    echo -e "${RED}❌ Error copiando WAR${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}🚀 Paso 5: Iniciando Tomcat...${NC}"
./catalina.sh start
sleep 3
echo -e "${GREEN}✅ Tomcat iniciado${NC}"

echo ""
echo -e "${YELLOW}⏳ Paso 6: Esperando despliegue (20 segundos)...${NC}"
for i in {20..1}; do
    printf "\r   %2d segundos restantes..." $i
    sleep 1
done
echo ""

echo ""
echo "============================================"
echo -e "${GREEN}✅ DESPLIEGUE COMPLETADO${NC}"
echo "============================================"
echo ""
echo "🌐 URL: http://localhost:8080/ohhell/"
echo ""
echo "🧪 AHORA:"
echo "   1. Abre el navegador en modo incógnito"
echo "   2. Crea una partida NUEVA"
echo "   3. Prueba con 2 jugadores"
echo ""
echo "📊 Logs:"
echo "   tail -f $TOMCAT_DIR/logs/catalina.$(date +%Y-%m-%d).log"
echo ""

