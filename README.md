# 🌱 Agrotech - Sistema de Irrigação Inteligente

### Descrição da Solução

O **Agrotech** é um sistema de agricultura de precisão que integra sensores IoT (ESP32) e dados de satélites (NASA/ESA) para otimizar a irrigação de plantações. O sistema recebe dados de umidade e temperatura do solo, cruza com previsões meteorológicas orbitais e decide automaticamente se deve ativar ou bloquear o sistema de irrigação.
O problema que buscamos resolver é a falta de informação em tempo real para agricultores sobre as condições do solo e clima, ajudando na tomada de decisão sobre irrigação e plantio. Essa solução também contribui diretamente com o meio ambiente, considerando que [**a agricultura é responsável por cerca de 70% de todo o consumo de água doce do planeta.**](#fonte-agua) Com dados precisos, é possível evitar o desperdício de água na irrigação, reduzindo a pressão sobre os recursos hídricos e tornando a produção agrícola mais sustentável.

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
git clone https://github.com/isismodd/01GSDevops-561497.git
cd 01GSDevops-561497
```
### Verificar estrutura do projeto
```
ls -la
```
Deve conter: docker-compose.yml  Dockerfile  mvnw  mvnw.cmd  pom.xml  src  target

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
xxxxxxxxxxxx   01GSDevops-561497-app-561497   app-561497     Up
xxxxxxxxxxxx   mysql:8.0                    db-561497      Up (healthy)
```
Containers em modo background

### Passo 4: Verificar Logs dos containers

```
# Logs da aplicação
sudo docker logs app-561497 --tail 50

# Logs do banco de dados
sudo docker logs db-561497 --tail 30
```

### Passo 5: Acessar o Container da Aplicação

```
# Entrar no container
sudo docker exec -it app-561497 /bin/sh
```
Evidência - Dentro do container:
```
# Dentro do container:
pwd
ls -la
whoami
exit
```
Usuário não privilegiado (appuser) | Diretório de trabalho (/app)


### Passo 6: Verificar Volume Nomeado
```
# Listar volumes
sudo docker volume ls
```
Evidência:
```
DRIVER    VOLUME NAME
local     mysql_data_561497
```

Inspecionar o volume
```
sudo docker volume inspect mysql_data_561497
```
Evidência:
```
[
    {
        "CreatedAt": "2026-06-02T21:15:00Z",
        "Driver": "local",
        "Name": "mysql_data_561497",
        "Mountpoint": "/var/lib/docker/volumes/mysql_data_561497/_data"
    }
]
```
Volume nomeado para persistência

### Passo 7: Verificar Variáveis de Ambiente e Portas
```
# Variáveis de ambiente da aplicação
sudo docker exec app-561497 env | grep -E "DB_HOST|SPRING_DATASOURCE"
```
Evidência:
```
DB_HOST=db-561497
SPRING_DATASOURCE_URL=jdbc:mysql://db-561497:3306/01GSDevops
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root123
```
Portas expostas
```
sudo docker port app-561497
sudo docker port db-561497
```
Evidência:
```
8080/tcp -> 0.0.0.0:8080
3306/tcp -> 0.0.0.0:3306
```
Variáveis de ambiente configuradas | Portas expostas

## Passo 8: Executar Testes da API com SELECT no Banco


- 8.1 CREATE - Inserir Registro de Solo

API:
```
curl -X POST http://localhost:8080/api/agro/solo \
  -H "Content-Type: application/json" \
  -d '{"umidade":35.5,"temperatura":26.0,"dispositivoId":"ESP32-EVIDENCIA"}'
```
Saída da API:
```
Ação: SISTEMA DE IRRIGAÇÃO ATIVADO. Solo seco e sem previsão de chuva.
```

- 8.2 Conectar ao Banco para Evidências
```
# Abrir conexão com o MySQL
sudo docker exec -it db-561497 mysql -uroot -proot123
```

- SELECT no Banco (Evidência):
```
USE agrotech;
SELECT * FROM tab_registro_solo ORDER BY id_registro DESC LIMIT 1;
```
Saída do SELECT:
```
+-------------+-------------+-----------------+---------------------+---------------------+
| id_registro | num_umidade | num_temperatura | dat_leitura         | id_dispositivo      |
+-------------+-------------+-----------------+---------------------+---------------------+
|           1 |        35.5 |            26.0 | 2026-06-02 21:30:00 | ESP32-EVIDENCIA     |
+-------------+-------------+-----------------+---------------------+---------------------+
```
CREATE evidenciado com SELECT

- 8.3 CREATE - Inserir Previsão de Satélite
API:
```
curl -X POST http://localhost:8080/api/agro/satelite \
  -H "Content-Type: application/json" \
  -d '{"regiao":"Setor_A_Principal","chuvaIminente":false}'
```
Saída da API:
```
{
  "id": 1,
  "regiao": "Setor_A_Principal",
  "chuvaIminente": false,
  "dataPrevisao": "2026-06-02"
}
```
SELECT no Banco (Evidência):
```
SELECT * FROM tab_previsao_satelite;
```
Saída do SELECT:
```
+--------------+--------------------+--------------------+------------------+
| id_previsao  | txt_regiao         | bol_chuva_iminente | dat_previsao     |
+--------------+--------------------+--------------------+------------------+
|            1 | Setor_A_Principal  |                  0 | 2026-06-02       |
+--------------+--------------------+--------------------+------------------+
```
CREATE da segunda tabela evidenciado

- 8.4 READ - Listar Todos os Registros
API:
```
curl http://localhost:8080/api/agro/solo
```
Saída da API:
```
[
  {
    "id": 1,
    "umidade": 35.5,
    "temperatura": 26.0,
    "dataLeitura": "2026-06-02T21:30:00",
    "dispositivoId": "ESP32-EVIDENCIA",
    "previsao": null
  }
]
```
SELECT no Banco (Evidência):
```
SELECT id_registro, num_umidade, num_temperatura, id_dispositivo FROM tab_registro_solo;
```
Saída do SELECT:
```
+-------------+-------------+-----------------+---------------------+
| id_registro | num_umidade | num_temperatura | id_dispositivo      |
+-------------+-------------+-----------------+---------------------+
|           1 |        35.5 |            26.0 | ESP32-EVIDENCIA     |
+-------------+-------------+-----------------+---------------------+
```
READ evidenciado com SELECT

- 8.5 UPDATE - Atualizar Registro
API:
```
curl -X PUT http://localhost:8080/api/agro/solo/1 \
  -H "Content-Type: application/json" \
  -d '{"umidade":80.0,"temperatura":32.0,"dispositivoId":"ESP32-UPDATED"}'
```
Saída da API:
```
{
  "id": 1,
  "umidade": 80.0,
  "temperatura": 32.0,
  "dataLeitura": "2026-06-02T21:35:00",
  "dispositivoId": "ESP32-UPDATED",
  "previsao": null
}
```
SELECT no Banco (Evidência):
```
SELECT * FROM tab_registro_solo WHERE id_registro = 1;
```
Saída do SELECT:
```
+-------------+-------------+-----------------+---------------------+------------------+
| id_registro | num_umidade | num_temperatura | dat_leitura         | id_dispositivo   |
+-------------+-------------+-----------------+---------------------+------------------+
|           1 |        80.0 |            32.0 | 2026-06-02T21:35:00 | ESP32-UPDATED    |
+-------------+-------------+-----------------+---------------------+------------------+
```
UPDATE evidenciado com SELECT

- 8.6 DELETE - Remover Registro
API:
```
curl -X DELETE http://localhost:8080/api/agro/solo/1
```

Saída da API: (nenhuma saída - status 204 No Content)

SELECT no Banco (Evidência):
```
SELECT * FROM tab_registro_solo WHERE id_registro = 1;
```
Saída do SELECT:
```
Empty set
```
DELETE evidenciado com SELECT

- 8.7 Evidência das Duas Tabelas com Relacionamento
```
SHOW TABLES;
```
Saída:
```
+-----------------------+
| Tables_in_agrotech    |
+-----------------------+
| tab_previsao_satelite |
| tab_registro_solo     |
+-----------------------+
```
```
SELECT * FROM tab_registro_solo;
SELECT * FROM tab_previsao_satelite;
```
Saída:
```
TAB_REGISTRO_SOLO (vazia após DELETE)
TAB_PREVISAO_SATELITE:
+--------------+--------------------+--------------------+------------------+
| id_previsao  | txt_regiao         | bol_chuva_iminente | dat_previsao     |
+--------------+--------------------+--------------------+------------------+
|            1 | Setor_A_Principal  |                  0 | 2026-06-02       |
+--------------+--------------------+--------------------+------------------+
```
Duas tabelas com relacionamento evidenciadas

## 👥 Equipe HARPI

| Nome | RM |
|------|-----|
| Ana Clara de Oliveira Nascimento | RM 561957 |
| Isis Macedo | RM 561497 |
| Henrique Pereira | RM 565608 |
| Pedro Mariutti | RM 75999 |
| Rafael Carvalho Meireles | RM 563413 |

---

## Referências

<span id="fonte-agua"></span>

**Fonte - Consumo de água pela agricultura:**

> Rodrigues-Silva, F. et al. (2025). "Recycling nutrients: The promise and perils of wastewater use in global and Brazilian agriculture." *Agricultural Water Management*, Volume 321. DOI: 10.1016/j.agwat.2025.109901

> *"Agriculture is the world's largest consumer of freshwater, accounting for nearly 70% of global water withdrawals"* (Barbosa et al., 2017; Leonel and Tonetti, 2021)

[🔝 Voltar ao topo](#)
