# Environment variables

PROFILE_DEP_CHECK=-Pdependency-check

# === Maven Commands ===
MVN=mvn
MVN_UNIT=$(MVN) clean test
MVN_VERIFY_IT=$(MVN) verify -Pintegration-tests
MVN_FULL_BUILD=$(MVN) clean verify -Pintegration-tests install
MVN_CLEAN_TEST_JACOCO=$(MVN) clean test jacoco:report

# === Sonar Analysis Commands ===
SONAR_URL=http://localhost:9000
SONAR_TOKEN?=$(shell echo $$SONAR_TOKEN)
SONAR_SCAN_CMD=$(MVN) sonar:sonar \
  -Dsonar.projectKey=vaultspring \
  -Dsonar.host.url=$(SONAR_URL) \
  -Dsonar.token=$(SONAR_TOKEN) \
  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# === Security Scanning ===
check-sec:
	@echo "🔍 Running OWASP Dependency-Check..."
	$(MVN) verify -Pdependency-check

check-sec-dev:
	@echo "🔍 Running OWASP Dependency-Check for Dev..."
	$(MVN) verify -Pdependency-check -Pdev

check-sec-prod:
	@echo "🔍 Running OWASP Dependency-Check for Prod..."
	$(MVN) verify -Pdependency-check -Pprod

report-sec:
	@echo "📂 Opening security report..."
	xdg-open target/dependency-check-report/dependency-check-report.html || open target/dependency-check-report/dependency-check-report.html

.PHONY: check-sec check-sec-dev check-sec-prod report-sec

# === Build Commands ===
build:
	@echo "📦 Building project with unit tests..."
	$(MVN) clean install

package:
	@echo "📦 Packaging project..."
	$(MVN) clean package

run:
	@echo "🚀 Running application..."
	$(MVN) spring-boot:run

# === Clean Commands ===
clean:
	@echo "🧹 Cleaning project..."
	$(MVN) clean

clean-test-jacoco:
	@echo "🧹 Cleaning project and running unit tests..."
	$(MVN_CLEAN_TEST_JACOCO)

# === Test Commands ===
test-unit:
	@echo "🧪 Running unit tests only (Surefire)..."
	$(MVN_UNIT)

test-it:
	@echo "🧪 Running integration tests only (Failsafe)..."
	$(MVN_VERIFY_IT)

test-all:
	@echo "🧪 Running unit and integration tests..."
	$(MVN_FULL_BUILD)

# === Code Quality ===
coverage:
	@echo "📈 Generating JaCoCo coverage report..."
	$(MVN) clean verify jacoco:report

# === Verification ===
verify:
	@echo "🔍 Verifying project..."
	$(MVN) verify