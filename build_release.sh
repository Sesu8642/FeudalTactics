#!/bin/bash
SCRIPT_DIR=$(dirname "$0")
cd $SCRIPT_DIR
./gradlew clean
# license report must be gernerated beforehand to be available for the build
./gradlew generateLicenseReport
# --rerun-tasks to make sure version number changes are reflected
./gradlew --rerun-tasks desktop:dist android:assembleRelease
mkdir -p ./release
cp ./desktop/build/libs/*.jar ./release
cp ./android/build/outputs/apk/release/*.apk ./release
