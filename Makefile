# Variáveis
MVN=mvn
PROFILE_DEP_CHECK=-Pdependency-check

check-sec:
	@echo "🔍 Rodando OWASP Dependency-Check..."
	mvn verify -P security

report-sec:
	@echo "📂 Abrindo relatório de segurança..."
	xdg-open target/dependency-check-report/dependency-check-report.html || open target/dependency-check-report/dependency-check-report.html

.PHONY: check-sec report-sec

# Comandos principais
build:
	$(MVN) clean install

package:
	$(MVN) clean package

run:
	$(MVN) spring-boot:run

test:
	$(MVN) test

verify:
	$(MVN) verify

# Análise de segurança (manual)
sec-check:
	$(MVN) verify $(PROFILE_DEP_CHECK)

clean:
	$(MVN) clean
