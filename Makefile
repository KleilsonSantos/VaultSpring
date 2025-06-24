# =====================
# 🌍 Environment Variables
# =====================
PROFILE_DEP_CHECK=-Pdependency-check -Dnvd.api.key=$$NVD_API_KEY

SONAR_URL=http://localhost:9000
SONAR_TOKEN?=$(shell echo $$SONAR_TOKEN)

# =====================
# ⚙️ Maven Commands
# =====================
MVN=mvn
MVN_UNIT=$(MVN) clean test
MVN_VERIFY_IT=$(MVN) verify -Pintegration-tests
MVN_FULL_BUILD=$(MVN) clean verify -Pintegration-tests install
MVN_CLEAN_TEST_JACOCO=$(MVN) clean test jacoco:report

# =====================
# 🔐 Sonar Analysis
# =====================
SONAR_SCAN_CMD=$(MVN) sonar:sonar \
  -Dsonar.projectKey=vaultspring \
  -Dsonar.host.url=$(SONAR_URL) \
  -Dsonar.token=$(SONAR_TOKEN) \
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

sonar:
	@echo "📊 Running Sonar analysis..."
	$(SONAR_SCAN_CMD)

# =====================
# 🔍 OWASP Dependency-Check
# =====================
check-sec:
	@echo "🔍 Running OWASP Dependency-Check..."
	$(MVN) verify $(PROFILE_DEP_CHECK)

dependency-check:
	docker run --rm \
		-e NVD_API_KEY=$(NVD_API_KEY) \
		-v $(CURDIR):/src \
		-v $(CURDIR)/dependency-check-data:/usr/share/dependency-check/data \
		owasp/dependency-check \
		--project "Meu Projeto" \
		--scan /src \
		--format "HTML"


check-sec-dev:
	@echo "🔍 Running OWASP Dependency-Check for Dev..."
	$(MVN) verify $(PROFILE_DEP_CHECK) -Pdev

check-sec-prod:
	@echo "🔍 Running OWASP Dependency-Check for Prod..."
	$(MVN) verify $(PROFILE_DEP_CHECK) -Pprod

report-sec:
	@echo "📂 Opening security report..."
	xdg-open target/dependency-check-report/dependency-check-report.html || open target/dependency-check-report/dependency-check-report.html

# =====================
# 🧪 Testes de Segurança Ofensiva
# =====================
sql-injection-test:
	@echo "🐍 Testando SQL Injection com sqlmap..."
	sqlmap -u "http://localhost:8080/api/v1/users/1" --risk=3 --level=5 --batch

xss-test:
	@echo "💀 Testando XSS com curl..."
	curl -X POST http://localhost:8080/api/v1/comments \
	 -H "Content-Type: application/json" \
	 -d "{\"comment\": \"<script>alert('xss')</script>\"}"

ddos-test:
	@echo "🌪️ Testando DDoS com ab (ApacheBench)..."
	ab -n 10000 -c 500 http://localhost:8080/actuator/health

zap-scan:
	@echo "🕷️ Executando ZAP Scan..."
	zap-baseline.py -t http://localhost:8080 -r zap-report.html

jwt-verify:
	@echo "🔐 Verificando JWT..."
	curl -H "Authorization: Bearer $$(cat jwt.txt)" http://localhost:8080/api/v1/users/1

# =====================
# 🐦 Flyway Database Migration
# =====================
flyway-clean-dev:
	@echo "🧹 Limpando banco de dados com Flyway (dev)..."
	$(MVN) flyway:clean -Pflyway-dev

flyway-info-prod:
	@echo "ℹ️  Exibindo informações do Flyway (prod)..."
	$(MVN) flyway:info -Pflyway-prod

flyway-repair-prod:
	@echo "🛠️  Reparando metadados do Flyway (prod)..."
	$(MVN) flyway:repair -Pflyway-prod

flyway-migrate-prod:
	@echo "🚀 Executando migrações Flyway (prod)..."
	$(MVN) flyway:migrate -Pflyway-prod

# =====================
# 🧱 Build & Run
# =====================
build:
	@echo "📦 Building project..."
	$(MVN) clean package

build-clean-install:
	@echo "📦 Building project with unit tests..."
	$(MVN) clean install

package:
	@echo "📦 Packaging project..."
	$(MVN) clean package -DskipTests

run:
	@echo "🚀 Running application..."
	$(MVN) spring-boot:run

run-dev:
	@echo "🚀 Running application in development mode..."
	$(MVN) spring-boot:run spring-boot:run -Dspring.profiles.active=dev

run-prod:
	@echo "🚀 Running application in production mode..."
	mvn spring-boot:run -Dspring.profiles.active=prod

# =====================
# 🧹 Clean Commands
# =====================
clean:
	@echo "🧹 Cleaning project..."
	$(MVN) clean

clean-test-jacoco:
	@echo "🧹 Cleaning project and running unit tests..."
	$(MVN_CLEAN_TEST_JACOCO)

# =====================
# 🧪 Test Commands
# =====================
test-unit:
	@echo "🧪 Running unit tests only (Surefire)..."
	$(MVN_UNIT)

test-it:
	@echo "🧪 Running integration tests only (Failsafe)..."
	$(MVN_VERIFY_IT)

test-all:
	@echo "🧪 Running unit and integration tests..."
	$(MVN_FULL_BUILD)

# =====================
# 📈 Code Quality
# =====================
coverage:
	@echo "📈 Generating JaCoCo coverage report..."
	$(MVN) clean verify jacoco:report

# =====================
# 📦 Maven Wrapper
# =====================
wrapper:
	@echo "🔧 Gerando Maven Wrapper (mvnw)..."
	$(MVN) -N io.takari:maven:wrapper

# =====================
# ✅ Verification
# =====================
verify:
	@echo "🔍 Verifying project..."
	$(MVN) verify

.PHONY: sonar check-sec check-sec-dev check-sec-prod report-sec \
        sql-injection-test xss-test ddos-test zap-scan jwt-verify \
        build package run clean clean-test-jacoco test-unit test-it test-all \
        coverage verify flyway-info flyway-clean flyway-repair flyway-migrate
