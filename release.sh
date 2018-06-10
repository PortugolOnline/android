#!/bin/bash

# Assinatura do APK: requisito para publicação na Google Play Store
# https://stackoverflow.com/a/47356720/1657502

# Antes de assinar, executar:
# $ keytool -genkey -v -keystore ~/portugol.keystore -alias portugol_android -keyalg RSA -validity 10000

../gradlew assembleRelease \
 -Pandroid.injected.signing.store.file=$KEYFILE \
 -Pandroid.injected.signing.store.password=$STORE_PASSWORD \
 -Pandroid.injected.signing.key.alias=$KEY_ALIAS \
 -Pandroid.injected.signing.key.password=$KEY_PASSWORD
