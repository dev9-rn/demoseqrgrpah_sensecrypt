package com.sssl.seqrgraphdemo.networks;

import com.sssl.seqrgraphdemo.models.login.ForgotPasswordRequest;
import com.sssl.seqrgraphdemo.models.login.ForgotPasswordResponse;
import com.sssl.seqrgraphdemo.models.login.InstituteLoginRequest;
import com.sssl.seqrgraphdemo.models.login.LoginRequest;
import com.sssl.seqrgraphdemo.models.login.LoginResponse;
import com.sssl.seqrgraphdemo.models.login.LogoutResponse;
import com.sssl.seqrgraphdemo.models.scanHistory.VerifierScanHistoryRequest;
import com.sssl.seqrgraphdemo.models.scanHistory.VerifierScanHistoryResponse;
import com.sssl.seqrgraphdemo.models.scanview.InstituteAuditScanResponse;
import com.sssl.seqrgraphdemo.models.scanview.ViewCertificateRequest;
import com.sssl.seqrgraphdemo.models.scanview.ViewCertificateResponse;
import com.sssl.seqrgraphdemo.models.verifierRegister.VerifierRegisterResponse;
import com.sssl.seqrgraphdemo.models.verifierRegister.VerifierSignUpRequest;
import com.sssl.seqrgraphdemo.models.verifyOtp.ResenOtpResponse;
import com.sssl.seqrgraphdemo.models.verifyOtp.ResendOtpRequest;
import com.sssl.seqrgraphdemo.models.verifyOtp.VerifyOtpRequest;
import com.sssl.seqrgraphdemo.models.verifyOtp.VerifyOtpResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.HeaderMap;

import retrofit2.http.POST;

public interface SeqrCodeService {

    @POST("/api/user-login")
    Call<LoginResponse> verifierLogin(@HeaderMap Map<String , String> headers , @Body LoginRequest body);

    @POST("/api/delete-user-acc")
    Call<LoginResponse> removeVerifierAccount(@HeaderMap Map<String , String> headers , @Body LoginRequest body);

    @POST("/api/passwordReset")
    Call<ForgotPasswordResponse> forgotVerifierPassword(@HeaderMap Map<String , String> headers , @Body ForgotPasswordRequest body);

    @POST("/api/institute-login")
    Call<LoginResponse> instituteLogin(@HeaderMap Map<String , String> headers , @Body InstituteLoginRequest body);

    @POST("/api/nidan/scan-certificate")
    Call<ViewCertificateResponse> instituteScanViewCertificate(@HeaderMap Map<String , String> headers , @Body ViewCertificateRequest body);

    @POST("/api/scan")
    Call<ViewCertificateResponse> verifierScanViewCertificate(@HeaderMap Map<String , String> headers , @Body ViewCertificateRequest body);

    @POST("/api/scan-audit-trail")
    Call<InstituteAuditScanResponse> instituteBarCodeViewDetails(@HeaderMap Map<String , String> headers , @Body ViewCertificateRequest body);

    @POST("/api/logout")
    Call<LogoutResponse> logout(@HeaderMap Map<String , String> headers);

    @POST("/api/scan-history")
    Call<VerifierScanHistoryResponse> getVerifierScanHistory(@HeaderMap Map<String, String> headers , @Body VerifierScanHistoryRequest body);

    @POST("/api/user-register")
    Call<VerifierRegisterResponse> registerVerifier(@HeaderMap Map<String, String> headers , @Body VerifierSignUpRequest body);

    @POST("/api/mobile-no-verify")
    Call<VerifyOtpResponse> verifyOtp(@HeaderMap Map<String, String> headers , @Body VerifyOtpRequest body);

    @POST("/api/resend-otp")
    Call<ResenOtpResponse> resendOtp(@HeaderMap Map<String, String> headers , @Body ResendOtpRequest body);
}
