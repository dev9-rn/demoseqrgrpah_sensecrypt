package com.sssl.seqrgraphdemo.utils;

import android.util.Log;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.util.Base64URL;

import java.security.interfaces.ECPublicKey;
import java.text.ParseException;

public class SignatureVerifier {


    private SignatureVerifier() {

    }

    public static int getSignatureLength(int keyId, String json) throws ParseException, SignatureVerifyException {


        JWKSet jwkSet = JWKSet.parse(json);

        JWK jwk = jwkSet.getKeyByKeyId(String.valueOf(keyId));


        if (jwk != null) {

            String algorithm = jwk.getAlgorithm().toString();


            if (algorithm.equals("ES256") || algorithm.equals("ES384")
                    || algorithm.equals("ES512")) {


                return ((jwk.size() / 8) * 2);


            } else if (algorithm.equals("RS256") || algorithm.equals("RS284")
                    || algorithm.equals("RS512")) {
                return (jwk.size() / 8);

            } else {

                //throw not supported exception
                throw new SignatureVerifyException("Alg " + jwk.getAlgorithm() + " not supported");
            }

        } else {

            //throw key with id not found
            throw new SignatureVerifyException("key with id " + keyId + " not found");
        }


    }


    public static boolean verifySignature(String jwsJson, int keyId, byte[] signedContent, byte[] signature) throws JOSEException, ParseException, SignatureVerifyException {


        JWKSet jwkSet = JWKSet.parse(jwsJson);

        JWK jwk = jwkSet.getKeyByKeyId(String.valueOf(keyId));

        Log.d("TAG","key id : "+keyId);


        if (jwk != null) {

            String algorithm = jwk.getAlgorithm().toString();

            Log.d("TAG","algorithm : "+algorithm);

            JWSHeader.Builder builder = new JWSHeader.Builder(JWSAlgorithm.parse(jwk.getAlgorithm().toString()));
            builder.jwk(jwk);

            JWSHeader header = builder.build();

            JWSVerifier verifier;


            if (algorithm.equals("ES256") || algorithm.equals("ES384")
                    || algorithm.equals("ES512")) {


                ECPublicKey publicKey = jwk.toECKey().toECPublicKey();

                verifier = new ECDSAVerifier(publicKey);


            } else if (algorithm.equals("RS256") || algorithm.equals("RS284")
                    || algorithm.equals("RS512")) {


                verifier = new RSASSAVerifier(jwk.toRSAKey().toPublicJWK());


            } else {

                //throw not supported exception
                throw new SignatureVerifyException("Alg " + jwk.getAlgorithm() + " not supported");
            }


            return verifier.verify(header, signedContent, Base64URL.encode(signature));

            

        } else {

            //throw key with id not found
            throw new SignatureVerifyException("key with id " + keyId + " not found");
        }


    }
}
