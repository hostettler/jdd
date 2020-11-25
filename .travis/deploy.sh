#!/usr/bin/env bash

set -e

# only do deployment, when travis detects a new tag
if [ ! -z "$TRAVIS_TAG" ]
then
    echo "Deploy Step 1 - on a tag -> set pom.xml <version> to $TRAVIS_TAG"
    mvn --settings .travis/settings.xml org.codehaus.mojo:versions-maven-plugin:2.3:set -DnewVersion=$TRAVIS_TAG -Prelease

	echo "Step 2 : delete definitly ~/.gnupg"
    if [ ! -z "$TRAVIS" -a -f "$HOME/.gnupg" ]; then
        find ~/.gnupg/ -type f -exec shred -f -v {} \;
        rm -rf ~/.gnupg
    fi

	echo "Deploy Step 3 - Import GPG key"
    #source .travis/gpg.sh
    echo $GPG_SECRET_KEYS | base64 --decode | gpg --import
    echo $GPG_OWNERTRUST | base64 --decode | gpg --import-ownertrust

	echo "Deploy Step 4 - Do the deploy"
    mvn clean deploy --settings .travis/settings.xml -DskipTests=true --batch-mode --update-snapshots -Prelease

	echo "Deploy Step 5 - delete definitly ~/.gnupg"
    if [ ! -z "$TRAVIS" ]; then
        find ~/.gnupg/ -type f -exec shred -f -v {} \; 
        rm -rf ~/.gnupg
    fi
else
    echo "not on a tag -> keep snapshot version in pom.xml"
fi