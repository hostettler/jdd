#!/usr/bin/env bash

set -e

echo "GPG Step 1 - Create random passphrase"
# create a random passphrase
export GPG_PASSPHRASE=$(echo "$RANDOM$(date)" | md5sum | cut -d\  -f1)

echo "GPG Step 2 - Create key configuration"
# configuration to generate gpg keys
cat >gen-key-script <<EOF
    %echo Generating a basic OpenPGP key
    Key-Type: RSA
    Key-Length: 4096
    Subkey-Type: 1
    Subkey-Length: 4096
    Name-Real: Opensource Steve Hostettler
    Name-Email: steve.hostettler@hostettler.net
    Expire-Date: 2y
    Passphrase: ${GPG_PASSPHRASE}
    %commit
    %echo done
EOF

echo "GPG Step 3 - Create key"
# create a local keypair with given configuration
gpg --batch --gen-key gen-key-script


gpg -K

# export created GPG key
#
# example output
# ssb   4096R/CC1613B2 2016-09-08
# ssb   4096R/55B7CAA2 2016-09-08
export GPG_KEYNAME=$(gpg -K | grep ^sec | cut -d/  -f2 | cut -d\  -f1 | head -n1)
echo "GPG_KEYNAME = ${GPG_KEYNAME}"

# cleanup local configuration
echo "GPG Step 4 - clean keys"
shred gen-key-script

# publish the gpg key
# (use keyserver.ubuntu.com as travis request keys from this server, 
#  we avoid synchronization issues, while releasing)
echo "GPG Step 5 - Publish keys" 
gpg --keyserver hkp:keyserver.ubuntu.com --send-keys ${GPG_KEYNAME}
gpg --keyserver hkp:keys.openpgp.org --send-keys ${GPG_KEYNAME}
gpg --keyserver hkp:keys.gnupg.net --send-keys ${GPG_KEYNAME}
gpg --keyserver hkp:pool.sks-keyservers.net --send-keys ${GPG_KEYNAME}


# wait for the key beeing accessible
echo "GPG Step 6 - Wait for the key to be published"
while(true); do
  date
  echo "Call keyserver to check for key readiness"
  gpg --keyserver keyserver.ubuntu.com  --recv-keys ${GPG_KEYNAME} && break || sleep 30
  gpg --keyserver keys.openpgp.org  --recv-keys ${GPG_KEYNAME} && break || sleep 30
  gpg --keyserver keys.gnupg.net  --recv-keys ${GPG_KEYNAME} && break || sleep 30
  gpg --keyserver pool.sks-keyservers.net  --recv-keys ${GPG_KEYNAME} && break || sleep 30
done

echo "GPG Step 7 - wait for 2minutes to let the key being synced"
sleep 60

echo "GPG Step 8 - end of script"