#!/bin/sh
#rm -r certs
#mkdir certs
#cd certs
#
### 1. Create a X.509 CA certificate using OpenSSL. Make sure to save the private key of the CA in a file.
#
#echo "=== Generating a private key for CA ...\n"
#
## Create private key for ca.
#openssl genrsa -out fake_priv_ca.key -des 2048
#
#echo "=== Creating a certificate request using the private key for CA ...\n"
#
## Create a certificate request with the private key for ca.
#openssl req -new -key fake_priv_ca.key -x509 -days 365 -out fake_ca.crt

## 4. Use keytool to create a user keypair that is stored in a keystore.

NAME="test"
ID="test"

echo "=== Using keytool to create a user keypair that is stored in keystore.jks ...\n"

keytool \
 -genkeypair \
 -alias $NAME \
 -keysize 1024 \
 -keystore "${ID}.jks" \
 -storepass eit060 \
 -keypass  eit060 \
 -dname "CN=${ID}, OU=DSEK, O=LTH, L=Lund, ST=Skane, C=SE"

## 5. Use keytool to create a CSR for the keys created in the previous step.

echo "=== Using keytool to create a Certificate Signing Request (CSR) ...\n"

keytool \
 -certreq \
 -alias $NAME \
 -storepass eit060 \
 -keypass eit060 \
 -keystore "${ID}.jks" \
 -file "${ID}.csr"

## 6. Use OpenSSL to sign the CSR with the CA created in the first step.

echo "=== Using OpenSSL to sign the CSR with the CA ...\n"

openssl x509 \
 -req \
 -days 365 \
 -in "${ID}.csr" \
 -CA ca.crt \
 -CAkey priv_ca.key \
 -out "${ID}.crt" \
 -CAcreateserial \
 -CAserial "${ID}.seq"

## 7. Import the certificate chain into your keystore.

echo "=== Importing CA to keystore ...\n"

# import ca
keytool \
 -importcert \
 -alias ca \
 -file ca.crt \
 -keystore "${ID}.jks" \
 -keypass eit060 \
 -storepass eit060

echo "=== Importing client certificate keychain to keystore ...\n"

# import client
keytool \
 -importcert \
 -alias $NAME \
 -file "${ID}.crt" \
 -keystore "${ID}.jks" \
 -storepass eit060 \
 -keypass eit060
