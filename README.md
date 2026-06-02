# 🌱 Agrotech - Sistema de Irrigação Inteligente

### Descrição da Solução

O **Agrotech** é um sistema de agricultura de precisão que integra sensores IoT (ESP32) e dados de satélites (NASA/ESA) para otimizar a irrigação de plantações. O sistema recebe dados de umidade e temperatura do solo, cruza com previsões meteorológicas orbitais e decide automaticamente se deve ativar ou bloquear o sistema de irrigação.

### Funcionalidades Principais
- Recebimento de dados telemétricos de sensores IoT (simulado)
- Integração com previsões climáticas via satélite (simulado)
- Tomada de decisão inteligente para irrigação
- CRUD completo para registros de solo
- Persistência de dados em banco MySQL

---

## Tutorial de Execução

### Pré-requisitos

- VM Linux na Azure (recomendado para melhor performance: 4GB RAM, 2 vCPUs)
- Docker e Docker Compose instalados
- Git instalado

### Passo 1: Clonar o Repositório

```
# Clonar o projeto
git clone https://github.com/seu-usuario/agrotech-561497.git
cd agrotech-561497
```
## Verificar estrutura do projeto
```
ls -la
```
Deve conter: pom.xml, src/, Dockerfile, docker-compose.yml

### Passo 3: Construir e Subir os Containers


```
# Construir a imagem da aplicação
sudo docker compose build

# Subir os containers em background
sudo docker compose up -d

# Verificar se os containers estão rodando
sudo docker ps

```
Saída esperada:

```
CONTAINER ID   IMAGE                        NAMES          STATUS
xxxxxxxxxxxx   agrotech-561497-app-561497   app-561497     Up
xxxxxxxxxxxx   mysql:8.0                    db-561497      Up (healthy)
```

### Passo 4: Verificar Logs

```
# Logs da aplicação
sudo docker logs app-561497 --tail 50

# Logs do banco de dados
sudo docker logs db-561497 --tail 30
```

## Passo 5: Executar Testes da API

Após os containers estarem rodando, realize os testes abaixo para validar o CRUD completo da aplicação.

### 6.1 CREATE - Criar Registro de Solo

Registra os dados de umidade e temperatura enviados pelo sensor IoT.

```
curl -X POST http://localhost:8080/api/agro/solo \
  -H "Content-Type: application/json" \
  -d '{"umidade":35.5,"temperatura":26.0,"dispositivoId":"ESP32-FAZENDA-01"}'
```

Saída esperada:

```
Ação: SISTEMA DE IRRIGAÇÃO ATIVADO. Solo seco e sem previsão de chuva.Saída esperada:

Ação: SISTEMA DE IRRIGAÇÃO ATIVADO. Solo seco e sem previsão de chuva.
```

### 6.2 CREATE - Criar Previsão de Satélite

Registra os dados de previsão climática orbital.

```
curl -X POST http://localhost:8080/api/agro/satelite \
  -H "Content-Type: application/json" \
  -d '{"regiao":"Setor_A_Principal","chuvaIminente":false}'
```

Saída esperada:

```
{
  "id": 1,
  "regiao": "Setor_A_Principal",
  "chuvaIminente": false,
  "dataPrevisao": "2026-06-02"
}
```

### 6.3 CREATE - Segundo Registro de Solo (Umidade Baixa)
```
curl -X POST http://localhost:8080/api/agro/solo \
  -H "Content-Type: application/json" \
  -d '{"umidade":25.0,"temperatura":24.0,"dispositivoId":"ESP32-FAZENDA-02"}'
```
Saída esperada:

```
Ação: SISTEMA DE IRRIGAÇÃO ATIVADO. Solo seco e sem previsão de chuva.
```
### 6.4 READ - Listar Todos os Registros de Solo
```
curl http://localhost:8080/api/agro/solo
```

