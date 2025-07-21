#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: $0 <mod_version>"
    exit 1
fi

MOD_VERSION="$1"

# ====== Supported Minecraft versions ======
MC_VERSIONS=(
  1.20 1.20.1 1.20.2 1.20.3 1.20.4 1.20.5 1.20.6
  1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8
)

# ====== Version mapping for each MC version ======
declare -A YARN
YARN[1.20]="1.20.1+build.1"
YARN[1.20.1]="1.20.1+build.1"
YARN[1.20.2]="1.20.2+build.1"
YARN[1.20.3]="1.20.3+build.1"
YARN[1.20.4]="1.20.4+build.1"
YARN[1.20.5]="1.20.5+build.1"
YARN[1.20.6]="1.20.6+build.1"
YARN[1.21]="1.21+build.9"
YARN[1.21.1]="1.21.1+build.1"
YARN[1.21.2]="1.21.2+build.1"
YARN[1.21.3]="1.21.3+build.1"
YARN[1.21.4]="1.21.4+build.1"
YARN[1.21.5]="1.21.5+build.1"
YARN[1.21.6]="1.21.6+build.1"
YARN[1.21.7]="1.21.7+build.1"
YARN[1.21.8]="1.21.8+build.1"

FABRIC_API="0.83.0+1.20"

declare -A LOADER
LOADER[1.20]="0.14.21"
LOADER[1.20.1]="0.14.21"
LOADER[1.20.2]="0.14.21"
LOADER[1.20.3]="0.14.21"
LOADER[1.20.4]="0.15.10"
LOADER[1.20.5]="0.15.10"
LOADER[1.20.6]="0.15.10"
LOADER[1.21]="0.16.14"
LOADER[1.21.1]="0.16.14"
LOADER[1.21.2]="0.16.14"
LOADER[1.21.3]="0.16.14"
LOADER[1.21.4]="0.16.14"
LOADER[1.21.5]="0.16.14"
LOADER[1.21.6]="0.16.14"
LOADER[1.21.7]="0.16.14"
LOADER[1.21.8]="0.16.14"

# ====== Batch build for all versions ======
for V in "${MC_VERSIONS[@]}"; do
    echo "Building for MC $V"
    # Use src2 for 1.20 and 1.20.1, use src3 for 1.20.2 and above
    if [[ "$V" == "1.20" || "$V" == "1.20.1" ]]; then
        rm -rf src
        cp -r src2 src
    else
        rm -rf src
        cp -r src3 src
    fi

    # Generate gradle.properties for this version
    cat > gradle.properties <<EOF
org.gradle.jvmargs=-Xmx1G
minecraft_version=$V
yarn_mappings=${YARN[$V]}
loader_version=${LOADER[$V]}
fabric_version=$FABRIC_API
mod_version=$MOD_VERSION
maven_group=byd.cxkcxkckx
archives_base_name=gotome
EOF

    ./gradlew clean build

    # Rename and copy output jars
    if [ ! -d output ]; then mkdir output; fi
    for f in build/libs/gotome-*.jar; do
        if [[ "$f" != *sources* ]]; then
            mv "$f" "build/libs/gotome-$MOD_VERSION-$V.jar"
            cp "build/libs/gotome-$MOD_VERSION-$V.jar" output/
        fi
    done
done

echo "All builds finished!"