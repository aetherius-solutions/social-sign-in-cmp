package com.cmpmodular.socialSignIn

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.LocalUIViewController
import cmpmodular.socialsignin.generated.resources.Res
import cmpmodular.socialsignin.generated.resources.ic_apple
import cmpmodular.socialsignin.generated.resources.ic_google
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.GoogleSignIn.GIDSignInResult
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
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.posix.memcpy

@Composable
actual fun GoogleLoginButton(
    webClientId: String,
    backgroundColor: Color?,
    drawableResource: DrawableResource?,
    onResponse: (AuthResponse) -> Unit,
    modifier: Modifier
) {
    val uiViewController = LocalUIViewController.current

    GoogleButtonUI(
        backgroundColor = backgroundColor ?: Color.White,
        drawableResource = drawableResource?: Res.drawable.ic_google,
        modifier = modifier,
        onClick = { googleLogin(uiViewController, onResponse) }
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun googleLogin(
    uiViewController: UIViewController,
    onLoggedIn: (AuthResponse) -> Unit
) {
    GIDSignIn.sharedInstance.signInWithPresentingViewController(uiViewController) { result, error ->
        when {
            result != null -> {
                result.user.refreshTokensIfNeededWithCompletion { user, nsError ->
                    user?.idToken?.let { token->
                        onLoggedIn(AuthResponse.Success(token.tokenString))
                    }
                }
            }
            error != null -> onLoggedIn(AuthResponse.Error(error.fullErrorMessage))
            else -> onLoggedIn(AuthResponse.Cancelled)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private val GIDSignInResult.toGoogleAccount: GoogleAccount
    get() = GoogleAccount(
        idToken = user.idToken?.tokenString.orEmpty(),
        accessToken = user.accessToken.tokenString,
        profile = Profile(
            name = user.profile?.name.orEmpty(),
            familyName = user.profile?.familyName.orEmpty(),
            givenName = user.profile?.givenName.orEmpty(),
            email = user.profile?.email.orEmpty(),
            picture = user.profile?.imageURLWithDimension(100u)?.absoluteString
        ),
    )

private val NSError.fullErrorMessage: String
    get() {
        val underlyingErrors = underlyingErrors.joinToString(", ") { it.toString() }
        val recoveryOptions = localizedRecoveryOptions?.joinToString(", ") { it.toString() }

        return listOfNotNull(
            "code: $code",
            domain?.let { "domain: $domain" },
            "description: $localizedDescription",
            localizedFailureReason?.let { "reason: $localizedFailureReason" },
            localizedRecoverySuggestion?.let { "suggestion: $localizedRecoverySuggestion" },
            "underlyingErrors: $underlyingErrors",
            "recoveryOptions: $recoveryOptions".takeIf { recoveryOptions != null },
        ).joinToString("\n")
    }

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
                            onResponse.invoke(AuthResponse.Success(res))
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