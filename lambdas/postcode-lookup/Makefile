.PHONY: build-PostcodeLookupFunction clean

IMAGE_NAME=native
OUTPUT_ZIP=$(ARTIFACTS_DIR)/bootstrap
NATIVE_ARTIFACTS_DIR=native-artifacts

build-PostcodeLookupFunction: $(OUTPUT_ZIP)

$(BUILD_DIR)/$(IMAGE_NAME): $(BUILD_DIR) $(wildcard src/**/*.java)
	gradle build
	docker compose up --build

$(OUTPUT_ZIP): $(BUILD_DIR)/$(IMAGE_NAME) $(NATIVE_ARTIFACTS_DIR)
	gradle clean
	cp native-artifacts/bootstrap $(ARTIFACTS_DIR)
	chmod +x $(ARTIFACTS_DIR)/bootstrap
	zip $(ARTIFACTS_DIR)/native-image.zip $(ARTIFACTS_DIR)/bootstrap
