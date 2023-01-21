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
PACKR_VERSION=4.0.0
PACKR_EXE_NAME="packr-all-$PACKR_VERSION.jar"
PACKR_DL_LINK="https://github.com/libgdx/packr/releases/download/$PACKR_VERSION/$PACKR_EXE_NAME"
PACKR_WORK_DIR="packr_work"
# use the following for the latest Linux version: https://api.adoptium.net/v3/info/release_versions?architecture=x64&heap_size=normal&image_type=jre&jvm_impl=hotspot&lts=true&os=linux&page=0&page_size=10&project=jdk&release_type=ga&sort_method=DATE&sort_order=DESC&vendor=eclipse&version=%5B8%2C9%5D
PACKR_BUNDLE_JRE_VERSION_LINUX="8u362-b09"
PACKR_BUNDLE_JRE_VERSION_WINDOWS="8u352-b08"
# "OS_HERE" is a placeholder for the os name
PACKR_JRE_CACHE_DIR="jre_cache_$PACKR_BUNDLE_JRE_VERSION"_OS_HERE
# probably need to delete jre cache if changing this
PACKR_JDK_BUNDLE_DL_LINK_LINUX="https://api.adoptium.net/v3/binary/version/jdk$PACKR_BUNDLE_JRE_VERSION_LINUX/linux/x64/jre/hotspot/normal/eclipse?project=jdk"
PACKR_JDK_BUNDLE_DL_LINK_WINDOWS="https://api.adoptium.net/v3/binary/version/jdk$PACKR_BUNDLE_JRE_VERSION_WINDOWS/windows/x64/jre/hotspot/normal/eclipse?project=jdk"
PACKR_PLATFORMS="LINUX WINDOWS"
SCRIPT_DIR=$(dirname "$0")

# for debugging
#set -x

cd "$SCRIPT_DIR"
echo "Cleaning project."
./gradlew clean
rm -rf "$PACKR_WORK_DIR"/$BINARY_ARTIFACT_NAME-*
rm -rf "$RELEASE_DIR"/*

echo "Generating Desktop and Android releases."
# --rerun-tasks to make sure version number changes are reflected
./gradlew --rerun-tasks desktop:dist android:assembleRelease android:bundle

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

mkdir -p "$PACKR_WORK_DIR"
cd "$PACKR_WORK_DIR"
if test -f "$PACKR_EXE_NAME"; then
    echo "Using existing Pakr executable $PACKR_EXE_NAME."
else
    echo "Downloading Packr from $PACKR_DL_LINK"
    wget "$PACKR_DL_LINK"
fi

for platform in $PACKR_PLATFORMS
do
    echo "Running Packr to generate bundle for platform $platform."
    mkdir -p "${PACKR_JRE_CACHE_DIR/OS_HERE/${platform}}"
    target_dir="$BINARY_ARTIFACT_NAME-$platform"
    jre_link=PACKR_JDK_BUNDLE_DL_LINK_$platform
    # minimizejre hard causes an exception on startup
    java -jar ./$PACKR_EXE_NAME --platform ${platform}64 --output $target_dir --jdk ${!jre_link} --cachejre ${PACKR_JRE_CACHE_DIR/OS_HERE/${platform}} --executable $BINARY_ARTIFACT_NAME --classpath ../desktop/build/libs/*.jar --mainclass de.sesu8642.feudaltactics.desktop.DesktopLauncher --minimizejre soft
    cd "$target_dir"
    # use lower-case for platform in file name
    zip -r - ./* > "../$BINARY_ARTIFACT_NAME-${platform,,}.zip"
    cd ..
done
cd ..

echo "Copying release artifacts to $RELEASE_DIR."
mkdir -p "$RELEASE_DIR"
cp desktop/build/libs/*.jar "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-desktop.jar
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
cp "$PACKR_WORK_DIR"/*.zip "$RELEASE_DIR"
