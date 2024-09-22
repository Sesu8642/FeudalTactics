#!/bin/bash
RELEASE_DIR="release"
BINARY_ARTIFACT_NAME="FeudalTactics"
SIGNING_DIR="signing"
KEYSTORE_PATH="$SIGNING_DIR/upload-keystore.jks"
KEYSTORE_PASSWORD_PATH="$SIGNING_DIR/password.txt"
BUILD_TOOLS_PATH_CONTAINING_FILE="$SIGNING_DIR/build_tools_path.txt"
ANDROID_BUILD_BASE_PATH="android/build/outputs"
UNSIGNED_APK_PATH="$ANDROID_BUILD_BASE_PATH/apk/release/android-release-unsigned.apk"
SIGNED_APK_PATH="$ANDROID_BUILD_BASE_PATH/apk/release/android-release-signed.apk"
UNSIGNED_BUNDLE_PATH="$ANDROID_BUILD_BASE_PATH/bundle/release/android-release.aab"
SIGNED_BUNDLE_PATH="$ANDROID_BUILD_BASE_PATH/bundle/release/android-release-signed.aab"
DESKTOP_BUILD_BASE_PATH="lwjgl3/build"
SCRIPT_DIR=$(dirname "$0")

# for debugging
#set -x

cd "$SCRIPT_DIR"
echo "Cleaning project."
./gradlew clean
rm -rf "$RELEASE_DIR"/*

echo "Generating Desktop and Android releases."
# --rerun-tasks to make sure version number changes are reflected
./gradlew --rerun-tasks android:assembleRelease android:bundle lwjgl3:packageLinuxX64 lwjgl3:packageWinX64

# sign apk and bundle
if !(test -f "$KEYSTORE_PATH" && test -f "$KEYSTORE_PASSWORD_PATH" && test -f "$BUILD_TOOLS_PATH_CONTAINING_FILE"); then
	echo "Not signing android releases because keystore, password and/or apksigner path containing files are missing."
else
	build_tools_path=$(cat "$BUILD_TOOLS_PATH_CONTAINING_FILE")
	keystore_pass=$(cat "$KEYSTORE_PASSWORD_PATH")
	apksigner_bin=$build_tools_path/apksigner
	eval $apksigner_bin sign --ks-key-alias upload --ks "$KEYSTORE_PATH" --in "$UNSIGNED_APK_PATH" --out "$SIGNED_APK_PATH" --ks-pass "file:$KEYSTORE_PASSWORD_PATH"
	jarsigner -verbose -keystore "$KEYSTORE_PATH" "$UNSIGNED_BUNDLE_PATH" upload -signedjar "$SIGNED_BUNDLE_PATH" -storepass "$keystore_pass"
fi

# copy release artifacts
echo "Copying release artifacts to $RELEASE_DIR."
mkdir -p "$RELEASE_DIR"
cp "$DESKTOP_BUILD_BASE_PATH"/libs/*.jar "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-desktop.jar
cp "$DESKTOP_BUILD_BASE_PATH"/construo/dist/FeudalTactics-linuxX64.zip "$RELEASE_DIR"
cp "$DESKTOP_BUILD_BASE_PATH"/construo/dist/FeudalTactics-winX64.zip "$RELEASE_DIR"
if test -f "$SIGNED_APK_PATH"; then
	cp "$SIGNED_APK_PATH" "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-android.apk
else
	cp "$UNSIGNED_APK_PATH" "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-android-UNSIGNED.apk
fi
if test -f "$SIGNED_BUNDLE_PATH"; then
	cp "$SIGNED_BUNDLE_PATH" "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-android.aab
else
	cp "$UNSIGNED_BUNDLE_PATH" "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-android-UNSIGNED.aab
fi
