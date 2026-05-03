.PHONY: init-spring init-fastapi init-next check-standards

init-spring:
	@echo "🚀 Scaffolding Spring Boot API..."
	cp -r templates/spring-api ./new-project
	cd new-project && mv src/main/java/com/felixmurcia/api src/main/java/com/felixmurcia/$(PROJECT_NAME)
	@echo "✅ Done. Edit pom.xml and application.yml"

init-fastapi:
	@echo "🐍 Scaffolding FastAPI..."
	cp -r templates/fastapi-api ./new-project
	@echo "✅ Done. Run 'poetry install' in new-project"

init-next:
	@echo "⚛️ Scaffolding Next.js..."
	cp -r templates/next-frontend ./new-project
	cd new-project && npm install
	@echo "✅ Done. Run 'npm run dev'"

check-standards:
	@echo "🔍 Validating architecture compliance..."
	@find . -name "*.java" -exec grep -l "org.springframework.stereotype.Service" {} \; | xargs -I {} grep -L "interface" {} || echo "✅ Java Services look good"
	@echo "⚠️ Full validation requires agent execution: /check-arch"
