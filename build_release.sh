#!/bin/bash
RELEASE_DIR="release"
BINARY_ARTIFACT_NAME="FeudalTactics"
PACKR_VERSION=4.0.0
PACKR_EXE_NAME="packr-all-$PACKR_VERSION.jar"
PACKR_DL_LINK="https://github.com/libgdx/packr/releases/download/$PACKR_VERSION/$PACKR_EXE_NAME"
PACKR_WORK_DIR="packr_work"
PACKR_BUNDLE_JRE_VERSION="8u332-b09"
PACKR_JRE_CACHE_DIR="jre_cache_$PACKR_BUNDLE_JRE_VERSION"
# probably need to delete jre cache if changing this
PACKR_JDK_BUNDLE_DL_LINK="https://github.com/adoptium/temurin8-binaries/releases/download/jdk$PACKR_BUNDLE_JRE_VERSION/OpenJDK8U-jdk_x64_linux_hotspot_${PACKR_BUNDLE_JRE_VERSION/-/}.tar.gz"
# minimizejre hard causes an exception on startup
PACKR_MAIN_ARGS="--jdk $PACKR_JDK_BUNDLE_DL_LINK --cachejre $PACKR_JRE_CACHE_DIR --executable $BINARY_ARTIFACT_NAME --classpath ../desktop/build/libs/*.jar --mainclass com.sesu8642.feudaltactics.desktop.DesktopLauncher --minimizejre soft"
PACKR_PLATFORMS="linux64 windows64"
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
./gradlew --rerun-tasks desktop:dist android:assembleRelease

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
    mkdir -p "$PACKR_JRE_CACHE_DIR"
    target_dir="$BINARY_ARTIFACT_NAME-$platform"
    java -jar ./$PACKR_EXE_NAME --platform $platform --output $target_dir $PACKR_MAIN_ARGS
    cd "$target_dir"
    zip -r - ./* > "../$target_dir.zip"
    cd ..
done
cd ..

echo "Copying release artifacts to $RELEASE_DIR."
mkdir -p "$RELEASE_DIR"
cp desktop/build/libs/*.jar "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-desktop.jar
cp android/build/outputs/apk/release/*.apk "$RELEASE_DIR"/$BINARY_ARTIFACT_NAME-android.apk
cp "$PACKR_WORK_DIR"/*.zip "$RELEASE_DIR"
