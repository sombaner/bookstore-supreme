#!/usr/bin/env bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function help() {
  echo $1
  echo ""
  echo "create_patch_set.sh <patch-set-name> <directory>"
  echo ""
  exit 1
}

patch_set_name=$1
if [[ -z $patch_set_name ]]; then
  help "Missing 'patch-set-name' parameter"
fi

sources=$2
if [[ -z $sources ]]; then
  help "Missing patch set directory"
else
  if [[ -d $sources ]]; then
    echo "Using '${sources}' as the root of patch set file"
  else
    help "Specified patch set directory '${sources}' is not a valid directory"
  fi
fi

if [[ ! -d ${DIR}/${patch_set_name} ]]; then
  mkdir -p ${DIR}/${patch_set_name}
fi

echo ""
echo "Building patch set: ${patch_set_name}"
echo "----------------------------------------------------"
echo ""
pushd $(dirname ${sources}) > /dev/null
# COPYFILE_DISABLE prevents creation of ._* files on macOS
# --no-xattrs prevents extended attributes from being stored in the archive for cross-platform compatibility (e.g extended header 'LIBARCHIVE.xattr.com.apple.provenance')
COPYFILE_DISABLE=1 tar --no-xattrs -cvpzf ${DIR}/${patch_set_name}/patches.tgz $(basename ${sources})
popd > /dev/null
echo "done."
