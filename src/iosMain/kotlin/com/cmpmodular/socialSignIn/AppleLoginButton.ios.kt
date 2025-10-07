package com.cmpmodular.socialSignIn

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cmpmodular.socialsignin.generated.resources.Res
import cmpmodular.socialsignin.generated.resources.ic_apple
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.compose.resources.DrawableResource
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun AppleLoginButton(
    backgroundColor: Color?,
    drawableResource: DrawableResource?,
    modifier: Modifier,
    onResponse: (AuthResponse) -> Unit
) {
    AppleButtonUI(
        backgroundColor = backgroundColor?: Color.White,
        drawableResource = drawableResource ?: Res.drawable.ic_apple,
        onClick = {
            SignInWithApple().signIn(
                onResult = { result ->
                    if (result.isSuccess) {
                        result.getOrNull()?.let { res ->
                            onResponse.invoke(AuthResponse.Success(res, null))
                        }

                    } else {
                        result.getOrNull()?.let { AuthResponse.Error(it) }?.let {
                            onResponse.invoke(it)
                        }
                    }

                }
            )
        }
    )
}

class SignInWithApple : NSObject(), ASAuthorizationControllerDelegateProtocol,
    ASAuthorizationControllerPresentationContextProvidingProtocol {

    private var callback: ((Result<String>) -> Unit)? = null

    fun signIn(onResult: (Result<String>) -> Unit) {
        this.callback = onResult

        val request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)

        val authorizationController = ASAuthorizationController(listOf(request))
        authorizationController.delegate = this
        authorizationController.presentationContextProvider = this
        authorizationController.performRequests()
    }

    @ObjCAction
    override fun authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization: ASAuthorization) {
        val appleIDCredential = didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential
        val identityToken = appleIDCredential?.identityToken

        val token = identityToken?.toByteArray()?.decodeToString()
        token?.let {
            callback?.invoke(Result.success(token))
        } ?: run {
            callback?.invoke(Result.failure(Exception("Invalid token")))
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun NSData.toByteArray(): ByteArray = ByteArray(this@toByteArray.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toByteArray.bytes, this@toByteArray.length)
        }
    }

    @ObjCAction
    override fun authorizationController(controller: ASAuthorizationController, didCompleteWithError: NSError) {
        callback?.invoke(Result.failure(Exception(didCompleteWithError.localizedDescription)))
    }

    override fun presentationAnchorForAuthorizationController(controller: ASAuthorizationController): UIWindow {
        return UIApplication.sharedApplication.keyWindow!!
    }
}
