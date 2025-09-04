#!/usr/bin/env bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

feature_name=$1
if [[ -z $feature_name ]]; then
  echo "Missing feature name from the command line parameters."
  exit 1
fi

feature_pack_tarball=${DIR}/${feature_name}/patches.tgz

echo "Applying code changes..."
pushd $DIR/..
# --no-xattrs ignores extended attributes for better cross-platform compatibility
tar --no-xattrs -xvf ${feature_pack_tarball}
popd

echo "Completed."
