package com.strand.finaid.data.network.module

import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.strand.finaid.R
import com.strand.finaid.data.network.AccountService
import com.strand.finaid.data.network.LogService
import com.strand.finaid.data.network.impl.AccountServiceImpl
import com.strand.finaid.data.network.impl.LogServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
class ServiceModule {
    @Provides
    fun provideContext(
        app: Application
    ): Context = app.applicationContext

    @Provides
    fun provideLogService(impl: LogServiceImpl): LogService = impl

    @Provides
    fun provideAccountService(
        oneTapClient: SignInClient,
        @Named("SignInRequest") signInRequest: BeginSignInRequest,
        @Named("SignUpRequest") signUpRequest: BeginSignInRequest,
        signInClient: GoogleSignInClient
    ): AccountService = AccountServiceImpl(
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        signInClient = signInClient
    )

    @Provides
    fun provideOneTapClient(
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Named("SignInRequest")
    fun provideSignInRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build())
        .setAutoSelectEnabled(true)
        .build()

    @Provides
    @Named("SignUpRequest")
    fun provideSignUpRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
        .build()


    @Provides
    fun provideGoogleSignInOptions(
        app: Application
    ) = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.web_client_id))
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)


}