Saída esperada:
```
[
  {
    "id": 1,
    "umidade": 35.5,
    "temperatura": 26.0,
    "dataLeitura": "2026-06-02T21:30:00",
    "dispositivoId": "ESP32-FAZENDA-01",
    "previsao": null
  },
  {
    "id": 2,
    "umidade": 25.0,
    "temperatura": 24.0,
    "dataLeitura": "2026-06-02T21:31:00",
    "dispositivoId": "ESP32-FAZENDA-02",
    "previsao": null
  }
]
```

### 6.5 READ - Buscar Registro por ID
```
curl http://localhost:8080/api/agro/solo/1
```

Saída esperada:
```
{
  "id": 1,
  "umidade": 35.5,
  "temperatura": 26.0,
  "dataLeitura": "2026-06-02T21:30:00",
  "dispositivoId": "ESP32-FAZENDA-01",
  "previsao": null
}
```

### 6.6 UPDATE - Atualizar Registro de Solo

```
curl -X PUT http://localhost:8080/api/agro/solo/1 \
  -H "Content-Type: application/json" \
  -d '{"umidade":80.0,"temperatura":30.0,"dispositivoId":"ESP32-UPDATED"}'
```

Saída esperada:
```
{
  "id": 1,
  "umidade": 80.0,
  "temperatura": 30.0,
  "dataLeitura": "2026-06-02T21:35:00",
  "dispositivoId": "ESP32-UPDATED",
  "previsao": null
}
```

### 6.7 READ - Verificar Registro Atualizado
```
curl http://localhost:8080/api/agro/solo/1
```

Saída esperada:
```
{
  "id": 1,
  "umidade": 80.0,
  "temperatura": 30.0,
  "dataLeitura": "2026-06-02T21:35:00",
  "dispositivoId": "ESP32-UPDATED",
  "previsao": null
}
```
### 6.8 DELETE - Remover Registro de Solo
```
curl -X DELETE http://localhost:8080/api/agro/solo/1
```

Saída esperada: (nenhuma saída - status 204 No Content)

### 6.9 READ - Confirmar Exclusão
```
curl http://localhost:8080/api/agro/solo
```

Saída esperada:
```
[
  {
    "id": 2,
    "umidade": 25.0,
    "temperatura": 24.0,
    "dataLeitura": "2026-06-02T21:31:00",
    "dispositivoId": "ESP32-FAZENDA-02",
    "previsao": null
  }
]
```

### 6.10 Teste de Decisão Inteligente - Irrigação Bloqueada
Para testar a regra de negócio (chuva iminente bloqueia irrigação):

```
# Criar previsão com chuva iminente
curl -X POST http://localhost:8080/api/agro/satelite \
  -H "Content-Type: application/json" \
  -d '{"regiao":"Setor_A_Principal","chuvaIminente":true}'

# Enviar dados de solo seco
curl -X POST http://localhost:8080/api/agro/solo \
  -H "Content-Type: application/json" \
  -d '{"umidade":30.0,"temperatura":25.0,"dispositivoId":"ESP32-TESTE"}'
```

Saída esperada:
```
SOLICITAÇÃO DE REGA RECEBIDA. Ação: REGA BLOQUEADA. Motivo: Dados orbitais indicam chuva iminente.
```

### 6.11 Resumo dos Testes Realizados

| Operação | Método | Endpoint | Status |
|----------|--------|----------|--------|
| Criar registro solo | POST | `/api/agro/solo` | ✅ |
| Criar previsão satélite | POST | `/api/agro/satelite` | ✅ |
| Listar registros | GET | `/api/agro/solo` | ✅ |
| Buscar por ID | GET | `/api/agro/solo/{id}` | ✅ |
| Atualizar registro | PUT | `/api/agro/solo/{id}` | ✅ |
| Deletar registro | DELETE | `/api/agro/solo/{id}` | ✅ |
| Regra de negócio | POST | `/api/agro/solo` | ✅ |
