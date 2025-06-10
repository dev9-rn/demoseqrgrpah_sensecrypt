package com.sssl.seqrgraphdemo.tlvdecoder;


import com.sssl.seqrgraphdemo.utils.SignatureVerifyException;

public interface ITLVDecoder {
    TLVDecodeResult decode(byte[] tlvEncodedData,String jwsJson) throws TLVDecodeException, IllegalArgumentException, SignatureVerifyException;
}
